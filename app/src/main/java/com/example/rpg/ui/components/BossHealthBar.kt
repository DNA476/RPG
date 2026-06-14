package com.example.rpg.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rpg.R

/**
 * Reusable boss health indicator with animated HP changes.
 */
@Composable
fun BossHealthBar(
    bossName: String,
    currentHp: Int,
    maxHp: Int,
    modifier: Modifier = Modifier,
) {
    val healthFraction = if (maxHp == 0) 0f else currentHp.toFloat() / maxHp.toFloat()
    val animatedFraction by animateFloatAsState(targetValue = healthFraction.coerceIn(0f, 1f), label = "bossHp")
    val barColor by animateColorAsState(
        targetValue = when {
            healthFraction > 0.5f -> Color(0xFF7CFF6B)
            healthFraction > 0.25f -> Color(0xFFFFC857)
            else -> Color(0xFFFF5C5C)
        },
        label = "bossHpColor",
    )

    Column(modifier = modifier) {
        Text(
            text = bossName,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF2A2A2A)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .height(18.dp)
                    .background(Brush.horizontalGradient(listOf(barColor, Color.White.copy(alpha = 0.65f)))),
            )
        }
        Text(
            text = stringResource(R.string.boss_health, currentHp, maxHp),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
