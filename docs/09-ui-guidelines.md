# 09 — UI Guidelines

Status: draft v0.1 for a **native Android app** (Kotlin, Jetpack Compose, Material 3). Surface mode: **Operate** (users are mid-task, often outdoors, often not looking at the screen). Product truth lives in `/PRODUCT.md`; requirements in `02-srs.md`.

> **Process note.** The Impeccable launcher could not download its engine binary in this session, so its direction roll (`concept-seed`), decision page and surface-brief tools did not run. The visual direction in section 2 was chosen by hand from the audience's own world, with no dealt alternatives. It has not been validated with users. Re-run `/impeccable shape` or `/impeccable critique` once the launcher works. `DESIGN.md` (the tokens-from-a-built-UI record) is not written yet because nothing is built; section 4 is the source of truth until then.

## 1. Who the UI is for, and where

| Persona | Primary channel | Typical moment |
|---|---|---|
| Blind (P1) | Speech and haptics; screen often off or ignored | Walking, phone on lanyard or in hand, asks a question or photographs a board |
| Low-vision (P1) | Large high-contrast text plus speech | Squinting in glare at a bus stand |
| Deaf / hard of hearing (P2) | Screen, vibration, flash | Glancing down for a horn or siren alert |
| Older adult (P3) | Large text, slow speech, few choices | Learning the app, one step at a time |
| Motor impairment | Voice, hardware keys, large targets | One hand, tremor, or single-switch use |

**Scene that drives the design:** outdoors, midday glare or dusk, traffic noise, one hand, walking pace. Anything that needs fine aiming, small text, quiet audio or a quick reaction fails here.

## 2. Visual direction: "Signboard"

**Thesis.** The app looks and speaks like Indian road and transit signage: one message per panel, big letters, pictogram plus word, extreme contrast, nothing decorative. Signage is the visual language this audience already reads and the one the app helps them read.

**Own world (recognisable with the content removed):**
- Ground is either clean white (Daylight) or deep ink (Night). Yellow is the main-action colour, as on taxi plates and tactile paving; blue is the informational colour, as on information signs; red and orange are reserved for alerts.
- Every control has a **thick dark outline** (2dp; 3dp in Max contrast). Panels are flat, edged boxes, not shadowed cards. In direct sun, edges survive where shadows and tints wash out.
- Pictogram and word always appear together. Panels hold one idea.
- No gradients, glass, glow, decorative illustration or ornamental motion.

**Colour strategy:** Restrained neutrals (white or ink), one **committed** main-action colour (signal yellow), and a small, fixed alert palette. Yellow marks "the thing to press" and nothing else.

**Why not Material You dynamic colour:** it derives colours from the user's wallpaper and cannot guarantee our contrast ratios. It is **off**.

**Honest risks.**
1. Black and yellow also reads as "caution". Mitigation: yellow only on the main action and focus; critical alerts use red plus an icon, a label and a vibration, so no meaning depends on hue.
2. Yellow on white is only 1.62:1, so a yellow button on a white ground needs its dark outline to be seen. The outline is mandatory, not decorative.
3. This is an untested direction. It needs a sunlight test and user feedback (section 13).

**Direction contract (compact):**
- THESIS: a signboard that listens: one idea per panel, loud contrast, the same patterns everywhere.
- STORY: user sees where to speak or press instantly, trusts that the answer is plain, knows how sure the app is.
- FIRST VIEWPORT (Home): status chip at the top, one full-width yellow **Talk** button in the thumb zone, three secondary actions beneath it, last result above.
- FORM: wayfinding board, thick outlines, flat panels, Material 3 structure.

## 3. Design principles

