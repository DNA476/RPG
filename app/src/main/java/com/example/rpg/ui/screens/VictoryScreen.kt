package com.example.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rpg.R
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.ui.localization.enemyNameResource
import com.example.rpg.ui.localization.exerciseNameResource

@Composable
fun VictoryScreen(
    enemyId: String,
    exerciseType: ExerciseType,
    completedRepetitions: Int,
    totalDamage: Int,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF285943), Color(0xFF0D1F1A), Color(0xFF050505)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.padding(28.dp),
        ) {
            Text(
                text = stringResource(R.string.victory),
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = stringResource(
                    R.string.enemy_defeated,
                    stringResource(enemyNameResource(enemyId)),
                ),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(
                    R.string.victory_summary,
                    stringResource(exerciseNameResource(exerciseType)),
                    completedRepetitions,
                    totalDamage,
                ),
                color = Color.White.copy(alpha = 0.84f),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onBackToMenu,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD166),
                    contentColor = Color(0xFF111111),
                ),
            ) {
                Text(stringResource(R.string.return_to_menu))
            }
        }
    }
}
