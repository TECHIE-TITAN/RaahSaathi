# 08 — Decisions (ADR log) and Open Questions

## Architecture decisions

### ADR-001 Android-first, native Kotlin (Jetpack Compose, Material 3) — *Accepted (user confirmed)*
- **Context:** The wake phrase and continuous sound perception need a background microphone service. iOS does not allow third-party always-on wake-word listening. Cross-platform frameworks make foreground audio services and low-latency ML harder.
- **Decision:** Build native Android first. Keep the backend API platform-neutral for a later iOS client.
- **Consequences:** Excludes iPhone users initially; best access to background audio and TalkBack; larger share of the Indian market.

### ADR-002 On-device for latency/privacy-critical tasks; cloud for language tasks — *Proposed*
- Wake phrase, sound classification, quality gate, and basic OCR run on device. ASR, translation, TTS, non-Devanagari OCR, and VLM run in the cloud via the backend.

### ADR-003 Provider adapter layer with per-language routing and failover — *Proposed*
- Both Sarvam and Bhashini are used. Routing is configuration driven and set from benchmark results. Rationale: vendor quality varies by language/task, and APIs change.

### ADR-004 Describe, never certify — *Accepted*
- No output may assert safety. Enforced in prompts, UI, tests (05 S1).

### ADR-005 Push-to-talk and hardware fallbacks ship before the wake phrase — *Proposed*
- The wake phrase is unreliable in noise and constrained by OEM/battery behaviour; it must never be the only trigger.

### ADR-006 Blind users get on-demand speech by default; proactive speech is opt-in — *Proposed*
- Avoids masking environmental sounds they rely on.

### ADR-007 Stateless backend, no media retention — *Proposed*
- Reduces privacy risk and compliance burden. Aggregated, media-free metrics only.

### ADR-008 General sound classification first; personalised and directional detection deferred — *Accepted (user decision)*
- Start with a general label set and an off-the-shelf model; collect Indian data to fine-tune later.

### ADR-009 Guided capture is a click-evaluate-instruct loop with a retry cap — *Accepted (user decision)*
- With an on-device quality gate, one instruction per attempt, three attempts maximum before best-effort answer; live-preview auto-capture considered in P3.

### ADR-010 Code-mixed speech handled via Sarvam/Bhashini APIs for now — *Accepted (user decision)*
- Transcripts stay code-mixed through to the language model; no premature translation.

### ADR-011 Free-only baseline stack; hosting deferred — *Accepted (user decision)*
- **Decision:** The baseline uses only free components (see 04 and `THIRD_PARTY.md`). Paid or credit-limited services (Claude API, Google Cloud Vision, Google Places, Picovoice, Sarvam beyond credits) are excluded from the baseline. Hosting is out of scope for now; the backend runs locally.
- **Consequences:** Bhashini is the single primary language engine, so its production terms are a critical open item. No wake phrase until Phase 3 (PTT first; no pre-trained non-commercial models). The core read-a-board flow must not depend on a VLM; "what is around me" is degraded in the baseline. Rate limits and fair-use rules (Bhashini, Nominatim 1 req/s) replace cost as the main constraint.
- **Supersedes** the Sarvam-primary routing in ADR-003 and ADR-010 for the baseline; Sarvam remains an optional adapter used within free credits for benchmarks.

### ADR-012 App name: Raahsaathi (राहसाथी) — *Accepted (user decision, 2026-10-31)*
- **Decision:** The app is named **Raahsaathi** (Devanagari: राहसाथी), "companion of the road", coined from *raah* (road) and *saathi* (companion). The wake phrase is "Hey Raahsaathi". Roman spelling is fixed as **Raahsaathi** (capital R, double "aa") everywhere.
- **Why:** Warm like *Saathi* but distinctive; gender-neutral in Hindi; sets a "with you" expectation rather than a "will lead you" one, which matters because v1 does not navigate and never certifies safety.
- **Risks to check:** (1) wake-word performance: "raah-saa-thi" has two long vowels and an aspirated "th", so test it on real recordings across accents before Phase 4; (2) trademark, Play Store and domain availability (not yet checked); (3) *Saathi* is widely used by other products, so the compound must be checked for near-matches.
- **Still open:** the Android package id (decide in setup step 0.7).

## Open questions

| # | Question | Needed by | Notes |
|---|---|---|---|
| Q1 | Native Android confirmed, or is a web/PWA expected? | Phase 0 | Web cannot do reliable background mic or wake phrase |
| Q2 | ~~App name (also the wake phrase)~~ **Resolved: Raahsaathi (ADR-012).** Remaining: availability check (trademark, Play Store, domain) and wake-phrase accent testing | Phase 1 / Phase 4 | See ADR-012 |
| Q3 | Which Indian languages at launch besides English and Hindi? | Phase 0 | Drives ASR/TTS/OCR benchmarks, test data collection |
| Q4 | Which city or cities for field collection and pilot? | Phase 0 | Affects sound/sign diversity |
| Q5 | Do we use any VLM at all in the free baseline (Gemini free tier, local open-weight model), or keep "what is around me" OCR/places-based only? | Phase 0 | Gemini free-tier limits and data-use terms need review; see 04 section 3.2 |
| Q15 | What are Bhashini's official terms for production/pilot use, and can we get written confirmation? | Phase 0 | Critical: it is the only free engine for ASR/translation/TTS/OCR |
| Q16 | Which clean-licence recipe do we use to train the wake phrase (datasets, TTS voices)? | Phase 2 | Pre-trained openWakeWord models are non-commercial |
| Q6 | Is any data collection for fine-tuning planned, and who approves ethics/consent? | Phase 0 | Long lead time |
| Q7 | Is the audience adults only, or minors included? | Phase 1 | Affects consent flows under DPDP |
| Q8 | Distribution: Play Store, side-loaded APKs for pilot, or both? | Phase 2 | Affects permissions review for foreground mic service |
| Q9 | Who are the disability-organisation partners for validation? | Phase 0 | Needed for formative research |
| Q10 | Monetisation or funding model (grant, CSR, free)? | Phase 3 | Revisit ADR-011 if paid services or hosting become affordable |
| Q11 | Do we support deafblind users in v1? | Phase 1 | Current plan: no (stretch) |
| Q12 | Offline expectations: how much must work without data? | Phase 1 | Current plan in 03 section 11 |
| Q13 | Are vendor zero-retention options available and acceptable? | Phase 1 | Privacy review |
| Q14 | Hardware accessories (bone-conduction headset, chest mount) recommended in the product, or phone only? | Phase 1 | Affects UX and safety advice |

## Assumptions to validate early

1. Existing sound detectors miss or confuse Indian horns/sirens enough that a tuned model is worthwhile (benchmark in Phase 0).
2. Sarvam or Bhashini ASR handles code-mixed street-noise commands at >= 85% intent accuracy.
3. ML Kit plus cloud OCR reads typical Indian shop boards well enough that 3 guided attempts suffice.
4. Blind users want on-demand over proactive descriptions.
5. Deaf users value horn/siren alerts, or prefer other signals such as being called or announcements; interviews decide priorities.
