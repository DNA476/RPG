# Architecture

## Overview

The project is a Kotlin multi-module Android application using MVVM at the app
boundary and one-way data flow through the pose, exercise, combat, and UI layers.

```text
FrameSource (:app)
    -> Bitmap
PoseAnalyzer (:pose)
    -> StateFlow<PoseFrame>
ExerciseDetector (:domain)
    -> SharedFlow<ExerciseEvent>
BattleSession (:game)
    -> StateFlow<BattleSnapshot>
BattleViewModel (:app)
    -> StateFlow<BattleUiState>
Compose UI (:app)
```

## Modules

### `:app`

Android entry point and composition root.

Responsibilities:

- Compose screens and reusable UI components.
- Android lifecycle and runtime permission handling.
- CameraX and debug video frame sources.
- ViewModel orchestration and conversion to UI state.
- Construction of current repository, detector, analyzer, and battle objects.

Key files:

- `MainActivity.kt`
- `ui/FitnessRpgApp.kt`
- `ui/viewmodel/BattleViewModel.kt`
- `ui/screens/BattleScreen.kt`
- `ui/components/CameraPreview.kt`
- `frame/CameraFrameSource.kt`
- `frame/VideoFileFrameSource.kt`

Allowed dependencies: `:pose`, `:domain`, `:game`, `:data`, Android/Compose.

### `:pose`

Android adapter for MediaPipe Pose Landmarker.

Responsibilities:

- Load the `.task` model from app assets.
- Run asynchronous live-stream inference.
- Apply confidence configuration.
- Convert MediaPipe landmarks to domain `PoseFrame` objects.
- Publish tracking, no-person, and error states.
- Reject a new frame while inference is already in progress.

It accepts `Bitmap`, so CameraX conversion, rotation, and mirroring stay in
`:app`.

Allowed dependencies: `:domain`, MediaPipe, Android, coroutines.

### `:domain`

Shared, framework-light fitness domain.

Responsibilities:

- Stable pose and landmark models.
- Exercise identifiers and event contracts.
- Exercise detector interface.
- Stateful squat recognition and its configuration.

This module must not know about Compose, CameraX, MediaPipe, enemies, or damage.

### `:game`

Framework-light combat domain.

Responsibilities:

- Enemy and player models.
- Exercise-to-attack mapping.
- Damage calculation.
- Battle state transitions and immutable snapshots.

`BattleSession` reacts to completed repetitions only. It must remain usable in
plain unit tests without Android.

### `:data`

Content and configuration sources.

Responsibilities:

- Enemy repository contracts and current in-memory implementation.
- Exercise configuration repository contracts and current in-memory
  implementation.

This is the future home for JSON, Room, or remote-backed implementations. The
game and domain modules should not depend on concrete storage.

## Dependency Direction

```text
:app  -> :pose, :domain, :game, :data
:pose -> :domain
:data -> :domain, :game
:game -> :domain
:domain -> Kotlin/coroutines only
```

Do not introduce reverse edges. In particular, `:game` must not depend on
`:app`, `:pose`, or `:data`.

## Runtime Flow

1. `BattleViewModel.start()` starts `SquatDetector` and moves the battle to
   `TRACKING`.
2. `CameraPreview` selects a `FrameSource`.
3. `CameraFrameSource` converts CameraX frames to correctly oriented/mirrored
   bitmaps, or `VideoFileFrameSource` decodes a test video.
4. `PoseAnalyzer.analyze()` sends an accepted bitmap to MediaPipe.
5. MediaPipe results become `PoseFrame` values.
6. While tracking, `BattleViewModel` starts the battle and passes frames to
   `SquatDetector`.
7. The detector state machine recognizes standing -> bottom -> standing and
   emits `RepetitionCompleted`.
8. `BattleSession` maps the exercise to an attack, calculates damage, mutates
   boss HP, and publishes a `BattleSnapshot`.
9. `BattleViewModel` combines pose, detector, and battle information into
   `BattleUiState`.
10. Compose renders camera/video, skeleton, boss HP, counters, status, damage,
    and victory state.

## State Ownership

- `PoseAnalyzer` owns the latest pose frame and inference busy flag.
- Each `ExerciseDetector` owns its movement phase and repetition count.
- `BattleSession` owns enemy HP, repetition total, and game state.
- `BattleViewModel` owns presentation messages and the aggregated UI model.
- Compose components render state and should not contain domain decisions.

## Extension Rules

### Add an exercise

1. Add or reuse an `ExerciseType`.
2. Implement an `ExerciseDetector` and dedicated config in `:domain`.
3. Expose the config through `:data`.
4. Map the exercise to an attack in `:game`.
5. Register the detector in app orchestration.
6. Add synthetic pose-frame unit tests before tuning against real video.

When multiple exercises are active, introduce a detector registry/coordinator
instead of adding more hard-coded collectors to `BattleViewModel`.

### Add an enemy

Add content through `EnemyRepository`. Avoid branching UI or battle code on
specific enemy IDs unless a real mechanic requires a typed capability.

### Add persistence

Create new repository implementations in `:data`; keep repository interfaces
stable and inject implementations from `:app`.

## Testing Strategy

- `:domain`: deterministic pose sequences for detector state machines,
  visibility thresholds, boundary angles, reset, and invalid frames.
- `:game`: attack mapping, damage, state transitions, victory, reset, and ignored
  events.
- `:data`: repository contract tests when persistence is introduced.
- `:app`: ViewModel tests with fakes, Compose UI tests, and debug-video smoke
  tests.
- Device/emulator: camera permission, front-camera orientation/mirroring,
  lifecycle restart, inference throughput, and end-to-end battle flow.

## Known Architectural Debt

- `BattleViewModel` is currently both composition root and orchestrator.
- The UI depends on concrete `PoseAnalyzer` rather than a narrow input contract.
- Boss state exposes a mutable domain object inside snapshots.
- No clock abstraction exists for transient UI messages.
- Error information is flattened to tracking state or display strings.
- There is no formal session/config model describing selected exercises, enemy,
  target duration, or difficulty.