1. **One thing at a time.** One instruction, one question, one primary action per screen.
2. **Channel parity.** Whatever is spoken is also readable, and whatever is shown is also spoken or felt. No core flow needs sight or hearing alone (NFR-USE-01/02).
3. **Say how sure.** Low confidence is stated in words, not implied by styling.
4. **Never silent.** Every state change gets a sound, a vibration or a message, matched to the user's channels.
5. **Don't compete with the street.** For blind users, speech is on request; sound alerts are shown, not read aloud (FR-SND-06).
6. **Big, near, reachable.** Primary controls sit in the bottom third, one-hand reachable.
7. **No hidden gestures.** Every action is a visible, labelled control. Swipe, drag and long-press are shortcuts only.
8. **Describe, never certify.** No wording or styling that implies "safe".
9. **Consistency over novelty.** The same control looks, sounds and behaves the same everywhere.

## 4. Foundations

### 4.1 Colour tokens

All text pairs meet 7:1 (WCAG AAA). Ratios below were computed with the WCAG relative-luminance formula. Large UI parts that carry meaning meet 3:1 or better against their surroundings.

**Material 3 role mapping**

| Role | Daylight | Night | Max contrast |
|---|---|---|---|
| `primary` (main action) | `#FFC20E` | `#FFC20E` | `#FFE600` |
| `onPrimary` | `#111111` | `#111111` | `#000000` |
| `secondary` (info, links, focus in light) | `#0A3A8C` | `#8DB4FF` | `#40E0FF` |
| `onSecondary` | `#FFFFFF` | `#111111` | `#000000` |
| `surface` / background | `#FFFFFF` | `#0E1116` | `#000000` |
| `surfaceContainer` | `#EEEEE8` | `#1A1F27` | `#000000` |
| `onSurface` | `#0E1217` | `#F2F4F7` | `#FFFFFF` |
| `onSurfaceVariant` (secondary text) | `#3B4350` | `#B9C1CC` | `#FFFFFF` |
| `outline` (control and panel borders) | `#111111` | `#F2F4F7` | `#FFFFFF` |
| `error` | `#AB1219` | `#FF7A7A` | `#FF7A7A` |
| `onError` | `#FFFFFF` | `#111111` | `#000000` |
| Focus ring | `#0A3A8C` | `#FFFFFF` | `#FFFFFF` |

**Alert palette (custom roles)**

| Tier | Meaning | Daylight fill / text | Night fill / text |
|---|---|---|---|
| `alertCritical` | siren, alarm | `#AB1219` / `#FFFFFF` | `#FF7A7A` / `#111111` |
| `alertHigh` | close loud horn | `#FF8A1F` / `#111111` | `#FF8A1F` / `#111111` |
| `alertInfo` | bell, knock, announcement | `#0A3A8C` / `#FFFFFF` | `#8DB4FF` / `#111111` |

**Computed contrast (text/foreground on its background)**

| Pair | Ratio |
|---|---|
| Body text `#0E1217` on `#FFFFFF` | 18.8:1 |
| Secondary text `#3B4350` on `#FFFFFF` / on `#EEEEE8` | 10.0:1 / 8.6:1 |
| `#111111` on yellow `#FFC20E` | 11.7:1 |
| White on blue `#0A3A8C` | 10.5:1 |
| White on critical red `#AB1219` | 7.4:1 |
| `#111111` on orange `#FF8A1F` | 8.0:1 |
| Night: `#F2F4F7` on `#0E1116` / secondary `#B9C1CC` | 17.2:1 / 10.4:1 |
| Night: yellow `#FFC20E` on `#0E1116` | 11.7:1 |
| Night: `#111111` on `#FF7A7A` / on `#8DB4FF` | 7.5:1 / 9.1:1 |
| Max: yellow `#FFE600` on black | 16.6:1 |
| Yellow `#FFC20E` vs white (the reason for the outline) | **1.6:1** |
| Disabled `#5B6470` on white (informational only) | 6.0:1 |

**Colour rules**
- Never use colour as the only signal. Every colour state also has an icon, a word and, where relevant, a vibration.
- Avoid red-versus-green distinctions; the palette relies on lightness, shape and text. Blue/yellow carries the main contrast.
- Yellow is the main-action fill only. Do not use it for decoration, headings or inactive states.
- Disabled controls keep their outline, drop the fill, and say why in text ("Unavailable offline").
- Three schemes: **Daylight** (default), **Night**, **Max contrast** (a Settings toggle). Night follows the system unless overridden. Dark theme is designed, not inverted.

