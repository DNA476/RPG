# Roadmap

This roadmap is ordered by dependency and risk, not by marketing priority.
Statuses describe repository state as of 2026-06-11.

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
- [x] Add `ExerciseDetectorFactory` with a ready squat detector and safe
  experimental placeholders.
- [x] Calculate damage from the selected exercise configuration.
- [x] Add menu -> battle -> victory -> menu navigation.
- [x] Add a debug-only simulated repetition action.
- [x] Add unit coverage for detector construction, squat repetition, catalog
  completeness, configured damage, and victory.

## Current Priority: Make The Vertical Slice Reliable

- [ ] Add unit tests for `SquatDetector` with synthetic pose sequences.
- [ ] Add unit tests for `BattleSession`, attack mapping, damage, victory, and
  reset.
- [ ] Add a committed or documented test-video workflow for debug builds.
- [ ] Verify camera rotation and front-camera mirroring on representative
  devices.
- [ ] Verify debug-video orientation and skeleton alignment.
- [ ] Test lifecycle transitions: background, resume, input switch, reset.
- [ ] Replace placeholder generated tests with project-specific coverage.
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
- [ ] Extract battle/session construction from `BattleViewModel`.
- [ ] Depend on narrow frame/pose contracts at the UI boundary.
- [ ] Add pause/resume/stop lifecycle.
- [ ] Add typed recoverable errors and retry actions.
- [ ] Move content configuration to JSON assets or Room.
- [ ] Add ViewModel tests with fake pose and detector sources.

Exit condition: a session can select content and exercises without hard-coded
changes across the ViewModel and UI.

## Content And Game Expansion

- [ ] Implement and validate lunge detection.
- [ ] Implement and validate push-up, pull-up, crunch, jumping-jack, and plank
  detection.
- [ ] Add distinct attack feedback for basic, heavy, and critical attacks.
- [ ] Add multiple enemies and configurable encounters.
- [ ] Add enemy phases or exercise prompts.
- [ ] Define fair player damage/defeat rules, if defeat remains in scope.
- [ ] Add rewards, experience, levels, and achievements.
- [ ] Add inventory/equipment/skills only after the reward loop is specified.
- [ ] Add audio, haptics, animation, and accessibility settings.

## Production Readiness

- [ ] Establish product analytics that do not collect camera frames.
- [ ] Add privacy disclosures and data-retention rules.
- [ ] Add crash/performance monitoring.
- [ ] Add localization and remove hard-coded user-facing strings.
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

- 2026-06-10: Keep pose inference on device.
- 2026-06-10: Preserve strict module boundaries between capture, pose,
  exercise, combat, and UI.
- 2026-06-10: Treat repetition accuracy and explainability as the first product
  milestone.
- 2026-06-10: Use camera and debug video as interchangeable frame sources in
  debug builds.
