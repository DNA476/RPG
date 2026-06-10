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
- Implemented exercise: squat.
- Implemented encounter: one training boss with 10 HP.
- Current combat rule: one valid squat maps to a basic attack with flat damage.
- Current end state: victory after the boss reaches zero HP.
- Persistence, accounts, progression, audio, analytics, and backend are absent.
- Debug builds can switch between live camera input and a looping video asset.

## Source Of Truth

When documentation and code disagree, code is authoritative. Read these files
first:

- `settings.gradle.kts` for the module list.
- `app/src/main/java/com/example/rpg/ui/viewmodel/BattleViewModel.kt` for runtime
  orchestration.
- `pose/src/main/java/com/example/rpg/pose/PoseAnalyzer.kt` for pose inference.
- `domain/src/main/java/com/example/rpg/domain/exercise/SquatDetector.kt` for
  repetition recognition.
- `game/src/main/java/com/example/rpg/game/battle/BattleSession.kt` for combat.
- `data/src/main/java/com/example/rpg/data` for current content/config sources.

## Important Domain Language

- Pose frame: one timestamped set of normalized body landmarks.
- Exercise detector: stateful logic that consumes pose frames and emits exercise
  events.
- Repetition: a complete valid movement cycle, not a single pose.
- Attack mapping: conversion from an exercise type to an attack type.
- Battle session: game state for one enemy encounter.
- Frame source: producer of bitmaps for pose analysis, currently camera or debug
  video.

## Product And Engineering Invariants

- Exercise recognition must remain independent from UI and combat.
- Combat must consume domain exercise events, not camera or MediaPipe objects.
- MediaPipe and CameraX types must not leak into `:domain` or `:game`.
- Damage is applied only for `ExerciseEvent.RepetitionCompleted`.
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
- Only one detector is wired directly into `BattleViewModel`.
- Dependencies are manually constructed; there is no DI framework.
- Enemy and exercise configurations are in memory.
- Automated coverage is currently limited to generated placeholder tests.
- Camera, rotation, mirroring, video decoding, and MediaPipe behavior require
  device or emulator validation.
- `VideoFileFrameSource` expects `assets/raw/test_video.mp4`; the file may be
  intentionally absent from source control.

## Near-Term Direction

The next useful milestone is a reliable vertical slice:

- start a session;
- acquire and explain tracking state;
- count squats reliably across common camera positions;
- damage and defeat an enemy;
- reset cleanly;
- validate the same flow with deterministic recorded video;
- cover detector and combat rules with unit tests.

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
