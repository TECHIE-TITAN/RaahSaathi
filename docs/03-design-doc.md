# 03 — Design Document

Status: draft v0.1. Decisions referenced as ADR-nnn are in 08.

## 1. Overview

Android native client does capture, on-device inference (wake phrase, sound classification, image quality gate, basic OCR) and output. A thin stateless backend orchestrates cloud calls (ASR, translation, TTS, OCR for non-Devanagari scripts, vision-language model) behind provider adapters.

## 2. Architecture

```
                          ANDROID APP
 ┌───────────────────────────────────────────────────────────────┐
 │ Audio Service (foreground, mic)                               │
 │   Mic ─► ring buffer ─┬─► Wake-phrase detector (on-device)    │
 │                       └─► Sound classifier (on-device)        │
 │                                  │                            │
 │ Camera module ─► Quality gate ──┐│                            │
 │                  (on-device)    ││                            │
 │ On-device OCR (Latin/Devanagari)││                            │
 │                                 ▼▼                            │
 │            Orchestrator / Dialogue manager                    │
 │      (state machine, intent routing, retries, profile)        │
 │                    │                         ▲                │
 │                    ▼                         │                │
 │             Output router ◄─── Alert policy (priority/dedup)  │
 │        ┌──────────┼───────────┐                               │
 │      TTS player  Large-text UI  Haptics/Flash                 │
 └────────────────────┬──────────────────────────────────────────┘
                      │ HTTPS (JSON + media, TLS)
 ┌────────────────────▼──────────────────────────────────────────┐
 │ BACKEND (stateless)                                           │
 │  API gateway ─► Pipeline orchestrator                         │
 │     ├─ ASR adapter ──► Sarvam | Bhashini                      │
 │     ├─ Translate adapter ──► Sarvam | Bhashini                │
 │     ├─ OCR adapter ──► Bhashini OCR | cloud OCR               │
 │     ├─ VLM/LLM adapter ──► vision-language model              │
 │     ├─ TTS adapter ──► Sarvam | Bhashini                      │
 │     └─ Places adapter ──► OSM/Overpass | Places API           │
 │  Cache (short TTL) · rate limiter · metrics/logs (no media)   │
 └───────────────────────────────────────────────────────────────┘
```

Rationale: latency-critical and privacy-critical tasks stay on device; heavy language tasks go to the cloud; the backend holds all vendor keys.

## 3. Android client modules

| Module | Responsibility | Notes |
|---|---|---|
| `app-ui` | Onboarding, home, settings, alert cards | Jetpack Compose with semantics for TalkBack |
| `audio-service` | Foreground service owning the mic | Shared by wake phrase, sound classifier, command recording |
| `wake` | Wake-phrase detection | openWakeWord (or Porcupine) via on-device runtime |
| `sound` | Audio event classification, alert policy | YAMNet-class TFLite model plus custom thresholds |
| `camera` | CameraX capture, torch, optional preview analysis | Quality gate hooks |
| `quality` | Blur/exposure/glare/text-region checks | OpenCV plus ML Kit text boxes |
| `ocr-local` | On-device OCR | ML Kit Text Recognition for Latin and Devanagari |
| `dialogue` | State machine, intents, confirmations, retries | Pure Kotlin, unit-testable |
| `output` | Speech, text, haptic routing | Android TTS fallback plus streamed vendor TTS |
| `net` | API client, retry, offline queue | Retrofit/OkHttp |
| `profile` | Local profile and settings | DataStore |
| `telemetry` | Opt-in anonymous metrics | No media |

## 4. Key flows

### 4.1 Voice command (PTT or wake phrase)
1. Trigger (wake phrase / key / button). Earcon plus haptic.
2. Record to end-of-speech (VAD) or timeout.
3. `POST /v1/voice` with audio, profile languages. Backend: ASR then language ID.
4. If confidence low or action consequential: respond with a confirm prompt (TTS). Client waits for yes/no.
5. Intent resolved on the backend (LLM with a small tool schema) or locally for simple commands (stop, slower).
6. Execute the action (for example open camera), then respond via the output router.

### 4.2 Guided capture and read (core flow)
```
User: "Hey <App>, read this board"
  └► Camera opens, haptic "ready"
User captures ► Quality gate (on device)
   ├─ FAIL (blur/dark/cut-off) ► one short instruction ► await recapture (attempt++)
   │      └─ attempt == 3 ► proceed with best frame + caveat
   └─ PASS ► local OCR (Latin/Devanagari) or upload for cloud OCR
            ► merge with prior attempts' text (same subject)
            ► classify (shop/street/bus/notice) ► translate if needed
            ► LLM: build <=25-word spoken answer, mark low-confidence words
            ► TTS in user language ► output router
```