### 4.2 Typography

- **Roboto** is the UI face for Latin. Indic scripts use the system **Noto Sans** family fallbacks (free, OFL licence; verify per script on target devices). No custom display face.
- Sizes in **sp**, never px, so they follow the system font setting. Support the system font scale up to **200%** (verify the platform maximum), plus Android display size.
- Larger than Material defaults: body is 18sp, not 16sp.

| Role | Size / line height | Weight | Use |
|---|---|---|---|
| Display | 40 / 48 sp | Bold | Onboarding questions, big alert label |
| Headline | 28 / 36 sp | Bold | Screen titles, reading result text |
| Title | 22 / 30 sp | Medium | Panel titles, button labels |
| Body | 18 / 28 sp | Regular | All running text |
| Label | 16 / 24 sp | Medium | Chips, captions, metadata |

- Essential information is never below 16sp. Smaller text (down to 14sp) is allowed only for non-essential metadata and must still pass contrast.
- Reading-result text defaults to 28sp and is user-adjustable from 18sp to 40sp.
- **Indic scripts:** add about +10% line height (body 18 / 30 sp or more) so matras and conjuncts are not clipped; **never apply letter-spacing** (tracking breaks conjuncts); do not use all-caps or faux-bold/italic on scripts that have no such forms; avoid text-overflow ellipsis on essential strings.
- Line length 45-65 characters for prose; left-aligned; no justified text.
- Tag **language spans** on mixed-language strings so TalkBack and TTS pick the right voice (see 5.3).

### 4.3 Layout and spacing

- 8dp base unit. Screen margins 16dp. Gap between tappable controls at least **12dp**.
- **Touch targets:** 48dp is the platform minimum; our standards are:
  - standard controls **56dp**,
  - primary buttons **72dp** high, full width,
  - the **Talk** button **88dp** high, full width,
  - shutter button **96dp**.
- **Thumb zone:** primary actions and the Talk button sit in the bottom third. Titles and status sit at the top. Nothing essential at the extreme edges.
- Edge-to-edge with window insets for status bar, navigation bar, cutouts and keyboard. Content never hides behind system bars.
- Single column on phones; no horizontal scrolling; content reflows at 200% text. Long content scrolls vertically with a visible scrollbar.
- Phones only in v1. Tablets and foldables get a centred single column (max 600dp) until designed.

### 4.4 Shape, outline, elevation

- Corner radius 12dp (controls) and 16dp (panels).
- **2dp outline** (`outline` role) on every button, panel and input; 3dp in Max contrast. Separation comes from outlines and spacing, not shadows or tint.
- Tonal elevation only for sheets and dialogs; no drop shadows.
- Focus ring: 4dp in the focus colour with a 2dp gap, never removed or clipped.

### 4.5 Iconography

- Material Symbols (rounded, weight 600+, free under Apache 2.0, verify), 32dp in buttons, 48dp in alert cards.
- Icons always appear with a text label. Icon-only buttons are not allowed except the standard system Back and Close, and they still carry a content description.
- One icon per concept across the app (mic = talk/listening, camera = read, ear = sounds, info = around me, pause, settings).

### 4.6 Motion

- Motion conveys state only. 150-250 ms, standard Material easing.
- Honour the system **Remove animations** setting with an instant cut or crossfade. No page-load choreography.
- **Flashing:** screen or torch flash alerts must not flash more than **3 times per second** and not cover a large area at high contrast (seizure safety, WCAG 2.3.1). Default: two slow pulses, user can switch flash off.
- Listening and processing indicators are shown by shape and text, not only motion (a static "Listening" label plus a simple level bar).

### 4.7 Haptic vocabulary (proposed; tune on real devices)

