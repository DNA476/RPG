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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpg.BuildConfig
import com.example.rpg.R
import com.example.rpg.pose.PoseAnalyzer
import com.example.rpg.ui.components.BossHealthBar
import com.example.rpg.ui.components.CameraPreview
import com.example.rpg.ui.components.EnemyCombatant
import com.example.rpg.ui.components.SkeletonOverlay
import com.example.rpg.ui.localization.detectorStatusResource
import com.example.rpg.ui.localization.difficultyResource
import com.example.rpg.ui.localization.enemyAbilityNameResource
import com.example.rpg.ui.localization.enemyNameResource
import com.example.rpg.ui.localization.exerciseNameResource
import com.example.rpg.ui.localization.trackingStateResource
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
    val enemyName = uiState.selectedEnemy?.let {
        stringResource(enemyNameResource(it.id))
    }.orEmpty()
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
                bossName = enemyName,
                currentHp = uiState.bossCurrentHp,
                maxHp = uiState.bossMaxHp,
                modifier = Modifier.fillMaxWidth(),
            )
            EnemyCombatant(
                imageResource = uiState.bossImageResource,
                enemyName = enemyName,
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
                text = exercise?.let { stringResource(exerciseNameResource(it.type)) }.orEmpty(),
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = exercise?.let {
                    stringResource(
                        R.string.battle_exercise_meta,
                        it.baseDamage,
                        stringResource(difficultyResource(it.difficulty)),
                        stringResource(detectorStatusResource(it.detectorStatus)),
                    )
                }.orEmpty(),
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelLarge,
            )
            val enemy = uiState.selectedEnemy
            Text(
                text = when (exercise?.type) {
                    enemy?.weakness?.exerciseType -> stringResource(R.string.enemy_weakness)
                    enemy?.resistance?.exerciseType -> stringResource(R.string.enemy_resistance)
                    else -> stringResource(R.string.enemy_neutral)
                },
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatBlock(
                    label = stringResource(R.string.repetitions),
                    value = uiState.completedRepetitions.toString(),
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatBlock(
                    label = stringResource(R.string.total_damage),
                    value = uiState.totalDamage.toString(),
                )
            }
            Text(
                text = stringResource(uiState.exerciseStatusResource),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (uiState.debuffSecondsRemaining > 0) {
                    stringResource(
                        R.string.enemy_ability_active,
                        stringResource(enemyAbilityNameResource(requireNotNull(enemy).id)),
                        enemy.ability.attackReductionPercent,
                        uiState.debuffSecondsRemaining,
                    )
                } else {
                    stringResource(R.string.enemy_attack_in, uiState.enemyAttackSecondsRemaining)
                },
                color = if (uiState.debuffSecondsRemaining > 0) {
                    Color(0xFFFF7B72)
                } else {
                    Color.White.copy(alpha = 0.72f)
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(
                    R.string.attack_power_tracking,
                    (uiState.playerAttackMultiplier * 100).toInt(),
                    stringResource(trackingStateResource(uiState.trackingState)),
                ),
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
                    Text(stringResource(R.string.simulate_repetition))
                }
            }
            OutlinedButton(
                onClick = onBackToMenu,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.back_to_menu))
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
