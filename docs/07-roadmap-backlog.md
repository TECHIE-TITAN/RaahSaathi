# 07 — Roadmap and Backlog

Confirmed plan (2026-10-31): **Phase 0 is setup only.** Durations assume a small team (3-5 people) and are rough; adjust after the first sprint. Each phase from 1 onward ends with a check against real users or real data.

## Phase numbering and the old labels

An earlier draft numbered phases P1-P4 with setup folded into "Phase 0". The requirement tables in `02-srs.md` and some other docs still carry those **legacy labels**. Map them like this until those docs are renumbered:

| Legacy label | Now means |
|---|---|
| "Phase 0" (benchmarks, interviews, accounts) | **Phase 1** (and the accounts part is in Phase 0) |
| P1 | **Phase 2** |
| P2 | **Phase 3** |
| P3 | **Phase 4** |
| P4 | **Phase 5** |

## Phase 0 — Setup (this is where we are)

Goal: a machine where code can be built and run, accounts in place, and the work saved in git. No product code.

| Step | What | Done when | Status |
|---|---|---|---|
| 0.1 | Android Studio first run: SDK, platform tools, emulator package | `~/Library/Android/sdk` contains `platform-tools`, `emulator`, `build-tools`, `platforms` | Done |
| 0.2 | Command-line paths: `JAVA_HOME`, `adb`, SDK variables | `java -version` and `adb version` work in a new terminal | Done |
| 0.3 | Emulator (arm64 image) created; real phone connected if available | `adb devices` lists a device or emulator | Done (emulator Min3GB; no real phone yet) |
| 0.4 | Python 3.11+ for the backend (machine has 3.9.6) | `python3.12 --version` (or chosen version) works | Done (uv 0.13.6, Python 3.12.16) |
| 0.5 | Git housekeeping: `.gitignore`, first commit of docs, `PRODUCT.md`, `ui-prototype/` | `git log` shows the commit; no secrets tracked | Done once the commit is confirmed |
| 0.6 | Bhashini account and API keys; read production terms | Keys stored locally (never committed); terms noted in `docs/THIRD_PARTY.md` | Dropped from the baseline (no Bhashini account; ADR-016). `backend/.env` kept for optional providers |
| 0.7 | Decisions recorded: launch languages beyond English and Hindi; 2 GB Lite mode yes/no; Android package name | Answers written into `docs/08` | Done (ADR-013/014/015; package id `dev.techietitan.raahsaathi`) |
| 0.8 | Final toolchain check | A throwaway "hello world" Android app builds and runs; a throwaway FastAPI app responds | Android hello world done; FastAPI check pending |

**Exit:** all rows done. Then scaffolding starts.

## Phase 1 — Foundations and spikes (2-3 weeks)

Goal: prove or kill the riskiest assumptions before building features.

- Android project skeleton: Compose, Material 3 theme with the tokens from `docs/09`, navigation shell, CI (build and lint).
- Backend skeleton: FastAPI, provider adapter interfaces, routing config, fake providers, health endpoint.
- Spikes with scripts and small throwaway apps:
  - ASR on code-mixed commands: Bhashini vs on-device `SpeechRecognizer` (and Sarvam within free credits as a reference).
  - OCR on BSTD images (Bharat Scene Text Dataset, kept outside git): ML Kit Latin and Devanagari. Assumes dataset-style framing; blind-user framing is deferred (ADR-017) and must be revisited before the Phase 2 exit claim.
  - YAMNet on about 30 minutes of street audio.
  - **On-device footprint:** APK size, steady and peak RAM, battery per hour on your lowest-spec phone; decide Lite mode.
- Formative interviews with 3-5 users per persona; recruit through disability organisations.
- Licence review of every "verify" item in `THIRD_PARTY.md`; written confirmation of Bhashini terms.

**Exit:** benchmark report; routing table v0; footprint numbers vs budgets; interview summary; updated risk register.

## Phase 2 — Voice and guided reading (4-6 weeks)  *(legacy P1)*

Scope: FR-PRO, FR-VOI (push-to-talk only), FR-CAM (01-04, 07), FR-OCR (01, 02, 04), FR-OUT (01-05), FR-LNG, FR-ERR (01, 03, 04), FR-PRV (01, 03-05), FR-ENV-03.