Distinct rhythms, not just strength. Patterns in ms (on-off-on...), amplitude scaled by the user's intensity setting. Android `VibrationEffect` support varies by OEM and amplitude control is not universal (verify).

| Event | Pattern | Feel |
|---|---|---|
| Listening starts | 40 | One tick |
| Listening ends / command accepted | 30-60-30 | Two ticks |
| Photo captured | 60 | One firm tap |
| Retry needed | 80-80-80-80-80 | Three short stutters |
| Success / answer ready | 40-60-120 | Rising |
| Error | 300 | One long |
| Alert: info (bell, knock) | 100 | One medium |
| Alert: high (close horn) | 150-100-150 | Double |
| Alert: critical (siren, alarm) | 600-200-600-200-600 at max amplitude | Triple long |

### 4.8 Sound vocabulary (earcons)

Short (under 400 ms), plain, non-musical tones, independent of TTS so users can learn them. Never required alone; always paired with a haptic or text.

| Event | Earcon |
|---|---|
| Ready / listening | Two ascending notes |
| Done listening | One descending note |
| Success | One soft chime |
| Retry | Two low short notes |
| Error | Descending two-note |

Volume follows media volume; they must be audible with a bone-conduction headset. All earcons can be turned off, haptics remain.

## 5. Accessibility specification

### 5.1 TalkBack contract (Compose)

- Every interactive element: meaningful `contentDescription` or visible label that reads well; role set (`Role.Button`, `Role.Switch`, etc.); state announced ("On", "Selected").
- **Headings** marked with `heading()` on each screen section so users can jump by headings.
- **Reading order** follows visual order; the main action is early in focus order but the screen title is first.
- **Live regions:** guidance and status text use `liveRegion` — *Polite* for progress, *Assertive* only for critical alerts and capture guidance that needs immediate action.
- Do not rely on visual layout to group related text; merge descendants into one focusable item where it reads better ("Bus 500D, to Majestic, platform 2").
- Custom actions (`customActions`) expose secondary actions on result items (Repeat, Slower, Explain simpler) so TalkBack users need not hunt for buttons.
- Focus moves predictably after every action (to the new content or back to the trigger) and never to a hidden element.
- No time-limited content that disappears before it can be read; any timeout is announced and extendable.
- Test with TalkBack on, screen curtain on (screen off), and with Voice Access.

### 5.2 Visual access

- Text contrast 7:1 target; non-text contrast 3:1 minimum.
- Layouts verified at font scale **2.0** and large display size: no clipped or overlapping text, no essential information behind truncation, all actions still reachable by scrolling.
- Support Android's bold text, high-contrast text and colour-correction settings by not overriding them.
- No information conveyed only by position, hue or animation.

### 5.3 Language and speech in the UI

- Declare the language of every string and mark mixed-language spans so TalkBack/TTS choose the right voice (Android locale spans). A Hindi sentence with an English shop name must not be read with one voice that mangles the other.
- TTS rate setting is available on every screen where speech plays (Slower / Faster stepper). Default rate for older adults is slower, set during onboarding.
- Never use abbreviations or symbols that read badly aloud ("&" as "and", times as "9:30 am").

### 5.4 Motor access

- No drag-only, swipe-only, pinch, double-tap-and-hold or precision gestures. Sliders are replaced by **minus / plus steppers** with the value in text.
- Supports **Switch Access** (all controls focusable, sensible order, no hidden gesture layers) and **Voice Access**.
- Hardware shortcuts: volume key double-press = Talk; volume key during capture = shutter; headset button = Talk. All are configurable.
- Large debounce tolerance: ignore accidental double-taps and accept slow, deliberate presses; no press-timing requirements.

### 5.5 Hearing access

- Every sound and speech output has a text equivalent on screen; spoken answers are shown as captions with adjustable size.
- Alerts use icon, text, vibration, and optional flash. Captions of speech (when enabled) show speaker-agnostic text with a timestamp; no reliance on audio cues to continue.

