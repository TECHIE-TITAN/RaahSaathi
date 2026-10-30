# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Stack

Native Android: Kotlin, Jetpack Compose, Material 3 (confirmed by the user; supersedes the "Proposed" status of ADR-001 in `docs/08`). Backend and ML stack are in `docs/03` and `docs/04`.

## Users

People who are blind or low-vision, and people who are deaf or hard of hearing, moving through everyday Indian surroundings (streets, markets, bus stands, stations). Many speak mainly an Indian language, often code-mixed with English. Older adults are a secondary audience. Deafblind users are a later stretch. Situations: walking outdoors in bright sun and noise, one hand often occupied, phone in pocket, on a lanyard, or held up to read a board.

## Product Purpose

A phone assistant that turns the surroundings into whatever channel the user can perceive: spoken description for those who cannot see, large text, icons and vibration for those who cannot hear. First capabilities: voice commands in Indian languages, guided camera capture and reading of shop boards and street names, a "what is around me" summary, and general sound-event alerts. Success: the user gets a correct, understandable answer or alert without needing to see or hear the other channel.

## Positioning

One app that serves both sight and hearing needs, in Indian languages and code-mixed speech, and coaches a blind user to take a usable photo. Existing tools serve one sense, assume English, and assume the user can frame a camera or see an alert.

## Operating Context

Outdoor, noisy, bright, one-handed, walking or standing at a kerb or stop. Often a mid-range or low-end Android phone with a 3 GB RAM minimum (2 GB "Lite" mode under evaluation), intermittent data, aggressive OEM battery management. Used with TalkBack, with a headset or bone-conduction device, or by voice. Free-only component stack (ADR-011); hosting deferred.

## Capabilities and Constraints

- Voice: push-to-talk first; wake phrase "Hey Raahsaathi" in a later phase; code-mixed speech through Bhashini (primary, free), with on-device fallbacks.
- Camera: on-device quality gate, one instruction per attempt, three attempts maximum, then best-effort answer with a caveat.
- Sound: general on-device classification, priority tiers, de-duplication; alerts are visual and haptic for deaf users, on-demand only for blind users.
- The app describes; it never certifies safety. No "safe to cross" statements, no medical, legal or financial advice.
- Not in v1: navigation, sound direction, name-call detection, sign language, iOS, tablets.
- Undecided: launch languages beyond English and Hindi, whether a vision-language model is used, 2 GB Lite mode. See `docs/08`.

## Brand Commitments

Name: **Raahsaathi** (Devanagari: राहसाथी), "companion of the road"; wake phrase "Hey Raahsaathi" (decided 2026-10-31). Always spell it "Raahsaathi" in Roman script. No logo, palette or existing look to keep (confirmed by the user). Tone: a companion that is with the user, never one that promises to lead them or keep them safe.

## Evidence on Hand

No user research, recordings, photos or usage data yet. The docs in `docs/` are planning artefacts. Nothing may be presented as validated user feedback until the Phase 0 interviews are done.

## Product Principles

1. Describe, never certify; always convey uncertainty.
2. Every output reaches the user in a channel they can perceive; no core flow requires seeing or hearing.
3. Never fail silently: ask the user to repeat, retake or confirm.
4. Do not talk over the sounds a blind user relies on; speech is on demand by default.
5. Privacy by default: wake word and sound classification stay on the device.

## Accessibility & Inclusion

Target WCAG 2.2 AA at minimum, with 7:1 text contrast as the app's own bar. Works with TalkBack and Switch Access, font scale up to 200%, Remove animations, and without colour as the only signal. Core flows completable eyes-closed and sound-off (NFR-USE-01/02). Indian scripts and code-mixed text must render and be spoken correctly. Co-design with blind, low-vision and deaf users is a requirement, not a nice-to-have.
