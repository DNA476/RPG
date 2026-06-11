package com.example.rpg.game.enemy

import com.example.rpg.domain.exercise.ExerciseType

/**
 * Base enemy model for battle logic and future campaign encounters.
 */
open class Enemy(
    val id: String,
    val name: String,
    val description: String,
    val maxHp: Int,
    currentHp: Int,
    val imageResource: String,
    val weakness: ExerciseAffinity,
    val resistance: ExerciseAffinity,
    val ability: EnemyAbility,
) {
    var currentHp: Int = currentHp.coerceIn(0, maxHp)
        private set

    fun receiveDamage(amount: Int) {
        currentHp = (currentHp - amount).coerceAtLeast(0)
    }

    fun resetHealth() {
        currentHp = maxHp
    }

    fun isDefeated(): Boolean = currentHp <= 0

    fun damageMultiplierFor(exerciseType: ExerciseType): Float = when (exerciseType) {
        weakness.exerciseType -> weakness.damageMultiplier
        resistance.exerciseType -> resistance.damageMultiplier
        else -> 1f
    }
}
