# 02 — Software Requirements Specification (SRS)

Version 0.1 (draft). Priority: M/S/C. Phase column uses **legacy labels** P1-P4, which map to **Phase 2-5** in the confirmed roadmap (Phase 0 is setup, Phase 1 is foundations and spikes; see the mapping table in 07).

## 1. Introduction

### 1.1 Purpose
Specify the behaviour and quality attributes of `Raahsaathi`, an Android application with a cloud backend that helps blind/low-vision and deaf/hard-of-hearing users perceive their surroundings.

### 1.2 Scope
In scope: the Android app, the backend orchestration service, and integration with speech, translation, OCR and vision-language providers. Out of scope: see Product Brief section 5.

### 1.3 Definitions
- **ASR/STT:** speech to text. **TTS:** text to speech. **OCR:** optical character recognition.
- **Wake phrase:** "Hey Raahsaathi". **Quality gate:** on-device check of a captured image.
- **Code-mixed:** speech mixing languages in one utterance (for example Hindi and English).
- **Provider adapter:** internal interface hiding a vendor API (Sarvam, Bhashini, etc.).

### 1.4 Assumptions and dependencies
- A1 Target devices are Android phones, API 29+ (verify minimum, see 04).
- A2 Users have intermittent mobile data; cloud features degrade gracefully offline.
- A3 Vendor APIs (Sarvam, Bhashini, VLM) remain available; adapters allow substitution.
- A4 Blind users can operate the phone with TalkBack or voice and will use a lanyard/chest mount or hold the phone.

## 2. Overall description

### 2.1 Product perspective
Mobile client (capture, on-device inference, output) plus stateless backend (orchestration, provider calls). No user accounts in v1; a local profile only.

### 2.2 User classes
Blind/low-vision (P1), deaf/hard-of-hearing (P2), older adult (P3). See Product Brief.

### 2.3 Operating environment
Android phone, rear camera, microphone, vibration motor, optional earbuds/bone-conduction headset, mobile or Wi-Fi network.

## 3. Functional requirements

### 3.1 Profile and onboarding (PRO)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-PRO-01 | On first run, offer a fully voice- and TalkBack-operable onboarding to choose condition(s), primary and secondary language, speech rate and text size. | M | P1 |
| FR-PRO-02 | Store the profile locally; allow changing it by voice or touch. | M | P1 |
| FR-PRO-03 | Suggest a profile change when behaviour indicates a mismatch (for example repeated "slower", "explain simpler", or screen-only use). The user must confirm any change. | S | P3 |
| FR-PRO-04 | Support multiple simultaneous conditions (for example low-vision and hard-of-hearing). | S | P2 |

### 3.2 Voice interaction (VOI)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-VOI-01 | Provide push-to-talk by on-screen large button, hardware key (volume double-press), and headset button. | M | P1 |
| FR-VOI-02 | Provide an on-device wake phrase "Hey Raahsaathi" while the foreground service is active, using a model with a licence that permits the intended use (NFR-COST-05). Push-to-talk remains available at all times. | M | P3 |
| FR-VOI-03 | On wake/PTT, play an earcon and haptic, then record until end-of-speech detected or timeout (default 8 s). | M | P1 |
| FR-VOI-04 | Transcribe user speech via Sarvam or Bhashini, supporting code-mixed utterances and automatic language detection constrained by the profile languages. | M | P1 |
| FR-VOI-05 | If ASR confidence is below threshold, or the intent is consequential, read back "Did you say ...?" and await confirmation. | M | P1 |
| FR-VOI-06 | Interpret intents at least: read this, what is around me, repeat, slower/faster, explain simpler, stop, change language, help. | M | P1 |
| FR-VOI-07 | Do not run wake-phrase detection while the app's TTS is playing (barge-in off by default), but allow "stop" via PTT. | M | P3 |
| FR-VOI-08 | The wake phrase must trigger no network traffic; audio is sent only after trigger. | M | P3 |

### 3.3 Guided camera capture (CAM)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-CAM-01 | Allow capture by voice command, large screen target, or hardware key, with an audible/haptic shutter confirmation. | M | P1 |
| FR-CAM-02 | After each capture, run an on-device quality gate: blur, brightness/exposure, glare, text-region presence, and text regions touching frame edges. | M | P1 |
| FR-CAM-03 | If the gate fails, give one short spoken/haptic instruction (for example "turn on torch", "text is cut off on the right, move the phone slightly right") and await the next capture. | M | P1 |
| FR-CAM-04 | Limit consecutive guided retries to 3 (configurable). After that, answer with the best available result and state what could not be read. | M | P1 |
| FR-CAM-05 | Merge text recovered across multiple captures of the same subject into one reading. | S | P2 |
| FR-CAM-06 | Offer an optional live-preview mode that analyses frames continuously and auto-captures when framing is acceptable. | C | P3 |
| FR-CAM-07 | Direction hints must be relative to the phone (up/down/left/right, closer/farther). | M | P1 |
| FR-CAM-08 | Auto-enable the torch suggestion in low light; never enable flash without user consent in the profile. | S | P2 |

