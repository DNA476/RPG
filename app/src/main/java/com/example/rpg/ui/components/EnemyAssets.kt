package com.example.rpg.ui.components

import androidx.annotation.DrawableRes
import com.example.rpg.R

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
