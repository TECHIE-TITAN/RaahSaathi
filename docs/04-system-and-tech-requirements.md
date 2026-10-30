# 04 — System, Model and Library Requirements (Free-Only Stack)

**Rule (ADR-011):** the baseline product uses only components that cost nothing to use. Anything paid, credit-limited or with unclear commercial terms is **not in the baseline**; it may appear only as an *optional* adapter, clearly labelled.

**Hosting is out of scope for now.** During development the backend runs locally (Docker on a developer machine). Hosting, scaling and uptime targets will be added later.

## 0. How to read the status column

| Status | Meaning |
|---|---|
| **Free (checked)** | Confirmed free in a web check on 2026-10-30 (source in `THIRD_PARTY.md`) |
| **Free (verify)** | Believed free/open source from memory; not yet confirmed. Check licence before relying on it |
| **Conditional** | Free only within limits or only for non-commercial/prototype use |
| **Excluded** | Not free; not used in the baseline |

## 1. Target devices

| Item | Minimum | Recommended |
|---|---|---|
| OS | Android 10 / API 29 (verify against chosen libraries) | Android 12+ |
| RAM | 3 GB | 4-6 GB |
| CPU/SoC | Mid-range 2020+ | 7-series class or better |
| Camera | Rear 8 MP, autofocus | 12 MP+, OIS, torch |
| Mic | One mic | Two mics (future direction work) |
| Storage free | 300 MB | 500 MB |
| Network | 3G for degraded modes, 4G normal | 4G/5G/Wi-Fi |
| Audio out | Phone speaker | Bluetooth or bone-conduction headset |
| Haptics | Vibration motor | Good haptic engine |

Platform notes:
- A microphone foreground service needs the matching foreground-service type and permissions on newer Android (e.g. type `microphone` on Android 14). (verify)
- Many OEMs kill background services; onboarding must guide battery-exemption and testing must cover those OEMs.
- Android 13+ needs the runtime notification permission for the persistent notification.

## 2. Development environment (all free)

| Tool | Status |
|---|---|
| Android Studio, Android SDK, emulator | Free (verify) |
| JDK 17 (OpenJDK) | Free (verify) |
| Python 3.11+, pip/uv | Free (verify) |
| Docker (Docker Desktop has licence terms for larger companies; Colima/Podman are alternatives) | Free for personal/small use (verify) |
| Git and GitHub (free tier, Actions free minutes are limited) | Conditional |
| Two or more physical Android test devices | Hardware you already own or borrow |

## 3. Baseline free stack at a glance

| Capability | Baseline (free) | Optional / fallback | Excluded (not free) |
|---|---|---|---|
| Wake phrase | **None in baseline.** Push-to-talk and hardware keys (ADR-005). Wake phrase from a self-trained model under a permissive licence in Phase 3 | `openWakeWord` code + own training data; `livekit-wakeword` (Apache 2.0, as found); `openwakeword-trainer` (MIT, as found) | Picovoice Porcupine; openWakeWord *pre-trained* models (CC BY-NC-SA) |
| VAD | Silero VAD or WebRTC VAD | | |
| Sound classification | YAMNet (Apache 2.0 code/weights; AudioSet labels CC BY 4.0, attribution required) | Other open models after licence check | |
| Image quality gate | OpenCV | | |
| OCR, Latin + Devanagari | ML Kit Text Recognition v2 on-device | Tesseract (Apache 2.0, verify) | |
| OCR, other Indic scripts | Bhashini OCR | Tesseract Indic data (weaker on scene text, verify) | Google Cloud Vision |
| ASR (incl. code-mixed) | Bhashini ASR | Android on-device `SpeechRecognizer` where a hi-IN etc. model is installed (verify); open models (AI4Bharat IndicConformer / IndicWhisper family, verify licence) for local dev | Sarvam beyond free credits |
| Translation | Bhashini NMT | AI4Bharat IndicTrans2 (verify licence) for local dev | Sarvam beyond free credits |
| TTS | Bhashini TTS | Android system TTS (offline, voice availability varies by device) | Sarvam beyond free credits |
| Intent understanding | Rule/keyword matcher over the fixed v1 intent set, run on-device or in the backend | Small open-weight language model run locally (licence check) | Paid LLM APIs |
| Scene description and "explain simpler" | **Degraded in baseline:** OCR-grounded template summaries; "explain simpler" via rules. Richer descriptions need a VLM (see 3.2) | Gemini API free tier (see 3.2); open-weight VLM run locally | Claude API and other paid VLMs |
| Nearby places | OpenStreetMap Overpass / Nominatim within fair-use limits | Offline OSM extracts (later) | Google Places API |