### 5.6 Cognitive and older-adult access

- Plain language, short sentences, no jargon, consistent terms (see section 7).
- One decision per screen in onboarding and settings.
- Undo or easy back-out for every action; confirm before consequential actions only.
- "Explain simpler" is always one press away on results.
- Optional **Simple mode**: hides everything except Talk, Read and Sounds, with larger text.

## 6. Multimodal output rules

| Situation | Blind / low-vision | Deaf / hard of hearing | Older adult |
|---|---|---|---|
| Answer ready | Speech (TTS) plus success haptic; text shown large | Large caption text plus success haptic | Slow speech plus large text |
| Needs retake | Spoken instruction plus retry haptic plus banner text | Banner text plus retry haptic | Spoken and written, one instruction |
| Sound alert | Haptic only; "What was that sound?" on request | Alert card plus pattern haptic plus optional flash | Alert card plus haptic |
| Error | Spoken cause and next step plus error haptic | Written cause and next step plus error haptic | Spoken and written |
| Confirmation | Spoken question; answer by voice or large buttons | Written question; large buttons | Both, slower |

Profile fields select the defaults (`docs/03` section 7). Users can override any channel. The inference of a possible condition (FR-PRO-03) may *suggest* a change in a dismissible card ("Turn on larger text? Yes / No") and never changes anything silently.

## 7. Voice and copy guidelines

**Tone:** calm, plain, respectful. Active voice, short sentences, second person. No exclamation marks, no emoji, no jokes, no baby talk. Treat the user as an expert in their own life.

**Rules**
1. Default spoken response is **25 words or fewer** (FR/NFR-USE-03).
2. Lead with the answer, then the caveat. Put the most important word first.
3. State uncertainty in words: "I think this says…", "I can't read the last word."
4. One action per instruction: "Move the phone a little to the right."
5. Direction words are relative to the phone: up, down, left, right, closer, farther.
6. Errors say what happened, what it affects and what to do, in two sentences at most. Never blame the user.
7. Do not say "safe", "clear", "all good" about the surroundings. Use "I can see…" or "I can read…".
8. Use "press" for buttons in text; in speech say what to do ("Say 'yes' or press Yes").
9. Keep the same term for the same thing everywhere (Talk, Read, Around me, Sounds).

**Patterns (English; Hindi drafts need native-speaker review)**

| Situation | English | Hindi (draft) |
|---|---|---|
| Confirm | "Did you say: read this board?" | "क्या आपने कहा: यह बोर्ड पढ़ो?" |
| Retake, cut off | "The text is cut off on the right. Move the phone a little to the right." | "दाईं ओर का लिखा कट गया है। फ़ोन को थोड़ा दाईं ओर ले जाइए।" |
| Too dark | "It is too dark. Turn on the torch or move to a brighter spot." | "बहुत अँधेरा है। टॉर्च जलाइए या रोशनी में जाइए।" |
| Low confidence | "I think this says Sharma Medical. I'm not sure about the first word." | "शायद यह 'शर्मा मेडिकल' है। पहले शब्द पर पक्का नहीं हूँ।" |
| Try limit reached | "I couldn't get a clear photo after three tries. Here is what I could read." | "तीन कोशिशों के बाद भी साफ़ फ़ोटो नहीं मिली। जितना पढ़ पाया, वह यह है।" |
| Offline | "No internet. I can still read English and Hindi boards and listen for sounds." | "इंटरनेट नहीं है। मैं अंग्रेज़ी और हिंदी बोर्ड पढ़ सकता हूँ और आवाज़ें सुन सकता हूँ।" |
| Not understood | "I didn't catch that. Please say it again." | "मैं समझ नहीं पाया। कृपया फिर से कहिए।" |

Avoid gendered self-reference in Hindi where possible (the examples above use a masculine form for brevity; the final copy should be gender-neutral or user-selectable).

## 8. Components

All components are Material 3 components, themed by the tokens above, with the exceptions noted.

