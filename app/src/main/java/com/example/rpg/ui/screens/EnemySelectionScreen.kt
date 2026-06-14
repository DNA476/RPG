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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpg.R
import com.example.rpg.data.enemy.EnemyConfig
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.ui.components.EnemyChoiceCard
import com.example.rpg.ui.localization.exerciseNameResource

@Composable
fun EnemySelectionScreen(
    exercise: ExerciseConfig,
    enemies: List<EnemyConfig>,
    selectedEnemy: EnemyConfig?,
    onEnemySelected: (String) -> Unit,
    onStartBattle: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2C2116), Color(0xFF111714), Color(0xFF080A09)),
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.choose_opponent_title),
                        color = Color(0xFFFFD166),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = stringResource(
                            R.string.selected_exercise_note,
                            stringResource(exerciseNameResource(exercise.type)),
                        ),
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            items(enemies, key = { it.id }) { enemy ->
                EnemyChoiceCard(
                    enemy = enemy,
                    exerciseType = exercise.type,
                    selected = enemy.id == selectedEnemy?.id,
                    onClick = { onEnemySelected(enemy.id) },
                )
            }
        }
        Button(
            onClick = onStartBattle,
            enabled = selectedEnemy != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD166),
                contentColor = Color(0xFF111111),
            ),
            contentPadding = PaddingValues(vertical = 15.dp),
        ) {
            Text(stringResource(R.string.start_battle), fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(stringResource(R.string.back_to_exercises))
        }
    }
}
