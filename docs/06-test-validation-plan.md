# 06 — Test and Validation Plan

## 1. Goals

Show that each requirement works, that the numbers in the SRS hold on realistic Indian audio/images, and that real users with disabilities find the product useful and safe enough.

## 2. Test levels

| Level | Scope | Tools | Gate |
|---|---|---|---|
| Unit | Dialogue state machine, quality-gate maths, alert policy, adapter routing, fuzzy merge | JUnit5/MockK, pytest | CI on each PR |
| Component | Backend endpoints with mocked vendors; client modules with fakes | respx, Espresso | CI |
| Contract | Client-backend API schema | Pact or schema tests | CI |
| Integration | Real vendor sandboxes; latency tests | pytest + secrets in CI (nightly) | Nightly |
| Model evaluation | ASR, OCR, sound classifier, wake phrase on fixed datasets | Python eval harness | Per model change |
| Device | Battery, thermal, OEM background survival, low-end performance | Physical devices | Per release |
| Accessibility | TalkBack walkthroughs, Accessibility Test Framework, large-font checks | Manual plus automated | Per release |
| Field | Walks in real streets with recordings and logs | Test protocol below | Per phase exit |
| User study | Blind and deaf participants | Study protocol below | Per phase exit |

## 3. Model evaluation protocols

### 3.1 ASR / command understanding
- Build a **command set**: ~30 intents, each phrased in Hindi, English, and code-mixed variants, spoken by >= 20 speakers (varied age, gender, region), recorded in quiet, street noise (traffic), and market noise.
- Metrics: WER/CER (`jiwer`), **intent accuracy** (the metric that matters), confirmation rate, latency.
- Compare Sarvam vs Bhashini per language; choose routing table (03 section 5) from results.

### 3.2 OCR and board reading
- Collect >= 500 photos of shop boards, street plates, bus boards across 3+ cities, in daylight, night, glare, partial occlusion, taken by blind volunteers where possible (realistic framing).
- Metrics: CER/WER per script, "correct business name" rate, fraction needing retake, **attempts-to-success**, final spoken-answer correctness (human-rated).
- Compare ML Kit (local) vs Bhashini vs cloud OCR vs VLM reading.

### 3.3 Quality gate
- Label frames as acceptable/unacceptable (blurry, dark, cut-off, no text). Metrics: precision/recall of the gate, agreement of directional hints with human judgement, and false "retake" rate (annoying) vs missed bad frame.

### 3.4 Sound events
- Street recordings in >= 3 cities (dense traffic, highway, market, railway/bus station, indoor), labelled by humans (horn, siren, bell, speech, announcement, alarm, other).
- Metrics per label: precision, recall, F1; **critical-sound recall** (siren, close loud horn); false alerts per 10 min; alert latency; effect of dedup settings.
- Report results by environment, not just aggregate.

### 3.5 Wake phrase
- Positive set: many speakers saying "Hey Raahsaathi" in varied accents and noise; negative set: hours of speech, TV, traffic, similar-sounding phrases.
- Metrics: false reject rate; false accepts per hour; latency; battery cost.

### 3.6 VLM/LLM output safety
- A red-team set of images/prompts: blurry text, misleading signs, crossing scenes, food packaging with allergens, faces, prompts asking "is it safe?".
- Pass criteria: zero safety assurances; zero face-based identification; uncertainty phrases when OCR confidence is low; <= 25 words by default.

## 4. Non-functional tests

| Area | Test | Pass criterion |
|---|---|---|
| Latency | 200 end-to-end read requests on 4G, mid-range phone | Median <= 5 s, p95 <= 8 s |
| Battery | 1-hour continuous listening on reference phones, screen off | <= 8%/h |
| Thermal | 30 min field use in hot weather | No throttling-induced failures |
| Background survival | 4+ hours idle on Xiaomi/Oppo/Samsung profiles with defaults | Service alive or restarts within 5 s |
| Offline | Airplane mode flows | Matches the offline table in 03 section 11 |
| Resilience | Kill a vendor mid-request | Failover or clear spoken error |
| Load | 50 concurrent users on backend | Meets latency; no errors above 1% |
| Security | Dependency scan; secrets scan; basic pen test of API | No high findings |
| Privacy | Network capture during idle listening | Zero audio/image traffic before trigger |

## 5. Accessibility testing

- Every screen: TalkBack read-through, focus order, labels, touch target size >= 48 dp, contrast, 200% text size.
- Every core flow completed **eyes-closed** (NFR-USE-01) and **sound-off** (NFR-USE-02).
- Run Android Accessibility Test Framework checks in CI; manual pass per release by a blind tester.

## 6. User validation

### 6.1 Participants
Blind/low-vision (>= 8), deaf/hard-of-hearing (>= 8), older adults (>= 4) in the first pilot; recruited through disability organisations; fairly compensated; informed consent in their language; ethical review if applicable.

### 6.2 Phases
1. **Formative interviews** (before building features): what they miss, how they cope, what they would not want automated.
2. **Prototype tasks** in a controlled setting: read a board, get surroundings, receive sound alert; measure success, attempts, time, errors.
3. **Supervised walks** along a fixed route with safety spotters: capture logs, think-aloud, debrief.
4. **Home/field diary** for one week: usage, failures, perceived usefulness.

### 6.3 Measures
Task success, attempts-to-success, time-to-answer, number of false alerts perceived, trust calibration (does the user over-rely?), SUS-style usability scale adapted for accessibility, perceived mental load, open feedback. Analyse separately per persona and language.

### 6.4 Safety monitoring
Spotters present in all mobility sessions; stop rules if a participant is at risk; incident log.

## 7. Traceability

Maintain a matrix `docs/traceability.csv`: requirement ID, test IDs, last result, evidence link. Test names embed the requirement ID.

## 8. Defect severity

| Severity | Definition | Example | Response |
|---|---|---|---|
| S1 | Safety/privacy breach or data loss | App claims a crossing is safe; audio sent without trigger | Stop release |
| S2 | Core flow broken for a persona | Cannot capture by voice | Fix before release |
| S3 | Degraded but workable | Slow TTS in one language | Schedule |
| S4 | Cosmetic | Icon misaligned | Backlog |

## 9. Exit criteria per phase

Defined in 07 and checked against this plan's results.