| Component | Spec | Notes |
|---|---|---|
| **Talk button** | Filled, yellow, full width, 88dp, 2dp outline, mic icon 40dp plus label "Talk" at 28sp bold | Always in the same place. States below. |
| **Primary button** | Filled yellow, 72dp, label 22sp | One per screen |
| **Secondary button** | Outlined, 56dp, label 18sp | Outline in `outline` role |
| **Tonal button** | `surfaceContainer` fill, 2dp outline | Low-emphasis actions |
| **Text button** | Link-style, 56dp touch area, underlined, `secondary` colour | Never the only way to proceed |
| **Icon + label tile** | 72dp+ tall, icon 32dp above or beside label | Home secondary actions |
| **Status chip** | Min 48dp, icon plus text, always on top of Home and Capture | States below |
| **Panel** | 16dp radius, 2dp outline, `surfaceContainer` fill, 16dp padding | Replaces shadowed cards |
| **Reading panel** | Panel with large text, type tag, confidence line, action row | Result screen |
| **Alert card** | Full width, min 96dp, tier fill from the alert palette, icon 48dp, label Display/Title, subtext Label | See 9.6 |
| **Guidance banner** | Solid scrim (80% black) over camera, text 24sp bold white, icon | Capture screen |
| **Stepper** | Minus / value text / plus, each 56dp | Replaces sliders |
| **Switch row** | 72dp row, label, state word "On / Off", M3 Switch | Never colour-only |
| **Inline confirm** | Modal bottom sheet, question text, two 72dp buttons (Yes / No) | Voice answer also accepted |
| **Banner (inline error)** | Panel with error icon, two-line message, one action | Replaces snackbars for errors |
| **Top app bar** | Small, title 22sp, Back and Settings with labels | |
| **Snackbar** | Only for non-critical confirmations; indefinite duration when it has an action | Also announced via TalkBack; never the only record |

**Navigation:** no bottom navigation bar. The app is a shallow hub: Home plus Settings and Alert history reached from the top bar. System Back always works, including predictive back. Reason: three persistent destinations add focus stops for TalkBack and clutter the thumb zone.

**State matrix** (every interactive component ships all of these): default, hover/pressed, focused, disabled, loading, error. Disabled controls keep their outline and say why.

**Talk button states**

| State | Visual | Text | Audio / haptic |
|---|---|---|---|
| Idle | Yellow | "Talk" | none |
| Listening | Yellow with thicker outline plus level bar | "Listening…" | ready earcon, tick |
| Processing | Outlined, spinner replaced by animated dots plus text | "Working…" | progress cue after 2 s |
| Speaking | Stop icon | "Stop" | speech |
| Error | Outlined with error icon | "Try again" | error earcon, long vibration |
| Paused / offline | Outlined, strikethrough mic | "Mic paused" / "Offline" | none |

**Status chip states:** Listening for sounds, Paused, Offline (limited), Battery saver may stop me. Each has icon, text and a TalkBack state.

## 9. Screens

### 9.1 Onboarding
- One question per screen. Progress spoken and written ("Step 2 of 5"). Back always available; entire flow works eyes-closed and sound-off.
- Steps: **Language** (each language written in its own script and spoken on focus) → **How should I tell you things?** (Speech, Large text, Vibration; multi-select, no medical labels) → **Text size and speech speed** with live sample → **Permissions** (microphone, camera, notifications; location optional), each with a one-sentence plain reason and a clear "Not now" → **Safety note**: "I describe what I see and hear. I am not a safety system. Please keep using your cane, guide or other help." → **Keep me running** (battery settings help, OEM-specific steps).
- Consent for optional data (location, telemetry, feedback export) is separate, off by default.

### 9.2 Home
- Top: status chip (left), Language chip and Settings button (right).
- Middle: last result or alert as a Panel; empty state teaches: "Say 'Read this board' or press Talk."
- Bottom third: Talk button (88dp), then a row of three tiles: **Read** (camera), **Around me** (info), **Sounds** (ear; toggles listening; label shows On/Off).
- Alert history and Settings via the top bar.

