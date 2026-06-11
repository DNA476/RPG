# Project Context

## Purpose

This repository contains an Android fitness game. The core interaction is:

1. The device camera observes the player.
2. On-device pose estimation converts frames into body landmarks.
3. Exercise detectors recognize valid repetitions.
4. A completed repetition becomes a game attack.
5. The attack damages an enemy and updates the battle UI.

The product goal is to make exercise sessions feel like short RPG battles rather
than passive repetition counters.

## Current Product State

The repository is an MVP/prototype, not a production-ready application.

- Platform: Android.
- UI: Jetpack Compose.
- Camera: CameraX with the front camera.
- Pose estimation: MediaPipe Pose Landmarker, processed locally on the device.
- App entry point: exercise selection menu.
- Exercise catalog: squat, push-up, pull-up, crunch, lunge, jumping jack, and
  plank.
- Ready detector: squat.
- Experimental placeholders: all other catalog exercises. They safely consume
  tracking state but do not count live-camera repetitions yet.
- Encounter selection: after choosing an exercise, the player receives three
  random enemies and chooses one. The trio has no refresh action and is cached
  for that exercise during the current app session.
- Enemy catalog: four goblin roles and two hound variants with distinct HP,
  portraits, weaknesses, resistances, and weakening abilities.
- Matchmaking guarantees at least one offered enemy is not resistant to the
  selected exercise.
- Current combat rule: one valid repetition applies exercise base damage,
  enemy affinity, and any active attack debuff. Final damage is never below 1.
- Enemy weakness multiplier: `1.5x`; resistance multiplier: `0.75x`.
- Enemies attack every 15 seconds and reduce outgoing player damage by 25% for
  10 seconds. Player HP and defeat are not implemented.
- Current end state: victory after the boss reaches zero HP.
- A valid hit shakes the goblin, flashes it red, and overlays a white sword slash.
- Persistence, accounts, progression, audio, analytics, and backend are absent.
- Debug builds can switch between live camera input and a looping video asset.
- Debug builds expose `Simulate repetition` to test every exercise, damage,
  counters, and victory without camera input.

## Source Of Truth

When documentation and code disagree, code is authoritative. Read these files
first:

- `settings.gradle.kts` for the module list.
- `app/src/main/java/com/example/rpg/ui/viewmodel/BattleViewModel.kt` for runtime
  orchestration.
- `pose/src/main/java/com/example/rpg/pose/PoseAnalyzer.kt` for pose inference.
- `data/src/main/java/com/example/rpg/data/exercise/ExerciseCatalog.kt` for
  exercise content and base damage.
- `data/src/main/java/com/example/rpg/data/enemy/InMemoryEnemyRepository.kt` for
  enemy content and random encounter selection.
- `domain/src/main/java/com/example/rpg/domain/exercise/ExerciseDetectorFactory.kt`
  for detector construction.
- `domain/src/main/java/com/example/rpg/domain/exercise/SquatDetector.kt` for
  ready repetition recognition.
- `game/src/main/java/com/example/rpg/game/battle/BattleSession.kt` for combat.
- `data/src/main/java/com/example/rpg/data` for current content/config sources.

## Important Domain Language

- Pose frame: one timestamped set of normalized body landmarks.
- Exercise detector: stateful logic that consumes pose frames and emits exercise
  events.
- Repetition: a complete valid movement cycle, not a single pose.
- Attack mapping: conversion from an exercise type to an attack type.
- Battle session: game state for one enemy encounter.
- Affinity: an exercise-specific weakness or resistance multiplier.
- Enemy ability: a timed action that currently applies a temporary attack
  reduction instead of damaging player HP.
- Frame source: producer of bitmaps for pose analysis, currently camera or debug
  video.

## Product And Engineering Invariants

- Exercise recognition must remain independent from UI and combat.
- Combat must consume domain exercise events, not camera or MediaPipe objects.
- MediaPipe and CameraX types must not leak into `:domain` or `:game`.
- Damage is applied only for `ExerciseEvent.RepetitionCompleted`.
- Enemy selection must never offer only resistant enemies.
- Resistances reduce damage but never create full immunity.
- A squat is counted only after the sequence standing -> bottom -> standing.
- Pose processing is local; do not introduce a server dependency without an
  explicit product decision.
- Debug-only input controls must not appear in release builds.
- False positive repetitions are more harmful than occasionally missed
  repetitions because they break trust in both fitness tracking and combat.
- Existing user changes in the worktree must not be discarded.

## Current Constraints And Risks

- Exercise thresholds are fixed and not calibrated per player.
- Squat detection uses 2D knee angles and landmark visibility only.
- There is no temporal smoothing, minimum movement duration, cooldown, or form
  quality score.
- Only squat has a calibrated movement state machine.
- Experimental detectors report tracking feedback but require exercise-specific
  recognition and calibration.
- Dependencies are manually constructed; there is no DI framework.
- Enemy and exercise configurations are in memory.
- Enemy attack timing is fixed at 15 seconds. The policy boundary exists, but
  exercise difficulty and player fitness are not yet inputs.
- Automated coverage is currently limited to generated placeholder tests.
- Camera, rotation, mirroring, video decoding, and MediaPipe behavior require
  device or emulator validation.
- `VideoFileFrameSource` expects `assets/raw/test_video.mp4`; the file may be
  intentionally absent from source control.

## Near-Term Direction

The next useful milestone is reliable detection beyond the menu/combat slice:

- count squats reliably across common camera positions;
- implement and calibrate one experimental detector at a time;
- validate the same flow with deterministic recorded video;
- add ViewModel and Compose navigation tests.

Progression systems and content breadth should follow reliability, not precede
it.

## Context Maintenance Rules

Future agents should update these files when behavior or direction changes:

- `PROJECT_CONTEXT.md`: product truth, current state, invariants, major risks.
- `ARCHITECTURE.md`: module ownership, data flow, contracts, technical decisions.
- `GAME_DESIGN.md`: player loop, rules, balance assumptions, design principles.
- `ROADMAP.md`: completed work, current priorities, deferred work, open decisions.

Do not use these files as release notes. Keep them concise, factual, and aligned
with the current repository.