Deliverables: onboarding; push-to-talk with confirm read-back; capture with the quality gate and retry loop; board reading with translation and TTS; "explain simpler"; provider adapters with failover; TalkBack-clean UI per `docs/09`.

**Exit:** at least 80% of board reads correct within 3 attempts in a small blind-user trial; median latency <= 5 s; no S1/S2 defects.

## Phase 3 — Sound perception and surroundings (4-6 weeks)  *(legacy P2)*

Scope: FR-SND (01-06, 08), FR-ENV (01, 02, 04, 05), FR-CAM-05, FR-OCR-03/05, FR-OUT-06, FR-ERR-02 (offline modes), FR-PRO-04.

Deliverables: foreground audio service; sound classifier with label map and alert policy; deaf-mode alert cards and vibration patterns; surroundings summary; offline OCR and sound alerts; feedback buttons (wrong/missed).

**Exit:** critical-sound recall >= 90% and false alerts <= 1 per 10 min on collected field data (or a documented gap and mitigation); deaf-user trial results; battery within budget.

## Phase 4 — Wake phrase and adaptivity (4 weeks)  *(legacy P3)*

Scope: FR-VOI-02/07/08, FR-SND-07/09, FR-CAM-06, FR-PRO-03, FR-PRV-02.

Deliverables: on-device "Hey Raahsaathi" with a clean-licence model and self-trigger handling; live captions; auto-capture mode; profile suggestions; OEM battery guidance flows.

**Exit:** wake false accepts below target; combined battery <= 8%/h; background survival on the OEM matrix.

## Phase 5 — Pilot and hardening (4-6 weeks)  *(legacy P4)*

Scope: bug fixing, language expansion, security review, legal/privacy review, one-week diary study with 15-20 users, closed-testing release or signed APK.

**Exit:** all Must requirements pass; pilot success measures met or documented; privacy review complete; incident-free pilot.

## Later / ideas

- Personalised name-call detection.
- Sound direction with a two-mic earbud/wristband accessory.
- Bus board reader integrated with GTFS/Chalo-style feeds.
- Landmark-based directions and accessible-route planning.
- iOS client.
- Deafblind support (braille display / haptic wearable).
- Community crowdsourcing of sign health and footpath data.
- On-device small VLM for offline surroundings.

## Epic backlog

| Epic | Key stories | Phase |
|---|---|---|
| E0 Setup | Toolchain, emulator, Python, git, keys, decisions | 0 |
| E1 Onboarding and profile | Voice-first onboarding; language and rate selection; consent screens | 2 |
| E2 Voice I/O | PTT; VAD; ASR via adapters; confirm read-back; intents | 2 |
| E3 Guided capture | CameraX; quality gate; hints; retry cap; merge | 2-3 |
| E4 Reading pipeline | OCR local/cloud; classify; translate; summarise; TTS | 2 |
| E5 Provider layer | Adapters; routing table; failover; circuit breaker; quota meter | 1-2 |
| E6 Sound pipeline | Audio service; classifier; alert policy; UI; feedback | 3 |
| E7 Surroundings | Places adapter; template/optional VLM summary; follow-ups | 3 |
| E8 Offline | Local OCR; system TTS fallback; messaging | 3 |
| E9 Wake phrase | Clean-licence model; service integration; self-trigger guard | 4 |
| E10 Adaptivity | Inference suggestions; output router refinements | 4 |
| E11 Safety and privacy | Prompt/template rules; red-team set; consent; deletion; blur | 2-4 |
| E12 Quality and ops | CI; eval harness; device matrix; monitoring; traceability | 1-5 |
| E13 User research | Interviews; trials; diary study | 1-5 |
| E14 UI system | Theme tokens, components, screens per `docs/09`; TalkBack and font-scale QA | 1-5 |

## Team roles (suggested)

Android lead; ML/audio engineer; backend/integration engineer; UX/accessibility lead (ideally with lived experience or close partnership); product/research owner; part-time privacy/legal advisor.

## Lead-time items to start now

Bhashini onboarding, access to disability organisations for recruitment, consent and ethics process, collecting Indian street audio and signboard images, app name selection.
