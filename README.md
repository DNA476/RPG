# Fitness RPG

Fitness RPG is an Android prototype that turns real exercises into short RPG
battles. The phone camera tracks the player on-device, recognized repetitions
become attacks, and enemies are defeated by completing movements.

Current features include:

- exercise selection with squat detection and safe experimental placeholders for
  other movements;
- tactical enemy matchups with weaknesses and resistances;
- local profile and workout statistics;
- inventory, equipment, artifacts, and weekly quest prototypes;
- artifact rewards for victories against resistant enemies;
- in-app language selection and localized interface text.

The project is built with Kotlin, Jetpack Compose, CameraX, and MediaPipe Pose
Landmarker. All profile, statistics, inventory, and quest progress data is
stored locally on the device.
