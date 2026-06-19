package com.example.rpg.data.enemy

import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.game.enemy.Boss

interface EnemyRepository {
    fun getRandomChoices(exerciseType: ExerciseType, count: Int = 3): List<EnemyConfig>

    fun getQuestChoices(
        exerciseType: ExerciseType,
        requireResistantEnemy: Boolean,
        count: Int = 3,
    ): List<EnemyConfig>

    fun createBoss(id: String, playerLevel: Int = 1): Boss
}