### 3.4 Text reading (OCR)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-OCR-01 | Extract text from images in English and Devanagari on-device; in other Indic scripts via cloud OCR. | M | P1 |
| FR-OCR-02 | Detect the script/language of the text and, if different from the user's language, translate it (keeping original text available). | M | P1 |
| FR-OCR-03 | Classify the content as shop name, street name, bus board, notice, or other, and tailor the spoken summary accordingly. | S | P2 |
| FR-OCR-04 | Mark low-confidence words (for example "I think this says ...") and never present guesses as certain. | M | P1 |
| FR-OCR-05 | Provide "read original" (in the source language/script phonetics) and "read translated" modes. | S | P2 |

### 3.5 Surroundings summary (ENV)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-ENV-01 | On request, produce a concise spoken summary (<= 3 short sentences by default) from OCR text in the image, optional coarse location, nearby named places and optional on-device labels. A vision-language model may enrich the summary only if an optional adapter is enabled (NFR-COST-03). | M | P2 |
| FR-ENV-02 | Express relative position using simple terms (left, right, ahead, approximate metres) only when derivable, otherwise omit. | M | P2 |
| FR-ENV-03 | Never state or imply that a route, crossing or object is safe. | M | P1 |
| FR-ENV-04 | Allow follow-up questions about the last image ("what does the board on the left say?"). | S | P2 |
| FR-ENV-05 | Proactive spoken descriptions are off by default and opt-in per user. | M | P2 |

### 3.6 Sound perception (SND)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-SND-01 | Continuously classify ambient audio on-device into a defined label set (horn, siren, bell, speech, alarm, knock/doorbell, dog bark, announcement-like speech, other). | M | P2 |
| FR-SND-02 | Map labels to priority levels (siren > horn > alarm > bell > others) and per-priority alert styles (icon, text, vibration pattern, optional flash). | M | P2 |
| FR-SND-03 | Suppress repeats of the same label within a configurable window (default 5 s) and coalesce bursts to avoid alert fatigue. | M | P2 |
| FR-SND-04 | Display an alert card with large icon, label and loudness cue; keep a short on-screen history. | M | P2 |
| FR-SND-05 | Let the user enable/disable each label and set sensitivity. | S | P2 |
| FR-SND-06 | For blind users, do not read sounds aloud automatically; provide "what was that sound?" on demand. | M | P2 |
| FR-SND-07 | Provide live captions of nearby speech on request (ASR streaming). | S | P3 |
| FR-SND-08 | Log (locally, opt-in export) detections and user feedback ("wrong"/"missed") for tuning. | S | P2 |
| FR-SND-09 | Pause alerting while the user is dictating a command, to avoid capturing own TTS or misfiring. | S | P3 |

### 3.7 Output and accessibility (OUT)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-OUT-01 | All output must be available as speech, on-screen text, or haptic, selected by profile. | M | P1 |
| FR-OUT-02 | Support adjustable speech rate, text size (up to at least 200%), and high-contrast theme. | M | P1 |
| FR-OUT-03 | Provide "explain simpler" which re-generates the last answer at a lower reading level. | M | P1 |
| FR-OUT-04 | All UI must work with TalkBack: labelled controls, logical focus order, no gesture-only actions. | M | P1 |
| FR-OUT-05 | Support speech output via the phone speaker, wired/Bluetooth headset, or bone-conduction device. | M | P1 |
| FR-OUT-06 | Descriptions of tables, lists or structured text must be linearised into short sentences. | S | P2 |

### 3.8 Languages (LNG)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-LNG-01 | Input and output in English, Hindi and at least one further Indian language at launch (choose in 08). | M | P1 |
| FR-LNG-02 | Provider adapter layer for ASR, translation, TTS and OCR with per-language provider routing and automatic failover. | M | P1 |
| FR-LNG-03 | Pass code-mixed transcripts to the language model unchanged; do not force translation to a single language first. | M | P1 |
| FR-LNG-04 | Offer a manual language override by voice ("speak in Tamil"). | S | P2 |

### 3.9 Errors and degraded operation (ERR)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-ERR-01 | When any provider call fails, retry once, then fail over; if still failing, tell the user plainly and offer on-device alternatives. | M | P1 |
| FR-ERR-02 | If there is no network, still support on-device OCR (English/Devanagari), sound alerts and basic commands; state clearly which features are unavailable. | M | P2 |
| FR-ERR-03 | Misrecognised speech must result in a repeat/confirm prompt, never a silent wrong action. | M | P1 |
| FR-ERR-04 | Every long operation (> 2 s) must give a progress cue (tone or short phrase). | S | P1 |

### 3.10 Privacy and data (PRV)
| ID | Requirement | Pri | Ph |
|---|---|---|---|
| FR-PRV-01 | Do not store images or audio on the server; process in memory and discard after the response. | M | P1 |
| FR-PRV-02 | Blur faces and number plates on-device before any upload when feasible. | S | P3 |
| FR-PRV-03 | Explain data use in the user's language during onboarding; collect explicit consent for each optional data class (location, telemetry, feedback export). | M | P1 |
| FR-PRV-04 | Provide a visible indicator whenever the microphone/camera is actively processing, and a quick "pause listening" control. | M | P1 |
| FR-PRV-05 | Allow deleting all local data from the app. | M | P1 |