### 9.3 Capture
- Full-screen camera preview. All controls sit on solid scrims, never directly on the image.
- Top: guidance banner (assertive live region) and an attempt chip "Try 1 of 3".
- Detected text: 4dp yellow outline around boxes for low-vision users.
- **Cut-off cue:** a 16dp orange bar on the edge where text is cut, an arrow icon, and the words, plus a spoken hint.
- Bottom: Torch (56dp), Shutter (96dp, labelled "Take photo"), Close. Volume key also takes the photo.
- After capture: haptic tap, "Checking the photo…", then a single instruction or the result. After 3 tries, best-effort result with the caveat.
- Live-preview auto-capture (Phase 3) uses the same layout with an "Auto" toggle.

### 9.4 Result
- Title with type tag ("Shop board", "Street name", "Notice").
- Main text at 28sp in a Reading panel; translated text first, **Original** a toggle. Uncertain words get a dashed underline *and* the word "(unsure)" in the text equivalent and are read as "maybe".
- Confidence line when below threshold: "I'm not sure about this reading."
- Actions: **Play/Stop** (primary), **Slower** / **Faster** (stepper), **Explain simpler**, **Repeat**, **Retake**.
- Opens with focus on the result text so TalkBack reads it first.

### 9.5 Around me
- A short summary (three sentences at most) in a Reading panel; a note of what it was based on ("From the photo and nearby places").
- Never uses reassurance wording (principle 8). Follow-up by voice: "What does the board on the left say?".

### 9.6 Sound alerts
- Alert card appears at the top of Home or over any screen as a heads-up; critical alerts also use a high-priority notification when the app is in the background.
- Anatomy: tier fill, icon 48dp, label ("Siren"), subtext ("Just now · loud"), tier name as text.
- **De-duplication:** repeated sounds merge into one card with a counter ("Horn ×4 in 10 seconds").
- Persistence: critical stays until dismissed or 30 s, info fades to history after 8 s. History lists the last 20, newest first, with timestamps and a **Wrong / Missed** feedback action.
- Blind users: no automatic speech; "What was that sound?" reads the latest entry.
- Never claim completeness: a persistent line in Sounds settings reads "I may miss sounds. Please stay alert."

### 9.7 Confirmation
- Inline bottom sheet: "Did you say: '…'?" with Yes and No, also answerable by voice. After 6 s of silence, one spoken re-prompt, then cancel. Shown only when confidence is low or the action is consequential (FR-VOI-05).

