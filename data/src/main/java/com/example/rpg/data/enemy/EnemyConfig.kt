package com.example.rpg.data.enemy

import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.game.enemy.EnemyAbility
import com.example.rpg.game.enemy.EnemyHealthScaling
import com.example.rpg.game.enemy.ExerciseAffinity

/**
 * Serializable-friendly enemy content that can later move to JSON or Room.
 */
data class EnemyConfig(
    val id: String,
    val name: String,
    val description: String,
    val maxHp: Int,
    val imageResource: String,
    val weakness: ExerciseAffinity,
    val resistance: ExerciseAffinity,
    val ability: EnemyAbility,
) {
    fun isResistantTo(exerciseType: ExerciseType): Boolean =
        resistance.exerciseType == exerciseType

    fun scaledForLevel(level: Int): EnemyConfig = copy(
        maxHp = EnemyHealthScaling.maxHpForLevel(maxHp, level),
    )
}
