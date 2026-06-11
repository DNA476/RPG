package com.example.rpg.domain.exercise

/**
 * Creates the detector selected for a battle without leaking concrete detectors into UI code.
 */
class ExerciseDetectorFactory(
    private val squatConfig: SquatDetectorConfig = SquatDetectorConfig(),
) {
    fun create(type: ExerciseType): ExerciseDetector = when (type) {
        ExerciseType.SQUAT -> SquatDetector(squatConfig)
        ExerciseType.PUSH_UP -> PushUpDetector()
        ExerciseType.PULL_UP -> PullUpDetector()
        ExerciseType.CRUNCH -> CrunchDetector()
        ExerciseType.LUNGE -> LungeDetector()
        ExerciseType.JUMPING_JACK -> JumpingJackDetector()
        ExerciseType.PLANK -> PlankDetector()
    }
}