### 9.8 Settings
- Grouped list with headings: Language, Voice (speed, voice), Display (text size, theme: Daylight / Night / Max contrast, Simple mode), Alerts (per sound on/off, sensitivity, vibration strength, flash on/off), Privacy (pause listening, delete my data, what is sent), Help and limits (what I can't do, safety note, report a problem).
- Rows are 72dp; values shown as text; steppers instead of sliders.

### 9.9 Persistent notification
- Required for the foreground service. Title "Listening for sounds"; actions **Pause** and **Stop**; high-contrast monochrome small icon; text states what is active (mic only; nothing recorded).

## 10. States and edge cases

| Case | Behaviour |
|---|---|
| Permission denied | Explain what stops working and offer the system settings link; offer push-to-talk-free alternatives (typing) |
| Offline | Chip shows Offline; local OCR and sound alerts keep working; features that need the network say so, once |
| Provider failure | One retry, failover, then plain message and an on-device alternative (FR-ERR-01) |
| Low battery / battery saver | Message and suggest turning listening off; do not silently degrade |
| Microphone in use by another app | Say so and offer to retry |
| Very long text (e.g. a notice) | Summarise first with "Read it all?"; paging for read-aloud with Next / Previous |
| Mixed scripts | Per-span languages; show original beside translation |
| No text found | "I can't see any text. Move closer or aim at the board." |
| First launch with TalkBack on | Onboarding starts speaking immediately; no visual-only gate |
| RTL languages (e.g. Urdu, if added) | Mirrored layouts, direction-aware icons; test separately |
| Font scale 2.0 | Controls grow; no truncation of essential text; vertical scroll |

## 11. Localisation

- All strings in resources per locale; no string concatenation for sentences; plurals via resources.
- Plan for 30-40% text expansion and taller line heights for Indic scripts; test with a pseudo-locale and with real Hindi, Tamil and Bengali strings.
- Language names are written in their own script ("हिन्दी", "தமிழ்") in the selector.
- Numerals: follow the user's choice (Western or native digits), applied consistently in text and speech.
- Copy review by native speakers for every shipped language, preferably users with disabilities.

## 12. Android platform rules

- Material 3 structure and components; predictive back supported; edge-to-edge with insets; sp for type; 48dp minimum targets (we exceed it).
- Foreground service notification follows platform requirements; microphone foreground-service type and permissions as in `docs/04`.
- Dynamic colour **off** (see section 2). Dark theme is a full scheme.
- Respect system settings: font size, display size, bold text, remove animations, colour correction, TalkBack, Switch Access, Voice Access.
- Phones only in v1. Orientation: portrait primary; landscape must still work (camera screen).

## 13. Quality checks and testing

**Before each release (design QA)**
1. Contrast: re-verify all token pairs; spot-check screens with Accessibility Scanner (Google, free; verify availability).
2. Font scale: set `adb shell settings put system font_scale 2.0` (read the original first with `settings get` and restore it afterwards); walk every screen for clipping.
3. Dark theme: `adb shell cmd uimode night yes` (restore after) and Max contrast mode on every screen.
4. Animations off: `adb shell settings put global animator_duration_scale 0` (restore afterwards); confirm no state depends on motion.
5. Colour-blindness: enable the developer-options colour simulation and check alerts remain distinguishable.
6. TalkBack walkthrough of every core flow, eyes-closed (screen curtain) and sound-off.
7. Voice Access and Switch Access smoke tests.
8. Screenshots from an emulator or device, not a browser; say which produced the evidence. Gestures, haptics and performance need physical devices.

**Field and user checks**
- **Sunlight test:** read every screen outdoors at midday on a mid-range phone; log anything unreadable.
- **One-hand walking test** with a spotter: can the user reach Talk, Read and Stop?
- **Haptic test** on at least three OEM devices: are patterns distinguishable?
- **Earcon test** with a bone-conduction headset.
- User sessions with blind, low-vision and deaf participants (`docs/06` section 6); record task success, attempts, and misread alerts; adjust tokens, sizes and patterns from the findings.

## 14. Open decisions

| # | Decision | Notes |
|---|---|---|
| U1 | App name and logo | Wake phrase constraint (`docs/08` Q2); logo must work in monochrome at 48dp |
| U2 | Final icon set | Material Symbols assumed; check coverage for "ear", "around me" |
| U3 | Earcon and haptic design | Needs a sound designer or careful in-house design, then device testing |
| U4 | Hindi and other-language copy | Native review needed; gender-neutral phrasing |
| U5 | Heads-up/overlay alerts over other apps | Platform policy and permission implications; may be limited to notifications |
| U6 | Whether Daylight stays the default theme | Test in sun against Night with real users |
| U7 | Simple mode scope | What exactly it hides; test with older adults |
| U8 | Screen-off use | How much of the app should work with the screen locked or off |
| U9 | Tablet and foldable support | Deferred past v1 |
| U10 | 2 GB Lite mode UI differences | Depends on memory decision (`docs/08`) |

## 15. Traceability

FR-PRO-01, FR-OUT-01 to 06, FR-CAM-01 to 08, FR-SND-04 to 06, FR-VOI-01 to 05, FR-ERR-04, NFR-USE-01 to 04, FR-PRV-04 are the requirements this document designs against. If a screen here conflicts with `02-srs.md`, the SRS wins until the conflict is resolved.
