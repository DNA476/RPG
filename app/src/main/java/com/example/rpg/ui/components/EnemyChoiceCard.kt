package com.example.rpg.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpg.data.enemy.EnemyConfig
import com.example.rpg.domain.exercise.ExerciseType

@Composable
fun EnemyChoiceCard(
    enemy: EnemyConfig,
    exerciseType: ExerciseType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF263F35) else Color(0xFF181E1C),
        ),
        border = if (selected) BorderStroke(2.dp, Color(0xFFFFD166)) else null,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(enemyDrawableResource(enemy.imageResource)),
                contentDescription = enemy.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(116.dp)
                    .height(148.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = enemy.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${enemy.maxHp} HP · ${enemy.description}",
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodySmall,
                )
                TraitChip(
                    text = affinityLabel(enemy, exerciseType),
                    color = affinityColor(enemy, exerciseType),
                )
                TraitChip(
                    text = "${enemy.ability.name}: −${enemy.ability.attackReductionPercent}% атаки",
                    color = Color(0xFFFFC857),
                )
            }
        }
    }
}

@Composable
private fun TraitChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50),
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

private fun affinityLabel(enemy: EnemyConfig, exerciseType: ExerciseType): String = when (exerciseType) {
    enemy.weakness.exerciseType -> "Уязвим к ${exerciseShortName(exerciseType)}: ×1.5"
    enemy.resistance.exerciseType -> "Сопротивление ${exerciseShortName(exerciseType)}: ×0.75"
    else -> "Нейтрален к выбранному упражнению"
}

private fun affinityColor(enemy: EnemyConfig, exerciseType: ExerciseType): Color = when (exerciseType) {
    enemy.weakness.exerciseType -> Color(0xFF6DD58C)
    enemy.resistance.exerciseType -> Color(0xFFFF7B72)
    else -> Color(0xFF9FB7AE)
}