### 3.1 Bhashini — the main free language engine
- **Free for prototyping.** Third-party sources say production/charging users needs a separate agreement; **official terms not found**. Action: read the licence terms on the official Bhashini site and ask Bhashini/MeitY in writing before any pilot beyond a demo.
- Beware `bhashini.ai` / `bhashiniservices.com`: reported as a **separate private vendor** with paid plans.
- Onboarding reported as via the ULCA portal with API keys from the profile page; up to 5 keys per integrator (third-party report, verify).
- Because Bhashini is the single free engine for ASR/translation/TTS/OCR, graceful failure (FR-ERR-01) matters more: add on-device fallbacks (system TTS, `SpeechRecognizer`, ML Kit).

### 3.2 The vision-language model gap (honest limits)
Free VLM options are weak or unstable:
- **Gemini API free tier:** per-model free limits were reported as *not published in public docs* (shown only in Google AI Studio), and were reported to have been cut in Dec 2025. **Free-tier terms may allow Google to use submitted data to improve products (verify)**, which would conflict with FR-PRV-01/03 for photos of public scenes with bystanders. Use only after reading the current terms, and only for non-sensitive prototype tests.
- **Open-weight VLMs** (for example Qwen-VL or Gemma-class models): free to download, but need a GPU-capable machine to run (local dev only while hosting is out of scope), and each has its own licence (verify).
- **Design consequence:** the *core* "read this board" flow must not depend on a VLM: OCR (local + Bhashini) then translate then template sentence then TTS. The VLM only enriches "what is around me". In the free baseline, "what is around me" is limited to: OCR text found + nearby named places + simple on-device image labels (see 3.3).

### 3.3 Optional on-device image labelling (free)
ML Kit Image Labeling / Object Detection (on-device, base models) or a TFLite MobileNet/EfficientDet-Lite model (Apache 2.0, verify) for coarse "shop, vehicle, signboard, stairs" cues. Accuracy for Indian street scenes is limited; present as hints with uncertainty.

## 4. On-device models

| Purpose | Model | Runtime | Status |
|---|---|---|---|
| Sound events | **YAMNet** (521 AudioSet classes), map to ~10 app labels, fine-tune later on Indian recordings | TFLite/LiteRT | Free (checked): Apache 2.0 code/weights; AudioSet labels CC BY 4.0 (keep attribution) |
| Wake phrase (Phase 3) | Self-trained model via openWakeWord-style pipeline | ONNX/TFLite | Conditional: code licence verify; **pre-trained models are CC BY-NC-SA, so do not ship them**; trained models can inherit non-commercial terms from training data (e.g. ACAV100M is CC BY-NC-SA per a trainer repo), so use permissively licensed negatives and your own/permissive TTS voices |
| VAD | Silero VAD / WebRTC VAD | ONNX/native | Free (verify) |
| OCR | ML Kit Text Recognition v2 (Latin, Devanagari, bundled) | ML Kit | Free (verify pricing; Devanagari support checked) |
| Image quality | OpenCV | OpenCV Android | Free (verify) |
| Image labels (optional) | ML Kit / TFLite | TFLite | Free (verify) |
| Face/plate blur (P3) | ML Kit face detection + small detector | TFLite | Free (verify), plate model licence TBD |

Size budget: total on-device models <= 150 MB (NFR-PWR-03).

## 5. Cloud services used (free tiers only)

