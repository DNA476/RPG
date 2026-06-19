package com.example.rpg.data.enemy

import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.game.enemy.Boss
import com.example.rpg.game.enemy.EnemyAbility
import com.example.rpg.game.enemy.ExerciseAffinity
import kotlin.random.Random

/**
 * In-memory enemy catalog and random encounter selection.
 */
class InMemoryEnemyRepository(
    private val random: Random = Random.Default,
) : EnemyRepository {
    private val enemies = listOf(
        EnemyConfig(
            id = "goblin_scout",
            name = "Гоблин-разведчик",
            description = "Быстрый стрелок, который сбивает темп тренировки.",
            maxHp = 10,
            imageResource = "goblin_scout",
            weakness = ExerciseAffinity.weakness(ExerciseType.JUMPING_JACK),
            resistance = ExerciseAffinity.resistance(ExerciseType.PLANK),
            ability = weakeningAbility("Сбивающий выстрел"),
        ),
        EnemyConfig(
            id = "goblin_brute",
            name = "Гоблин-громила",
            description = "Живучий противник, полагающийся на грубую силу.",
            maxHp = 18,
            imageResource = "goblin_brute",
            weakness = ExerciseAffinity.weakness(ExerciseType.PULL_UP),
            resistance = ExerciseAffinity.resistance(ExerciseType.SQUAT),
            ability = weakeningAbility("Боевой рёв"),
        ),
        EnemyConfig(
            id = "goblin_shaman",
            name = "Гоблин-шаман",
            description = "Накладывает проклятие, ослабляющее серию атак.",
            maxHp = 14,
            imageResource = "goblin_shaman",
            weakness = ExerciseAffinity.weakness(ExerciseType.CRUNCH),
            resistance = ExerciseAffinity.resistance(ExerciseType.PUSH_UP),
            ability = weakeningAbility("Ослабляющее проклятие"),
        ),
        EnemyConfig(
            id = "goblin_guard",
            name = "Гоблин-щитоносец",
            description = "Медленный защитник с тяжёлым деревянным щитом.",
            maxHp = 20,
            imageResource = "goblin_guard",
            weakness = ExerciseAffinity.weakness(ExerciseType.PUSH_UP),
            resistance = ExerciseAffinity.resistance(ExerciseType.CRUNCH),
            ability = weakeningAbility("Удар щитом"),
        ),
        EnemyConfig(
            id = "cave_hound",
            name = "Пещерный пёс",
            description = "Подвижный зверь, устойчивый к размеренным приседаниям.",
            maxHp = 12,
            imageResource = "cave_hound",
            weakness = ExerciseAffinity.weakness(ExerciseType.LUNGE),
            resistance = ExerciseAffinity.resistance(ExerciseType.SQUAT),
            ability = weakeningAbility("Оглушающий вой"),
        ),
        EnemyConfig(
            id = "ash_hound",
            name = "Пепельный пёс",
            description = "Зверь из горячих пещер, атакующий резкими выпадами.",
            maxHp = 16,
            imageResource = "ash_hound",
            weakness = ExerciseAffinity.weakness(ExerciseType.JUMPING_JACK),
            resistance = ExerciseAffinity.resistance(ExerciseType.PULL_UP),
            ability = weakeningAbility("Пламенный рык"),
        ),
    )

    override fun getRandomChoices(exerciseType: ExerciseType, count: Int): List<EnemyConfig> {
        return buildChoices(
            exerciseType = exerciseType,
            count = count,
            requireResistantEnemy = false,
        )
    }

    override fun getQuestChoices(
        exerciseType: ExerciseType,
        requireResistantEnemy: Boolean,
        count: Int,
    ): List<EnemyConfig> {
        return buildChoices(
            exerciseType = exerciseType,
            count = count,
            requireResistantEnemy = requireResistantEnemy,
        )
    }

    private fun buildChoices(
        exerciseType: ExerciseType,
        count: Int,
        requireResistantEnemy: Boolean,
    ): List<EnemyConfig> {
        require(count in 1..enemies.size)
        require(!requireResistantEnemy || count >= 2)
        val shuffled = enemies.shuffled(random)
        val choices = shuffled.take(count).toMutableList()
        if (choices.all { it.isResistantTo(exerciseType) }) {
            choices[choices.lastIndex] = shuffled.first { !it.isResistantTo(exerciseType) }
        }
        if (requireResistantEnemy && choices.none { it.isResistantTo(exerciseType) }) {
            val resistantEnemy = shuffled.first { it.isResistantTo(exerciseType) }
            val replacementIndex = choices.indexOfLast {
                !it.isResistantTo(exerciseType)
            }
            choices[replacementIndex] = resistantEnemy
        }
        return choices.distinctBy { it.id }
    }

    override fun createBoss(id: String, playerLevel: Int): Boss {
        val config = enemies.first { it.id == id }.scaledForLevel(playerLevel)
        return Boss(
            id = config.id,
            name = config.name,
            description = config.description,
            maxHp = config.maxHp,
            currentHp = config.maxHp,
            imageResource = config.imageResource,
            weakness = config.weakness,
            resistance = config.resistance,
            ability = config.ability,
        )
    }

    private fun weakeningAbility(name: String) = EnemyAbility(
        name = name,
        description = "Снижает урон игрока на 25% на 10 секунд.",
    )
}
