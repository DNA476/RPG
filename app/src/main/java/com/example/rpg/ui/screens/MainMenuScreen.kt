package com.example.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.ui.components.ExerciseCard

@Composable
fun MainMenuScreen(
    exercises: List<ExerciseConfig>,
    selectedExercise: ExerciseConfig?,
    onExerciseSelected: (ExerciseType) -> Unit,
    onContinue: () -> Unit,
    onStatistics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF17352B), Color(0xFF0E1714), Color(0xFF080A09)),
                ),
            )
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "FitRPG",
                        color = Color(0xFFFFD166),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = "Побеждай врагов, выполняя реальные упражнения перед камерой",
                        color = Color.White.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Выбери тренировку",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
            }
            items(exercises, key = { it.type.name }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    selected = exercise.type == selectedExercise?.type,
                    onClick = { onExerciseSelected(exercise.type) },
                )
            }
        }
        OutlinedButton(
            onClick = onStatistics,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            Text(
                text = "Статистика",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Button(
            onClick = onContinue,
            enabled = selectedExercise != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD166),
                contentColor = Color(0xFF111111),
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            Text(
                text = "Выбрать противника",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
