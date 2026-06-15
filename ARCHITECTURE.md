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
    -> SharedFlow<ExerciseEvent> + StateFlow<ExerciseDetectionResult>
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
- State-driven navigation between onboarding, menu, statistics, profile,
  settings, inventory/equipment, weekly quests, battle, and victory.
- Main-menu-only navigation drawer for profile, statistics, and settings
  destinations.
- Android string resources and AppCompat per-app locales for device-selected or
  explicitly selected Russian, English, German, Spanish, French, and Portuguese
  localization.
- Enemy choice UI and coroutine-driven presentation timers.
- Construction of current repository, detector, analyzer, and battle objects.
- SharedPreferences adapter for the optional user profile and daily fitness
  aggregates.
- SharedPreferences adapter for owned inventory IDs and equipped slot mappings.
- Scalable Compose Canvas icons for individual inventory item silhouettes.
- SharedPreferences adapter for weekly quest progress and granted rewards.

Key files:

- `MainActivity.kt`
- `ui/FitnessRpgApp.kt`
- `ui/viewmodel/BattleViewModel.kt`
- `ui/screens/BattleScreen.kt`
- `ui/screens/ProfileScreen.kt`
- `ui/screens/SettingsScreen.kt`
- `ui/screens/InventoryScreen.kt`
- `ui/screens/WeeklyQuestsScreen.kt`
- `ui/screens/StatisticsScreen.kt`
- `data/local/SharedPreferencesFitnessRepository.kt`
- `data/local/SharedPreferencesInventoryRepository.kt`
- `data/local/SharedPreferencesWeeklyQuestRepository.kt`
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
- Exercise content model, difficulty, and detector status.
- Exercise detector interface.
- Stateful squat recognition and its configuration.
- Typed detector feedback values that the Android UI maps to localized strings.
- Detector factory and safe experimental detector placeholders.

This module must not know about Compose, CameraX, MediaPipe, enemies, or damage.

### `:game`

Framework-light combat domain.

Responsibilities:

- Enemy and player models.
- Exercise affinities and enemy ability models.
- Exercise-to-attack mapping.
- Damage calculation from exercise config, enemy affinity, and temporary player
  attack modifiers.
- Replaceable enemy attack timing policy.
- Battle state transitions and immutable snapshots.

`BattleSession` reacts to completed repetitions only. It must remain usable in
plain unit tests without Android.

### `:data`

Content and configuration sources.

Responsibilities:

- Enemy repository contracts and current in-memory implementation.
- Random three-enemy selection with a fair-matchup guarantee.
- Exercise configuration repository contracts and current in-memory
  implementation.
- User profile and exercise statistics repository contracts.
- Inventory models, equipment slots, icon types, repository contract, and
  30-item test catalog.
- Weekly quest models, catalog, progress rules, and repository contract.
- Framework-light daily statistics models and calorie estimation.
- `ExerciseCatalog`, the single source of truth for names, descriptions, base
  damage, difficulty, and detector status.

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

1. On first launch, `FitnessRpgApp` opens `ProfileScreen`; every field is
   optional and the player may skip it.
2. Later launches open `MainMenuScreen`, where a drawer links to profile,
   statistics, and language settings, a daily calorie card links directly to
   seven-day statistics, a backpack button opens inventory, and a
   sword-in-shield button opens weekly quests.
3. Inventory and equipment share a horizontal pager. Slot selection filters the
   inventory, while equip/unequip changes are saved locally. Newly added
   non-quest test items are merged into existing local inventories on load.
4. Weekly quests show one regular, one resistant-matchup, and one
   resistant-matchup-without-artifact objective. Starting a quest selects its
   exercise and builds an encounter containing a qualifying resistant enemy
   when required.
5. Outside quests, the player selects an `ExerciseConfig` from
   `ExerciseCatalog`.
6. `BattleViewModel.openEnemySelection()` obtains and caches three random enemy
   configs for the selected exercise.
7. The player chooses one enemy; `startBattle()` creates its `Boss`, detector,
   and `BattleSession`.
