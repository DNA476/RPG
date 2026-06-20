# Roadmap

This roadmap is ordered by dependency and risk, not by marketing priority.
Statuses describe repository state as of 2026-06-19.

## Completed Foundation

- [x] Create multi-module Android project: `:app`, `:domain`, `:game`, `:data`,
  and `:pose`.
- [x] Build Compose battle and victory UI.
- [x] Integrate CameraX front-camera preview.
- [x] Integrate on-device MediaPipe Pose Landmarker.
- [x] Convert MediaPipe output to framework-independent pose models.
- [x] Implement squat state machine using hip-knee-ankle angles.
- [x] Emit typed exercise events.
- [x] Map exercise repetitions to attacks and flat damage.
- [x] Implement a training boss, HP flow, victory, and reset.
- [x] Add debug frame-source abstraction.
- [x] Add debug video input mode for deterministic manual testing.
- [x] Add a visible goblin enemy and synchronized hit-reaction animation.
- [x] Add a main menu with seven exercise cards and explicit selection.
- [x] Add scalable exercise content models and a central `ExerciseCatalog`.
- [x] Add `ExerciseDetectorFactory` with a ready squat detector and initially
  safe experimental placeholders.
- [x] Calculate damage from the selected exercise configuration.
- [x] Add menu -> battle -> victory -> menu navigation.
- [x] Add a debug-only simulated repetition action.
- [x] Add unit coverage for detector construction, squat repetition, catalog
  completeness, configured damage, and victory.
- [x] Add six configurable enemies with generated portraits.
- [x] Add fixed random three-enemy selection after exercise selection.
- [x] Guarantee at least one non-resistant matchup in every trio.
- [x] Add exercise weaknesses and non-blocking resistances.
- [x] Add timed enemy abilities that reduce player attack instead of player HP.
- [x] Add an extensible enemy attack timing policy.
- [x] Add `CODEX_RULES.md` for repository-wide development and delivery rules.
- [x] Add optional first-launch profile fields for weight, height, and sex.
- [x] Persist accepted repetitions as local daily exercise aggregates.
- [x] Add statistics with 7/30/90-day periods, exercise filtering, per-type
  totals, and a daily chart.
- [x] Add approximate calorie estimation with optional weight personalization.
- [x] Add 12 calorie-based progression levels and show the current level in
  statistics.
- [x] Scale enemy HP on a cubic curve from the level-1 baseline to `3x` at
  level 12.
- [x] Keep debug-simulated repetitions out of fitness statistics.
- [x] Add functional experimental repetition state machines for pull-ups,
  crunches, and jumping jacks so every catalog exercise can be live-tested.
- [x] Use MediaPipe world landmarks with normalized 3D fallback for
  front-facing exercise geometry.
- [x] Remove horizontal screen-orientation requirements from push-up and plank
  detection and support knee or ankle body endpoints.
- [x] Move profile and statistics navigation into a main-menu drawer.
- [x] Add a clickable main-menu card for today's approximate calories.
- [x] Add settings with an in-app language selector.
- [x] Add a locally persisted inventory and equipment screen with body,
  weapon, and artifact slots.
- [x] Add button and swipe navigation between inventory and equipment.
- [x] Expand the catalog to 36 items with seven bonus types and
  item-specific scalable Compose icons.
- [x] Add three locally persisted weekly quest categories with required
  exercises, progress, restrictions, and fixed rare/epic/legendary rewards.
- [x] Add a three-week test quest rotation covering the current ISO week and
  two future weeks.
- [x] Add a sword-in-shield quest entry beside the inventory button.
- [x] Start players with zero artifacts and grant non-quest artifacts from
  resistant-matchup victories.
- [x] Add a custom adaptive launcher icon for APK builds.
- [x] Keep bottom UI actions above the Android system navigation bar.
- [x] Add experimental live detectors for push-up, lunge, and plank so they can
  produce battle attacks during real camera sessions.
- [x] Promote inventory into the main progression loop with a six-item starter
  loadout, deterministic victory rewards, named loot on victory, and save
  migration from prototype ownership.
- [x] Apply all seven bounded equipment bonus types to combat damage and timing.
- [x] Replace the equipment mannequin with original generated Vitruvian-style
  loadout art.

## Current Priority: Make The Vertical Slice Reliable

- [ ] Add unit tests for `SquatDetector` with synthetic pose sequences.
- [x] Add unit tests for `BattleSession`, tactical damage, victory, and enemy
  selection fairness.
- [ ] Add a committed or documented test-video workflow for debug builds.
- [ ] Verify camera rotation and front-camera mirroring on representative
  devices.
- [ ] Verify debug-video orientation and skeleton alignment.
- [ ] Test lifecycle transitions: background, resume, input switch, reset.
- [ ] Expand project-specific coverage beyond detector, combat, data, and
  catalog rules into ViewModel and Compose navigation tests.
- [ ] Add structured input/pose error reporting instead of display-only strings.
- [ ] Measure dropped frames and inference latency on a low/mid-range device.

Exit condition: a recorded squat sequence and a live-camera session both produce
stable, explainable repetition counts and complete a battle without duplicate
damage.

## Next: Detection Quality

- [ ] Add temporal smoothing for noisy landmarks/angles.
- [ ] Add hysteresis and configurable minimum phase duration.
- [ ] Add cooldown/debounce protection against repeated events.
- [ ] Distinguish poor visibility from insufficient squat depth.
- [ ] Add player calibration for camera position and safe personal range.
- [ ] Define structured form/validation feedback.
- [ ] Evaluate side-view versus front-view detection assumptions.
- [ ] Add detector telemetry usable in local debug tooling.

