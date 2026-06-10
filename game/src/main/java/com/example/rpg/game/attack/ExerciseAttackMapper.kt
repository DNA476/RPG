package com.example.rpg.game.attack

import com.example.rpg.domain.exercise.ExerciseType

/**
 * Maps exercise identities to game attacks.
 */
interface ExerciseAttackMapper {
    /**
     * Converts an exercise type into an attack type for the battle engine.
     */
    fun map(exerciseType: ExerciseType): AttackType
}