## 4. External interface requirements

- **UI:** Android, TalkBack-compatible; large controls; no reliance on colour alone.
- **Hardware:** camera, microphone, vibrator, optional Bluetooth audio.
- **Software:** Sarvam APIs, Bhashini APIs, a vision-language model API, optional Google Places/Overpass (OpenStreetMap) for nearby places.
- **Communications:** HTTPS/TLS 1.2+ to the backend; backend to vendors over HTTPS.

## 5. Non-functional requirements

### 5.1 Performance (initial targets, online, mid-range phone)
| ID | Requirement |
|---|---|
| NFR-PERF-01 | On-device quality gate result within 300 ms of capture. |
| NFR-PERF-02 | Wake/PTT to first audio response <= 5 s median, <= 8 s p95 for a single-photo read. |
| NFR-PERF-03 | Sound classification latency <= 1 s from event onset to alert. |
| NFR-PERF-04 | App cold start <= 3 s; service restart after kill <= 5 s. |

### 5.2 Accuracy (initial targets, to be validated on field data)
| ID | Requirement |
|---|---|
| NFR-ACC-01 | Critical-sound (siren, close loud horn) recall >= 90%. |
| NFR-ACC-02 | False alerts <= 1 per 10 min of dense-traffic audio at default sensitivity. |
| NFR-ACC-03 | ASR word error rate target per language to be set after baseline benchmarks (see 06). |
| NFR-ACC-04 | OCR character error rate on shop boards to be baselined per script (see 06). |

### 5.3 Reliability and availability
| ID | Requirement |
|---|---|
| NFR-REL-01 | Backend availability target 99.5% monthly. |
| NFR-REL-02 | The foreground service must survive common OEM battery-saver policies where permitted, and guide the user to exemption settings. |
| NFR-REL-03 | Crash-free sessions >= 99%. |

### 5.4 Battery and resources
| ID | Requirement |
|---|---|
| NFR-PWR-01 | Always-on listening (wake phrase plus sound classifier) <= 8% battery per hour on a reference mid-range phone. |
| NFR-PWR-02 | Camera used on demand only, except in optional live-preview mode. |
| NFR-PWR-03 | On-device models total <= 150 MB installed (excluding optional downloads). |

### 5.5 Usability and accessibility
| ID | Requirement |
|---|---|
| NFR-USE-01 | All core flows completable without looking at the screen. |
| NFR-USE-02 | All core flows completable without hearing any audio. |
| NFR-USE-03 | Spoken responses default to <= 25 words unless the user asks for more. |
| NFR-USE-04 | Conform to WCAG 2.2 AA principles for the app UI where applicable. |

### 5.6 Security
| ID | Requirement |
|---|---|
| NFR-SEC-01 | TLS for all transport; no vendor keys in the client; backend holds secrets. |
| NFR-SEC-02 | Rate limiting and abuse protection on the backend; per-install anonymous token. |
| NFR-SEC-03 | Dependency scanning and pinned versions. |

### 5.7 Privacy and compliance
| ID | Requirement |
|---|---|
| NFR-PRV-01 | Align with India's Digital Personal Data Protection Act, 2023 and applicable rules (verify current status): notice, consent, purpose limitation, deletion. |
| NFR-PRV-02 | No third-party analytics SDKs that collect audio/images. |

### 5.7a Cost and licensing
| ID | Requirement |
|---|---|
| NFR-COST-01 | The baseline product must run using only free components (04, `THIRD_PARTY.md`); paid or credit-limited services are optional adapters, off by default. |
| NFR-COST-02 | Every third-party component's licence and pricing status is recorded in `THIRD_PARTY.md` and reviewed before each release. |
| NFR-COST-03 | The core "read this board" flow (FR-CAM, FR-OCR, FR-LNG) must not depend on a vision-language model. |
| NFR-COST-04 | Respect fair-use/rate limits of free services (for example Nominatim 1 request/s for the whole app); degrade gracefully when limits are hit. |
| NFR-COST-05 | Do not ship models whose licence forbids commercial use (for example openWakeWord pre-trained models). |

### 5.8 Maintainability and portability
| ID | Requirement |
|---|---|
| NFR-MNT-01 | Provider adapters isolate vendor specifics; swapping a vendor must not change app code. |
| NFR-MNT-02 | Model files are versioned and updatable without an app release where store policy allows. |
| NFR-PORT-01 | Core logic structured to allow a later iOS port (shared backend and API contract). |

## 6. Acceptance criteria (summary)

A release candidate is accepted when: all Must requirements for its phase pass their tests; the Product Brief success measures meet targets in a pilot of at least 5 users per primary persona; there are no open critical safety or privacy defects; and the app passes a TalkBack walkthrough of every core flow.

## 7. Traceability
Each FR maps to test cases in 06 using the same ID in the test name (for example `test_FR_CAM_04_retry_cap`).
