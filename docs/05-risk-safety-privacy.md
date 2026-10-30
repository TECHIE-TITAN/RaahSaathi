# 05 — Risk, Safety and Privacy

## 1. Safety stance

The product is an **assistive aid, not a safety system**. It must never be the sole basis for a safety decision. It must not say that anything is safe (crossings, vehicles, food allergens, paths). This is enforced in prompts, UI copy, onboarding, and tests.

## 2. Risk register

Likelihood (L) and Impact (I): 1 low to 5 high.

| ID | Risk | L | I | Mitigation | Owner |
|---|---|---|---|---|---|
| R1 | User relies on missed/absent sound alert and is harmed | 3 | 5 | Publish measured recall; onboarding disclaimer in user's language; never claim completeness; conservative defaults for siren/close horn; field testing before release | Product |
| R2 | VLM hallucinates board text or surroundings | 4 | 4 | OCR-grounded answers; mark low-confidence words; "I can't tell" behaviour; test with adversarial images | ML |
| R3 | Wrong ASR leads to wrong action | 4 | 3 | Confirm read-back for consequential intents; low-confidence thresholds | App |
| R4 | Wake phrase false accepts (privacy, annoyance) or false rejects (unusable) | 4 | 3 | On-device model; PTT fallbacks; tune threshold; test on noise corpora | ML |
| R5 | Background service killed by OEM battery policies | 5 | 4 | Onboarding exemption guide; watchdog restart; user-visible status; OEM test matrix | App |
| R6 | Battery drain from always-on listening | 4 | 3 | Single shared audio capture; duty cycling; pause control; measure budgets | App |
| R7 | Camera use unsafe while walking (blind user stops, blocks path) | 3 | 4 | Advice in onboarding to pause in safe place; short capture loops; retry cap | Product |
| R8 | Bystander privacy (faces, number plates, conversations) | 4 | 4 | On-device processing; blur before upload; no storage; clear mic/camera indicators | Privacy |
| R9 | Vendor API outage or quota exhaustion | 4 | 3 | Adapter failover, circuit breaker, offline modes, quota monitoring | Backend |
| R10 | Vendor data-handling terms conflict with promised privacy | 3 | 4 | Review terms; choose zero-retention options where available; document processors | Privacy |
| R11 | Poor Indic/code-mixed accuracy in noise | 4 | 3 | Benchmark early; per-language routing; confirmation flows; collect failures | ML |
| R12 | Alert fatigue in dense honking | 5 | 3 | Dedup/cooldown; priority tiers; sensitivity control; user feedback loop | ML |
| R13 | Overload of blind user's attention (voice over street noise) | 3 | 4 | On-demand speech default; short outputs; bone-conduction support | UX |
| R14 | Excluding users: low literacy, low digital skill, older users | 3 | 3 | Voice-first onboarding; simple modes; user testing across groups | UX |
| R15 | Bias or poor performance for some accents, genders, regions | 3 | 3 | Diverse test sets; per-group error reporting | ML |
| R16 | Data collection for fine-tuning violates consent/law | 2 | 5 | Written consent; minimal data; anonymisation; secure storage; DPDP review | Privacy |
| R17 | Free-service limits or terms change (Bhashini quotas/terms, Gemini free tier, Nominatim 1 req/s) and break features | 4 | 4 | Quota meter; caching; on-device fallbacks; written confirmation of Bhashini terms; optional adapters; re-check `THIRD_PARTY.md` each release | Backend |
| R21 | Free-tier data-use terms (if a free VLM is used) conflict with promised privacy | 3 | 4 | Do not use free-tier VLMs for user photos until terms reviewed; keep VLM adapter off by default | Privacy |
| R22 | A shipped model carries a non-commercial licence (e.g. openWakeWord pre-trained models, models trained on CC BY-NC-SA data) | 3 | 4 | Licence review gate before release; clean-licence training recipe | ML |
| R18 | Cultural sensitivity or misreading of religious/sensitive signage | 2 | 3 | Neutral phrasing; no inference beyond text | Product |
| R19 | Security: stolen API keys or abuse of backend | 2 | 4 | Keys server-side; per-install tokens; rate limits; WAF | Backend |
| R20 | Accessibility regressions in the app's own UI | 3 | 3 | Accessibility tests in CI; manual TalkBack passes each release | App |

## 3. Safety requirements (extracted)

- S1 No safety assurances in any output (ties to FR-ENV-03).
- S2 Disclaimers shown/spoken at onboarding, in the user's language, and re-surfaced in Help.
- S3 Uncertainty must be communicated whenever confidence is below threshold.
- S4 Alert features must expose their limits ("may miss sounds").
- S5 A big, always-reachable "Stop" action exists (voice, button, key).
- S6 Release gating: no release without a field-test report on critical-sound recall and false alerts.

## 4. Privacy design

### 4.1 Data inventory

| Data | Where processed | Stored? | Retention | Consent |
|---|---|---|---|---|
| Ambient audio (sound classification) | On device only | Not stored (ring buffer) | Seconds | Onboarding notice |
| Wake-phrase audio | On device only | Not stored | Seconds | Onboarding notice |
| Command audio | Device then backend then vendor ASR | Not stored by us; vendor terms to verify | In memory | Per use (user triggers) |
| Photos | Device, then backend/vendor on capture | Not stored by us | In memory | Per use |
| Coarse location | Device then backend for places | Not stored | In memory | Opt-in |
| Profile | Device | Yes, local | Until deleted | Setup |
| Alert history / feedback | Device | Yes, local, opt-in export | User-controlled | Opt-in |
| Telemetry | Device then backend | Aggregated, no media | 90 days (proposal) | Opt-in |

### 4.2 Principles
Data minimisation, purpose limitation, on-device first, explicit and understandable consent in the user's language, easy deletion, visible indicators when sensors are active, no sale or advertising use.

### 4.3 Legal
Design to align with India's Digital Personal Data Protection Act, 2023 and rules made under it (verify current notified provisions): notice and consent, purpose limitation, rights of access/erasure, children's data caution (a user may be under 18; plan for verifiable parental consent handling if minors are in scope), data processor agreements with vendors, breach response. Get legal review before pilot.

### 4.4 Research data (if collecting street audio/images for tuning)
Written informed consent from participants; avoid recording identifiable conversations (filter or discard speech segments); blur faces/plates; encrypted storage with access control; documented deletion policy; consider institutional ethics review.

## 5. Security measures

TLS everywhere; secrets in a managed secret store; per-install token; rate limiting and request-size limits; input validation for media; dependency pinning and scanning; least-privilege cloud roles; no PII in logs; periodic review of vendor permissions.

## 6. Ethical considerations

Co-design with users with disabilities; pay and credit participants fairly; avoid paternalism (the user stays in control of proactivity); transparency about limitations; accessible feedback and grievance channel.
