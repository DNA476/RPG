# Fitness RPG MVP Architecture

## Описание архитектуры

Проект реализован как multi-module Android-приложение с MVVM и однонаправленными зависимостями. UI не содержит игровой логики, игровой движок не зависит от Android, Compose, CameraX или MediaPipe.

Модули:

- `:app` - Android-приложение, Compose UI, `MainActivity`, MVVM ViewModel, CameraX preview.
- `:domain` - общие доменные контракты: поза, суставы, события упражнений, `ExerciseDetector`, `SquatDetector`.
- `:game` - игровой движок: враги, босс, игрок, атаки, расчет урона, `BattleSession`, StateFlow состояния боя.
- `:data` - конфигурации и репозитории для врагов и упражнений. Сейчас in-memory, позже можно заменить JSON/Room.
- `:pose` - Android-адаптер MediaPipe Pose Landmarker. Конвертирует кадры CameraX в доменную `PoseFrame`.

Основной принцип: данные идут от камеры к PoseAnalyzer, затем к детектору упражнения, затем в BattleSession, затем через StateFlow в UI.

## Схема взаимодействия модулей

```text
CameraX Preview (:app)
        |
        v
PoseAnalyzer (:pose, MediaPipe Tasks)
        |
        v
PoseFrame (:domain)
        |
        v
ExerciseDetector / SquatDetector (:domain)
        |
        v
ExerciseEvent.RepetitionCompleted (:domain)
        |
        v
BattleSession (:game)
        |
        v
BattleSnapshot StateFlow (:game)
        |
        v
BattleViewModel (:app)
        |
        v
Compose UI (:app)
```

Зависимости:

```text
:app  -> :pose, :domain, :game, :data
:pose -> :domain
:data -> :domain, :game
:game -> :domain
:domain -> external Kotlin/coroutines only
```

## Список ключевых классов

### `:domain`

- `BodyLandmarkName` - стабильные имена 33 MediaPipe landmarks.
- `BodyLandmark` - нормализованные координаты одного сустава.
- `PoseTrackingState` - состояние трекинга позы.
- `PoseFrame` - снимок позы для детекторов.
- `ExerciseType` - идентификатор упражнения.
- `ExerciseEvent` - события детекторов: старт упражнения, завершенный повтор, окончание движения.
- `ExerciseDetector` - общий интерфейс детектора упражнения.
- `SquatDetectorConfig` - пороги углов и видимости для приседаний.
- `SquatDetector` - первый детектор, считает полный цикл стоя -> низ -> стоя.

### `:game`

- `Enemy` - базовый враг с HP и ресурсом изображения.
- `Boss` - расширение врага для будущих boss mechanics.
- `PlayerStats` - заготовка для уровня, опыта, силы, экипировки и навыков.
- `AttackType` - типы атак: `BasicAttack`, `HeavyAttack`, `CriticalAttack`.
- `ExerciseAttackMapper` - маппинг упражнений в атаки.
- `DefaultExerciseAttackMapper` - MVP-маппинг: `Squat -> BasicAttack`.
- `DamageCalculator` - интерфейс расчета урона.
- `FlatDamageCalculator` - MVP-урон без RPG-модификаторов.
- `GameState` - `IDLE`, `TRACKING`, `BATTLE`, `VICTORY`, `DEFEAT`.
- `BattleSnapshot` - immutable состояние боя для StateFlow.
- `BattleSession` - игровой движок одного боя.

### `:data`

- `EnemyConfig` - конфиг врага, пригодный для JSON/Room.
- `EnemyRepository` - источник врагов.
- `InMemoryEnemyRepository` - первый босс `Training Dummy`, 10 HP.
- `ExerciseConfigRepository` - источник настроек упражнений.
- `InMemoryExerciseConfigRepository` - пороги `SquatDetector`.

### `:pose`

- `PoseAnalyzerConfiguration` - настройки модели и confidence thresholds MediaPipe.
- `PoseAnalyzer` - локальный MediaPipe Pose Landmarker adapter, публикует `StateFlow<PoseFrame>`.

### `:app`

- `MainActivity` - Android entry point.
- `FitnessRpgApp` - root Compose UI.
- `BattleViewModel` - связывает pose, detector, battle engine и UI state.
- `BattleUiState` - UI-модель экрана боя.
- `CameraPreview` - CameraX preview и отправка кадров в `PoseAnalyzer`.
- `SkeletonOverlay` - отрисовка скелета поверх камеры.
- `BossHealthBar` - переиспользуемая полоска здоровья.
- `BattleScreen` - экран боя.
- `VictoryScreen` - экран победы.
- `DamagePopup` - сообщение о нанесенном уроне.

## Как добавить новое упражнение

1. В `:domain` добавить новый `ExerciseType`, если его еще нет.
2. Создать конфиг детектора, например `LungeDetectorConfig`.
3. Реализовать `ExerciseDetector`, например `LungeDetector`, который принимает `PoseFrame` и эмитит `ExerciseEvent`.
4. В `:data` добавить конфигурацию в `ExerciseConfigRepository`.
5. В `:game` обновить `DefaultExerciseAttackMapper`, например `Lunge -> HeavyAttack`.
6. В `BattleViewModel` подключить новый детектор к общему потоку `PoseFrame`. При росте числа упражнений лучше заменить ручное подключение на `ExerciseDetectorRegistry`.

Минимально затрагиваемые места: `:domain/exercise`, `:data/exercise`, `DefaultExerciseAttackMapper`, `BattleViewModel`.

## Как добавить нового врага или босса

1. Добавить новый `EnemyConfig` в `InMemoryEnemyRepository` или внешний JSON.
2. Вернуть нужного босса через `EnemyRepository`.
3. UI и `BattleSession` менять не нужно, если модель врага остается `Boss`/`Enemy`.

Минимально затрагиваемое место: `:data/enemy/InMemoryEnemyRepository.kt`.

## Рекомендации по дальнейшему развитию

- Добавить `ExerciseDetectorRegistry`, чтобы ViewModel работала со списком детекторов, а не с конкретным `SquatDetector`.
- Вынести battle setup в `BattleFactory`, чтобы выбирать босса, набор упражнений и правила боя из кампании.
- Добавить `RewardService` в `:game` для опыта, уровней и достижений после победы.
- Добавить `InventoryRepository`, `EquipmentRepository`, `SkillTreeRepository` в `:data` и соответствующие доменные модели.
- Заменить in-memory конфиги на JSON assets или Room, чтобы новые враги и упражнения добавлялись без перекомпиляции логики.
- Добавить калибровку пользователя: рост, глубина приседа, допустимые углы, сторона камеры.
- Добавить unit-тесты для каждого детектора упражнений с синтетическими `PoseFrame`.
- Добавить instrumentation-тесты Compose UI и ручной QA на реальном устройстве, потому что MediaPipe/CameraX требуют камеры.

## MediaPipe и локальная обработка

Модель `pose_landmarker_lite.task` лежит в `app/src/main/assets` и используется через `PoseAnalyzerConfiguration.modelAssetPath`. Обработка выполняется локально через `com.google.mediapipe:tasks-vision`, серверная часть отсутствует.

Официальная документация MediaPipe указывает, что Android Pose Landmarker использует `com.google.mediapipe:tasks-vision`, требует `.task` модель в `src/main/assets`, поддерживает `LIVE_STREAM` режим и возвращает 33 landmarks с нормализованными координатами.
