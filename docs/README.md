# Raahsaathi — Pre-Build Documentation

A phone-based **situational awareness assistant** for people who are blind/low-vision or deaf/hard-of-hearing, moving through everyday Indian surroundings. It converts what the environment shows and sounds like into whatever channel the user can perceive (speech, large text, vibration), in Indian languages including code-mixed speech.

**Raahsaathi** (राहसाथी, "companion of the road") is the app name, finalised 2026-10-31 (see ADR-012 in 08). It doubles as the wake phrase ("Hey Raahsaathi").

## Document index

| # | Document | Purpose |
|---|---|---|
| 01 | [Product brief](01-product-brief.md) | Vision, users, scope, non-goals, success measures |
| 02 | [SRS](02-srs.md) | Numbered functional and non-functional requirements |
| 03 | [Design document](03-design-doc.md) | Architecture, components, flows, APIs, data model |
| 04 | [System and tech requirements](04-system-and-tech-requirements.md) | Devices, backend, libraries, models, datasets, costs |
| 05 | [Risk, safety and privacy](05-risk-safety-privacy.md) | Risk register, safety rules, privacy design |
| 06 | [Test and validation plan](06-test-validation-plan.md) | Test levels, metrics, field and user studies |
| 07 | [Roadmap and backlog](07-roadmap-backlog.md) | Phases, exit criteria, epics |
| 08 | [Decisions and open questions](08-decisions-open-questions.md) | ADR log and what still needs an answer |
| 09 | [UI guidelines](09-ui-guidelines.md) | Visual direction, tokens, accessibility spec, components, screens, copy, QA |

Product truth for design work lives in [`/PRODUCT.md`](../PRODUCT.md).

Also: [`THIRD_PARTY.md`](THIRD_PARTY.md) lists every component with licence, cost status and open licence tasks.

## Stack policy
The baseline uses **free components only** (ADR-011). Hosting is deferred. See 04 for the free stack and `THIRD_PARTY.md` for the evidence and open items.

## Conventions

- Requirement IDs: `FR-<MODULE>-nn` (functional), `NFR-<AREA>-nn` (non-functional).
- Priority: **M**ust / **S**hould / **C**ould, with the roadmap phase. Requirement tags still use legacy labels P1-P4, which now mean Phase 2-5; "Phase 0" mentions in older docs mean the benchmark/interview work now in Phase 1. Mapping table is in 07. Phase 0 is setup only.
- Numeric targets are **initial hypotheses to be validated**, not measured facts.
- Third-party model names, API names, versions, limits and prices are from memory and **must be verified against current vendor docs** before they are relied on. They are marked `(verify)`.