**Quality gate details**
- Blur: variance of Laplacian on a downscaled grayscale frame; threshold tuned per device.
- Exposure: mean luminance and clipped-pixel ratio; suggest torch if dark.
- Glare: ratio of near-saturated blobs overlapping text regions.
- Text presence: ML Kit text boxes; none found gives "I can't see any text, move closer or aim at the board".
- Cut-off: any text box with an edge within a few px of the frame edge gives a directional hint toward that edge.
- Distance: median text box height too small gives "move closer"; too large and cut gives "step back".
- Only one instruction per attempt; prioritise: dark, then cut-off, then blur, then distance.

**Multi-attempt merge**: keep recognised strings from each frame; fuzzy-match (edit distance) to join overlapping fragments; prefer higher-confidence tokens.

### 4.3 Sound alert
1. Audio ring buffer yields ~1 s windows, ~50% overlap, to classifier.
2. Classifier emits label scores; smoothing over 2-3 windows; apply per-label threshold.
3. Alert policy: priority table, dedup window (per label), cooldown, global rate cap; user per-label enable.
4. Output router: deaf profile gives card plus vibration pattern (unique per priority) plus optional flash; blind profile gives nothing automatic, entry stored for "what was that sound?".

### 4.4 Surroundings summary
Capture (with quality gate), optional coarse location, nearby named places from the OSM Places adapter, OCR text found in the frame, and optional on-device image labels. **Free baseline:** compose a short template-based summary from these ("Text reads X. Nearby: pharmacy, bus stop."). **If a VLM is enabled** (optional adapter; Gemini free tier or a local open-weight model, see 04 section 3.2): prompt for a short factual description with relative positions only when visible, no safety claims. Then TTS.

## 5. Provider adapter layer

```kotlin
// conceptually identical on backend (Python Protocols)
interface AsrProvider    { transcribe(audio, langHints): AsrResult }   // text, lang, confidence, words
interface TranslateProvider { translate(text, src, tgt): String }
interface OcrProvider    { read(image, scriptHints): OcrResult }       // blocks, text, conf
interface TtsProvider    { synthesize(text, lang, voice, rate): Audio }
interface VlmProvider    { describe(image, prompt, ctx): String }
```

- **Routing table** (config, not code): per language and per task, an ordered provider list. **Free baseline (ADR-011):** ASR: Bhashini then on-device `SpeechRecognizer`; Translate: Bhashini; TTS: Bhashini then Android system TTS; OCR: ML Kit (Latin/Devanagari) then Bhashini OCR for other scripts. Sarvam is an optional adapter used only within free credits for benchmarks. (Final assignments from the benchmark in 06.)
- **Failover:** retry once with jitter; on failure or timeout (per-task budgets) move to next provider; record outcome for metrics.
- **Circuit breaker** per provider to skip known-bad providers for a short period.
- **Normalisation:** each adapter returns a common result shape including confidence if available.

## 6. Dialogue manager

States: `Idle`, `Listening`, `Transcribing`, `Confirming`, `Capturing`, `Analysing`, `Speaking`, `Error`. Events: trigger, speech end, transcript, confirm yes/no, capture, gate pass/fail, response ready, user stop.

Rules:
- Any state accepts `stop` (PTT) and returns to `Idle`.
- `Confirming` times out after 6 s with one re-prompt, then cancels.
- Every transition to `Error` speaks a plain-language cause and a next step.
- Intent set is small and fixed in v1; unknown intents lead to "I can do X, Y, Z. Which one?".

## 7. Condition-aware output (profile and inference)

- Profile fields: `conditions[]`, `languages{primary,secondary}`, `speechRate`, `textScale`, `proactiveSpeech`, `hapticIntensity`, `flashAlerts`.
- **Router rules:** blind gives speech default, haptics for alerts, no unprompted verbosity. Deaf gives text/captions default, vibration for alerts. Elderly gives larger text, slower speech, one instruction per step. Multi-condition gives union.
- **Inference (P3):** signals such as repeated "slower", "explain simpler", screen never used, TalkBack on, or no audio output. Produces a suggestion that the user confirms. Never change profile silently.

## 8. Data model (client-local)

