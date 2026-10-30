# Third-Party Components, Licences and Cost Status

Last checked: 2026-10-30. "Checked" = confirmed via web search (mostly secondary sources; **official pages not always retrieved**). "Verify" = from memory, not yet checked. Re-check before any public release.

| Component | Role | Licence / terms | Free? | Status | Source / action |
|---|---|---|---|---|---|
| Bhashini APIs | ASR, NMT, TTS, OCR | Government platform; production/commercial terms unconfirmed | Free for prototyping | Checked (secondary) | Read official terms; ask Bhashini/MeitY in writing. Not to be confused with `bhashini.ai` / `bhashiniservices.com` (private vendor) |
| Sarvam APIs | Optional benchmark only | Paid: STT about ₹30/h, Bulbul v3 TTS about ₹30/10k chars; about ₹100 signup credit | No | Checked | https://docs.sarvam.ai/api/getting-started/pricing |
| Claude API | Not used | Paid | No | Checked | Excluded from baseline |
| Gemini API free tier | Optional VLM | Free-tier limits unpublished/volatile; data-use terms to check | Conditional | Checked (secondary) | Read AI Studio limits and data terms first |
| YAMNet (code and weights) | Sound classification | Apache 2.0 | Yes | Checked (mirrors) | Keep Apache notice |
| AudioSet ontology/labels | Label names | CC BY 4.0 | Yes, with attribution | Checked (mirrors) | Add attribution in app credits |
| openWakeWord library | Wake-phrase code | Code licence not confirmed (one source says Apache 2.0) | Verify | Verify | Read repo LICENSE |
| openWakeWord pre-trained models | Wake phrase | CC BY-NC-SA 4.0 | **No for commercial** | Checked | Do not ship |
| livekit-wakeword | Wake-phrase training | Apache 2.0 (per PyPI listing) | Yes | Checked (single source) | Verify, check dataset licences |
| openwakeword-trainer | Wake-phrase training | MIT (per listing) | Yes | Checked (single source) | Its pipeline uses ACAV100M (CC BY-NC-SA), so trained models may inherit non-commercial terms; swap datasets |
| Picovoice Porcupine | Not used | Free-tier terms contradictory; paid plans | No | Checked | Excluded |
| ML Kit Text Recognition v2 | On-device OCR | Google ML Kit terms; Devanagari + Latin supported | Likely yes | Partly checked (support yes, pricing not stated) | Read ML Kit terms |
| Tesseract | Fallback OCR | Apache 2.0 | Yes | Verify | |
| OpenCV | Image quality | Apache 2.0 | Yes | Verify | |
| TensorFlow Lite / LiteRT | On-device inference | Apache 2.0 | Yes | Verify | |
| Silero VAD / WebRTC VAD | End-of-speech | MIT / BSD-style | Yes | Verify | |
| Android SDK, Jetpack, CameraX, Compose | Client | Apache 2.0 | Yes | Verify | |
| OkHttp, Retrofit, Hilt, DataStore | Client | Apache 2.0 | Yes | Verify | |
| FastAPI, pydantic, httpx, uvicorn | Backend | MIT / BSD | Yes | Verify | |
| Pillow, RapidFuzz, tenacity, structlog | Backend | HPND / MIT / Apache | Yes | Verify | |
| ffmpeg | Audio transcoding | LGPL or GPL depending on build | Yes | Verify | Choose an LGPL build |
| AI4Bharat models (IndicTrans2, IndicConformer, IndicWhisper) | Optional local dev | Open licences (verify per model) | Yes | Verify | Check each model card |
| OSM Nominatim | Geocoding | Public instance: reported max 1 req/s for whole app, User-Agent required, cache, no bulk/autocomplete | Conditional | Checked (secondary) | https://operations.osmfoundation.org/policies/nominatim/ |
| OSM Overpass | POI lookup | Fair use; no official numeric limit found | Conditional | Checked (secondary) | Read the instance's policy |
| OpenStreetMap data | Map data | ODbL; attribution required | Yes | Verify | Add attribution |
| Datasets: ESC-50, UrbanSound8K, AudioSet | Evaluation | Various; some non-commercial | Verify | Verify | Check before using for training shipped models |
| Datasets: IndicVoices, Kathbath, Vistaar | ASR evaluation | AI4Bharat licences | Verify | Verify | Check before use |
| Datasets: IIIT-ILST, ICDAR MLT | OCR evaluation | Research licences likely | Verify | Verify | Check before use |

## Attribution to include in the app credits
AudioSet labels (CC BY 4.0), YAMNet (Apache 2.0), OpenStreetMap contributors (ODbL), plus notices required by every Apache/MIT/BSD component.

## Open licence tasks
1. Official Bhashini terms for production use.
2. openWakeWord repository licence, and a clean-licence training recipe.
3. ML Kit terms and any quotas.
4. Gemini free-tier data-use terms (only if used).
5. Dataset licences for any data used to train shipped models.
