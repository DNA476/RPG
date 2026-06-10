package com.example.rpg.game.attack

import com.example.rpg.domain.exercise.ExerciseType

/**
 * Default MVP mapping from exercises to attacks.
 */
class DefaultExerciseAttackMapper : ExerciseAttackMapper {
    override fun map(exerciseType: ExerciseType): AttackType = when (exerciseType) {
        ExerciseType.SQUAT -> AttackType.BasicAttack
        ExerciseType.LUNGE -> AttackType.HeavyAttack
        ExerciseType.JUMP -> AttackType.CriticalAttack
    }
}