```
Profile(id, conditions[], primaryLang, secondaryLang, speechRate, textScale,
        proactiveSpeech, hapticIntensity, flashAlerts, consents{location,telemetry,feedbackExport})
SoundEvent(ts, label, score, priority, acknowledged)           // ring-limited, local
CaptureSession(id, ts, attempts[], mergedText, finalAnswerRef)  // memory-first; persisted only if user opts in
Feedback(ts, targetType, targetId, kind[wrong|missed|good], note?)
```
No server-side user data in v1. Backend logs hold request metadata only (no media).

## 9. Backend API (v1, JSON over HTTPS)

| Endpoint | Purpose | Request | Response |
|---|---|---|---|
| `POST /v1/voice` | ASR plus intent | audio (opus/wav), langHints, profile | transcript, lang, confidence, intent, confirm? |
| `POST /v1/read` | OCR plus translate plus summarise | image(s), prior fragments, target lang, mode | text, translated, spoken, lowConfWords[], needMore? |
| `POST /v1/describe` | Surroundings summary | image, coarse loc?, lang | spoken text |
| `POST /v1/simplify` | Explain simpler | text, lang, level | simpler text |
| `POST /v1/speak` | TTS | text, lang, voice, rate | audio stream |
| `GET /v1/health` | Liveness/provider status | | status |

Common: per-install anonymous token, request-id, idempotency key, versioned. Errors use a stable code set (`PROVIDER_UNAVAILABLE`, `LOW_QUALITY_INPUT`, `UNSUPPORTED_LANGUAGE`, `RATE_LIMITED`).

## 10. Prompting and safety rules for the VLM/LLM (applies only if a VLM/LLM adapter is enabled)

The free baseline replaces most LLM use with rules and templates (intent matcher, template summaries, rule-based "explain simpler"). The same invariants apply to templates and to any optional model.

System prompt invariants (tested in 06):
1. Describe only what is visible or given; say "I can't tell" when unsure.
2. No safety assurances (crossing, traffic, food allergens, medical).
3. Short sentences, at most 25 words by default.
4. Mark OCR uncertainty explicitly.
5. Keep code-mixed user phrasing; answer in the user's chosen output language.
6. Refuse to guess names of people from faces.

## 11. Offline behaviour

| Capability | Offline |
|---|---|
| Sound alerts | Yes |
| Wake phrase / PTT trigger | Yes |
| OCR Latin/Devanagari | Yes (read only, no translation) |
| Quality gate and hints | Yes |
| Basic commands (stop, slower, repeat) | Yes |
| ASR, translation, VLM, other scripts, TTS (vendor voices) | No; Android system TTS as fallback for supported locales |

Queue nothing involving media for later; speak current limits instead.

## 12. Performance budget (target, online)

| Step | Budget |
|---|---|
| Quality gate | 0.3 s |
| Upload (compressed 1 MP JPEG, typical 4G) | 0.5-1.0 s |
| OCR (local or cloud) | 0.5-1.5 s |
| Translate | 0.3-0.6 s |
| VLM/LLM | 1.0-2.0 s |
| TTS first audio | 0.5 s (stream) |
| **Total** | **~3.1-6.0 s** |

Tactics: downscale and JPEG-compress before upload, stream TTS, parallelise OCR and VLM when both are needed, small-model routing for simple reads.

## 13. Battery strategy

- Single foreground service sharing one audio capture.
- Sound classifier at ~1 Hz inference; wake model at its native frame rate with a lightweight pre-VAD gate.
- Use NNAPI/GPU delegates where they reduce power.
- Offer "listening schedule" and "pause listening" quick toggle.
- Measure on reference devices (see 06).

## 14. Observability

Backend: request counts, provider success/latency/failure, error codes, p50/p95. Client (opt-in): crash reports, anonymised timings, alert-feedback counts. No audio, images or transcripts are logged by default.

## 15. Technology choices (summary; see 04)

| Area | Choice | Alternative |
|---|---|---|
| Client | Kotlin, Jetpack Compose, CameraX | Flutter (weaker for foreground audio service) |
| On-device ML | TensorFlow Lite / LiteRT, ML Kit | ONNX Runtime Mobile |
| Backend | Python 3.11, FastAPI, httpx | Node/TypeScript |
| Hosting | **Deferred (out of scope for now); run locally with Docker** | To be decided later |
| Cache/limits | In-process cache and limiter | Redis when hosting is decided |

## 16. Extensibility

Planned seams: sound-direction (second mic / earbuds), name-call detection, navigation, iOS client sharing the same API, deafblind haptic/braille output.
