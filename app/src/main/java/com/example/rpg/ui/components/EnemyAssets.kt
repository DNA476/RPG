package com.example.rpg.ui.components

import androidx.annotation.DrawableRes
import com.example.rpg.R
import com.example.rpg.domain.exercise.ExerciseType

@DrawableRes
fun enemyDrawableResource(resourceName: String): Int = when (resourceName) {
    "goblin_scout" -> R.drawable.goblin_scout
    "goblin_brute" -> R.drawable.goblin_brute
    "goblin_shaman" -> R.drawable.goblin_shaman
    "goblin_guard" -> R.drawable.goblin_guard
    "cave_hound" -> R.drawable.cave_hound
    "ash_hound" -> R.drawable.ash_hound
    else -> R.drawable.goblin_enemy
}

fun exerciseShortName(type: ExerciseType): String = when (type) {
    ExerciseType.SQUAT -> "приседаниям"
    ExerciseType.PUSH_UP -> "отжиманиям"
    ExerciseType.PULL_UP -> "подтягиваниям"
    ExerciseType.CRUNCH -> "скручиваниям"
    ExerciseType.LUNGE -> "выпадам"
    ExerciseType.JUMPING_JACK -> "jumping jacks"
    ExerciseType.PLANK -> "планке"
}
