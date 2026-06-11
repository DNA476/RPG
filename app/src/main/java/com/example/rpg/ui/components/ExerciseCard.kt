package com.example.rpg.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpg.domain.exercise.DetectorStatus
import com.example.rpg.domain.exercise.ExerciseConfig

@Composable
fun ExerciseCard(
    exercise: ExerciseConfig,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF263F35) else Color(0xFF1B211F),
        ),
        border = if (selected) BorderStroke(2.dp, Color(0xFFFFD166)) else null,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = exercise.displayName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                DetectorBadge(status = exercise.detectorStatus)
            }
            Text(
                text = exercise.description,
                color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Урон: ${exercise.baseDamage}  •  Сложность: ${exercise.difficulty.name}",
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun DetectorBadge(status: DetectorStatus) {
    val color = when (status) {
        DetectorStatus.READY -> Color(0xFF6DD58C)
        DetectorStatus.EXPERIMENTAL -> Color(0xFFFFC857)
        DetectorStatus.COMING_SOON -> Color(0xFF9AA0A6)
    }
    Surface(
        color = color.copy(alpha = 0.16f),
        shape = RoundedCornerShape(50),
    ) {
        Text(
            text = status.name.replace('_', ' '),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}