Exit condition: common invalid or ambiguous movements produce clear feedback and
do not count as valid attacks.

## Then: Scalable Session Architecture

- [x] Introduce detector factory/registry construction.
- [ ] Introduce a session configuration model.
- [ ] Extract enemy attack scheduling from `BattleViewModel`.
- [ ] Use exercise difficulty and an optional player fitness profile in
  `EnemyAttackTimingPolicy`.
- [ ] Extract battle/session construction from `BattleViewModel`.
- [ ] Depend on narrow frame/pose contracts at the UI boundary.
- [ ] Add pause/resume/stop lifecycle.
- [ ] Add typed recoverable errors and retry actions.
- [ ] Move content configuration to JSON assets or Room.
- [ ] Add ViewModel tests with fake pose and detector sources.

Exit condition: a session can select content and exercises without hard-coded
changes across the ViewModel and UI.

## Content And Game Expansion

- [ ] Calibrate and validate lunge, push-up, and plank detection against real
  camera sessions and recorded video.
- [ ] Implement and validate pull-up, crunch, and jumping-jack detection.
- [ ] Add distinct attack feedback for basic, heavy, and critical attacks.
- [x] Add multiple enemies and configurable encounters.
- [ ] Add enemy phases or exercise prompts.
- [ ] Define fair player damage/defeat rules, if defeat remains in scope.
- [ ] Add rewards, experience, levels, and achievements.
- [x] Connect resistant-matchup victories to prototype artifact rewards.
- [x] Prototype weekly quests and reserve the highest item rarity for quest
  rewards.
- [ ] Define production quest rotation, reward pools, duplicate handling, and
  long-term balance.
- [ ] Playtest equipment acquisition pacing and tune bonus values/caps.
- [ ] Expand inventory into a broader equipment/skills build loop.
- [ ] Add audio, haptics, animation, and accessibility settings.

## Production Readiness

- [ ] Establish product analytics that do not collect camera frames.
- [ ] Add privacy disclosures and data-retention rules.
- [ ] Add crash/performance monitoring.
- [x] Add Russian, English, German, Spanish, French, and Portuguese
  localization and remove hard-coded user-facing strings from the app layer.
- [ ] Add accessibility review for UI and exercise alternatives.
- [ ] Add release signing, CI, lint, unit tests, and instrumentation gates.
- [ ] Define supported devices, Android versions, camera requirements, and
  performance floor.
- [ ] Run a safety and claims review before public distribution.

## Deferred Until Product Decisions Exist

- Backend/accounts/cloud sync.
- Social competition and leaderboards.
- Monetization.
- Multiplayer.
- Health platform integration.
- Procedural campaigns.
- Medical or rehabilitation use cases.

## Decision Log

- 2026-06-19: Prefer MediaPipe world landmarks for detector geometry while
  retaining normalized coordinates for UI overlays and as a fallback.
- 2026-06-19: Optimize experimental detectors for front-facing tests without
  removing side-view compatibility; keep all new paths marked experimental
  until recorded and live calibration is complete.
- 2026-06-19: Derive player level from lifetime approximate calories using
  `100 * 2^level` thresholds; use level 0 before 200 kcal and cap at level 12.
- 2026-06-19: Treat catalog enemy HP as the level-1 baseline and use cubic
  scaling to reach exactly triple HP at level 12.
- 2026-06-19: Make inventory a persistent progression system: six starter
  items, one regular equipment reward per victory, existing resistant/quest
  rewards, and versioned migration from prototype ownership.
- 2026-06-19: Activate all seven equipment bonus types with caps in `:game` and
  freeze the equipped loadout at battle start.
- 2026-06-10: Keep pose inference on device.
- 2026-06-10: Preserve strict module boundaries between capture, pose,
  exercise, combat, and UI.
- 2026-06-10: Treat repetition accuracy and explainability as the first product
  milestone.
- 2026-06-10: Use camera and debug video as interchangeable frame sources in
  debug builds.
- 2026-06-13: Store the optional profile and daily activity locally without
  accounts or camera uploads.
- 2026-06-13: Record accepted live repetitions immediately; exclude debug
  simulation from user statistics.
- 2026-06-13: Present calories as estimates based on weight, configured
  intensity, and assumed repetition duration.
- 2026-06-14: Keep profile and full statistics in a main-menu drawer while
  retaining a visible daily calorie summary as the quick statistics entry.
- 2026-06-14: Follow the device language with Russian as the fallback and
  support English, German, Spanish, French, and Portuguese resources.
- 2026-06-14: Let players override the device language from settings while
  retaining the system-language option.
- 2026-06-15: Prototype inventory and equipment before reward integration;
  persist equipped slots locally and keep preview bonuses out of combat.
- 2026-06-15: Prototype three weekly quest categories with local ISO-week
  progress, battle-condition checks, direct quest encounter setup, and
  automatic inventory rewards.
- 2026-06-15: Expand the prototype inventory to 30 items and use code-drawn
  item-specific icons so the collection can grow without bitmap asset churn.
- 2026-06-16: Keep weekly quests as three active cards, but source them from a
  three-week rotation with distinct exercises and rare/epic/legendary rewards.
- 2026-06-17: Start players with zero artifacts, grant non-quest artifacts from
  resistant victories, and keep the strongest rewards quest-exclusive.
- 2026-06-17: Use a custom adaptive launcher icon so APK builds no longer ship
  the default Android placeholder icon.
- 2026-06-17: Apply the Android navigation bar inset at the Compose app root so
  bottom buttons are not covered by system controls.
