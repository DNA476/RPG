package com.example.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpg.BuildConfig
import com.example.rpg.pose.PoseAnalyzer
import com.example.rpg.ui.components.BossHealthBar
import com.example.rpg.ui.components.CameraPreview
import com.example.rpg.ui.components.EnemyCombatant
import com.example.rpg.ui.components.SkeletonOverlay
import com.example.rpg.ui.viewmodel.BattleUiState

/**
 * Main battle screen showing camera preview, pose skeleton, boss HP, squat count, and exercise feedback.
 */
@Composable
fun BattleScreen(
    uiState: BattleUiState,
    poseAnalyzer: PoseAnalyzer,
    onBackToMenu: () -> Unit,
    onSimulateRepetition: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(
            poseAnalyzer = poseAnalyzer,
            modifier = Modifier.fillMaxSize(),
        )
        SkeletonOverlay(
            poseFrame = uiState.poseFrame,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xCC050505), Color.Transparent, Color(0xDD050505)),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            BossHealthBar(
                bossName = uiState.bossName,
                currentHp = uiState.bossCurrentHp,
                maxHp = uiState.bossMaxHp,
                modifier = Modifier.fillMaxWidth(),
            )
            EnemyCombatant(
                hitEventId = uiState.hitEventId,
                damageMessage = uiState.damageMessage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.82f)
                    .height(390.dp),
            )
            BattleHud(
                uiState = uiState,
                onBackToMenu = onBackToMenu,
                onSimulateRepetition = onSimulateRepetition,
            )
        }
    }
}

/**
 * Bottom battle HUD with counters and detector status.
 */
@Composable
fun BattleHud(
    uiState: BattleUiState,
    onBackToMenu: () -> Unit,
    onSimulateRepetition: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xE61A1A1A),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val exercise = uiState.selectedExercise
            Text(
                text = exercise?.displayName.orEmpty(),
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Урон: ${exercise?.baseDamage ?: 0}  •  " +
                    "${exercise?.difficulty?.name.orEmpty()}  •  ${exercise?.detectorStatus?.name.orEmpty()}",
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatBlock(label = "Повторы", value = uiState.completedRepetitions.toString())
                Spacer(modifier = Modifier.width(12.dp))
                StatBlock(label = "Общий урон", value = uiState.totalDamage.toString())
            }
            Text(
                text = uiState.exerciseStatus,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Tracking: ${uiState.trackingState.name}",
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (BuildConfig.DEBUG) {
                Button(
                    onClick = onSimulateRepetition,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD166),
                        contentColor = Color(0xFF111111),
                    ),
                ) {
                    Text("Simulate repetition")
                }
            }
            OutlinedButton(
                onClick = onBackToMenu,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Назад в меню")
            }
        }
    }
}

/**
 * Small labeled metric used inside the battle HUD.
 */
@Composable
fun StatBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.68f),
            style = MaterialTheme.typography.labelLarge,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFFFFD166), RoundedCornerShape(50)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