8. `CameraPreview` selects a `FrameSource`.
9. `CameraFrameSource` converts CameraX frames to correctly oriented/mirrored
   bitmaps, or `VideoFileFrameSource` decodes a test video.
10. `PoseAnalyzer.analyze()` sends an accepted bitmap to MediaPipe.
11. MediaPipe results become `PoseFrame` values.
12. While tracking, `BattleViewModel` passes frames to the selected detector.
13. The ready squat detector recognizes standing -> bottom -> standing and
   emits `RepetitionCompleted`.
14. `BattleSession` calculates damage from base damage, affinity, and current
   attack multiplier, mutates HP, and publishes a `BattleSnapshot`.
15. After `BattleSession` accepts a live repetition, `BattleViewModel` records
   it in the local daily aggregate. Debug simulation bypasses this write.
16. A ViewModel timer uses `EnemyAttackTimingPolicy`; every 15 seconds it applies
   the selected enemy's 25% attack reduction for 10 seconds.
17. On victory, `WeeklyQuestProgress` evaluates exercise, resistance, and the
   artifact state captured at battle start. Completed quest rewards are added
   to the persisted inventory once.
18. `BattleViewModel` increments `hitEventId` for every completed attack and
   combines pose, detector, battle, and presentation information into
   `BattleUiState`.
19. Compose renders battle feedback and switches to `VictoryScreen` at zero HP.

## State Ownership

- `PoseAnalyzer` owns the latest pose frame and inference busy flag.
- Each `ExerciseDetector` owns its movement phase and repetition count.
- `BattleSession` owns enemy HP, repetition total, and game state.
- `BattleSession` owns the active player attack multiplier.
- `BattleViewModel` owns navigation, encounter choices, timer jobs,
  presentation messages, profile form state, statistics filters, and the
  aggregated UI model.
- `MainMenuScreen` owns only transient drawer open/closed state.
- `SharedPreferencesFitnessRepository` owns the persisted profile, onboarding
  completion flag, and daily exercise aggregates.
- `SharedPreferencesInventoryRepository` owns persisted inventory and equipped
  slot IDs; `InventoryCatalog` owns test item definitions, bonuses, and icon
  types. `InventoryItemIcon` renders those icon types without bitmap assets.
- `SharedPreferencesWeeklyQuestRepository` owns persisted week progress;
  `WeeklyQuestCatalog` and `WeeklyQuestProgress` own quest content and matching
  rules.
- `EnemyCombatant` owns transient shake, red-flash, and slash animation state.
- Compose components render state and should not contain domain decisions.

## Extension Rules

### Add an exercise

1. Add the `ExerciseType`.
2. Add one `ExerciseConfig` entry to `ExerciseCatalog`.
3. Implement an `ExerciseDetector` in `:domain`.
4. Register it in `ExerciseDetectorFactory`.
5. Add synthetic pose-frame tests before tuning against real video.

Base damage is configured only in `ExerciseCatalog`. Future critical hits,
equipment, combos, player stats, and enemy weaknesses belong in
`DamageCalculator`, not in detectors or Compose.

### Add an enemy

1. Add an `EnemyConfig` to `InMemoryEnemyRepository`.
2. Provide HP, portrait resource name, one weakness, one resistance, and one
   ability.
3. Add the portrait mapping in `EnemyAssets.kt`.
4. Add selection and damage tests for any new mechanic.

Do not branch combat code on enemy IDs. Add a typed ability or affinity instead.

### Add persistence

Keep repository contracts in `:data`. Android-specific implementations may live
behind adapters in `:app`; inject them at the composition root.

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
- Enemy attack scheduling currently runs in `BattleViewModel`; move it to a
  lifecycle-aware encounter coordinator when pause/resume is introduced.
- Error information is flattened to tracking state or display strings.
- The selected exercise is held by `BattleViewModel`; a formal session config is
  still needed for exercise switching, Auto Mode, and Free Battle Mode.