| Service | Use | Status | Constraint |
|---|---|---|---|
| Bhashini APIs | ASR, translation, TTS, OCR | Conditional | Prototype/PoC only until terms confirmed |
| OpenStreetMap Nominatim | Reverse geocode, place lookup | Conditional | Reported policy: max 1 request/s for the **whole app**, descriptive User-Agent, cache results, no bulk/autocomplete (check official policy) |
| OpenStreetMap Overpass | Nearby POIs | Conditional | Fair use; no official numeric limit found; keep queries small and cached |
| Gemini free tier (optional) | VLM enrichment | Conditional | Unpublished/volatile limits; data-use terms to check |
| Sarvam (optional, benchmarks only) | Compare code-mixed ASR in Phase 0 | Conditional | New accounts get about ₹100 credit (older ₹1,000 figure appears outdated); not for baseline |

Accounts to arrange: Bhashini/ULCA access; optional Gemini (AI Studio) key; optional Sarvam key for benchmarking within credits. No paid accounts.

## 6. Libraries (all free/open source; confirm each licence in `THIRD_PARTY.md`)

### 6.1 Android (Kotlin)
| Need | Library |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Camera | CameraX |
| ML runtime | TensorFlow Lite / LiteRT (or ONNX Runtime Mobile) |
| OCR | ML Kit Text Recognition (Latin, Devanagari) |
| Image ops | OpenCV Android |
| Audio | `AudioRecord` (Oboe optional) |
| Networking | OkHttp, Retrofit, kotlinx.serialization |
| DI/async | Hilt, Kotlin coroutines/Flow |
| Storage | Jetpack DataStore |
| Background | Foreground Service, WorkManager |
| System speech | `TextToSpeech`, `SpeechRecognizer` |
| Test | JUnit5, MockK, Turbine, Espresso / Compose UI test, Accessibility Test Framework |

### 6.2 Backend (Python, runs locally for now)
| Need | Library |
|---|---|
| API | FastAPI, uvicorn, pydantic v2 |
| HTTP client | httpx |
| Audio | `soundfile`, `pydub` + ffmpeg (check the ffmpeg build's licence: LGPL vs GPL) |
| Imaging | Pillow, OpenCV-python-headless |
| Text matching | RapidFuzz |
| Resilience | tenacity, own circuit breaker |
| Cache/limits | In-process cache and limiter (no managed Redis while hosting is out of scope) |
| Config | pydantic-settings, YAML routing table |
| Observability | structlog, prometheus-client |
| Test | pytest, pytest-asyncio, respx, hypothesis |
| Quality | ruff, mypy |

### 6.3 Evaluation tooling
`jiwer` (WER/CER), `librosa`/`torchaudio`, `scikit-learn`, `pandas`, `matplotlib`.

## 7. Datasets (free, check licences before commercial use)

| Task | Dataset | Notes |
|---|---|---|
| Sound events | AudioSet, ESC-50, UrbanSound8K | Baseline checks; verify each licence |
| Indian road sounds | **Self-collected** | Primary evaluation/fine-tune set; consent + anonymity |
| Indic ASR | IndicVoices, Kathbath, Vistaar (AI4Bharat) | Baselines per language; verify licence |
| Code-mixed | Self-recorded commands in noise | Intent-level tests |
| Indic scene text | IIIT-ILST, ICDAR MLT, self-captured boards | OCR baselines; verify licences |
| Wake phrase | Synthetic speech from permissively licensed TTS + real recordings | Do not use non-commercial datasets if the model will ship |

## 8. System requirements summary (dev machine)

- 16 GB RAM recommended (Android Studio + emulator + Docker); 8 GB workable with a physical device.
- 30 GB free disk (SDKs, emulator images, datasets).
- GPU only if running open-weight VLM/ASR models locally; otherwise not needed.
- Reliable internet for Bhashini and OSM calls during development.

## 9. Cost and quota drivers (cost is zero; quotas are the constraint)

Because nothing is paid, the limiting factors are **rate limits and fair-use rules**: Bhashini quotas/terms, Nominatim 1 req/s for the whole app, Gemini free-tier limits if used, Sarvam credits if used for benchmarks. Mitigations: on-device processing first, caching (places, repeated reads), small payloads, graceful degradation to on-device fallbacks, and a quota meter in the backend.

## 10. Deferred / excluded for now

- Hosting, scaling, uptime targets, managed cache, load balancers (deferred by decision).
- Distribution costs (e.g. a Play developer registration fee): pilot via side-loaded APK.
- All paid APIs: Claude API, Google Cloud Vision, Google Places, Picovoice, Sarvam beyond credits.
