# 01 — Product Brief

## 1. Problem

Daily movement through Indian public spaces depends on senses many people do not have in full:

- **Sight:** shop names, street names, bus boards, signs, obstacles. Signage is multi-script, inconsistent, faded or occluded.
- **Hearing:** horns, sirens, someone calling your name, station announcements.

Existing tools are single-purpose (read text, or detect a few sounds), English-first, and assume the user can frame a camera or see an alert. None combine both senses, handle Indian scripts and code-mixed speech, and help a blind user capture a usable photo.

## 2. Vision

One phone app that acts as a sensory bridge: it perceives the surroundings and delivers the result through the channel the user can use, in the language they speak.

## 3. Target users

| Persona | Needs | Primary channels |
|---|---|---|
| **P1 Blind / low-vision commuter** | Know what a shop/street/bus board says, what is nearby, ask questions hands-free | Speech in/out, haptics |
| **P2 Deaf / hard-of-hearing pedestrian** | Know about horns, sirens, being called, announcements | Large text, icons, vibration, flash |
| **P3 Older adult** (secondary) | Simple steps, large text, slow speech | Large text, slow TTS |
| **P4 Deafblind** (stretch, later) | Both | Haptics, braille display |

Language: users speak mainly an Indian language, often code-mixed with English.

## 4. Core capabilities (v1 scope)

1. **Voice interaction:** "Hey Raahsaathi" wake phrase (with non-voice fallbacks), code-mixed speech understanding via Sarvam/Bhashini.
2. **Guided capture:** blind user takes a photo; the app evaluates it and asks for adjusted angles before giving the final answer.
3. **Board and street-name reader:** OCR and translation of shop boards, street names, simple signs.
4. **"What is around me":** short spoken summary on request, combining camera, location and nearby places.
5. **General sound perception:** classify everyday sounds (horn, siren, bell, speech, alarm, etc.) and alert deaf users visually and by vibration.
6. **Output router:** selects speech, large text or vibration according to the user profile.

## 5. Non-goals (v1)

- Navigation or turn-by-turn routing.
- Any "safe to cross" or hazard-clearance statements. The product describes; it never certifies safety.
- Medical, legal or financial advice.
- Sound **direction** estimation (phone mics cannot do this reliably).
- Personalised name-calling detection (deferred; see roadmap).
- Sign language recognition.
- iOS (deferred; see ADR-001).
- Training a foundation model from scratch.
- Dedicated hardware (v1 runs on a normal Android phone).

## 6. Principles

1. **Describe, never certify.** Always convey uncertainty.
2. **Never go silent on failure.** Ask the user to repeat/retake, or say what is not understood.
3. **Do not talk over the sounds the user relies on.** Prefer on-demand speech over proactive speech for blind users.
4. **Privacy by default.** Wake-word and sound classification run on-device; nothing is stored unless the user opts in.
5. **Real users shape decisions.** Validate with blind and deaf users from the first prototype.

## 7. Success measures (initial hypotheses)

| Measure | Target |
|---|---|
| Blind user obtains a correct board reading within 3 capture attempts | >= 80% of trials |
| Median time from "Hey Raahsaathi" to first spoken answer (online) | <= 5 s |
| Critical-sound alert recall (siren, close loud horn) in field recordings | >= 90%, with false alerts <= 1 per 10 min in dense traffic |
| Code-mixed command understanding (intent level) in street noise | >= 85% |
| User-reported usefulness (7-point scale) after a week | median >= 5 |

## 8. Stakeholders

Primary users, disability organisations for validation (for example blind and deaf associations), the development team, and API providers (Sarvam, Bhashini, LLM vendor).
