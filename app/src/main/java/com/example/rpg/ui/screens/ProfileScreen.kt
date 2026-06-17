package com.example.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpg.R
import com.example.rpg.data.profile.UserSex
import com.example.rpg.ui.viewmodel.ProfileFormUiState

@Composable
fun ProfileScreen(
    form: ProfileFormUiState,
    isOnboarding: Boolean,
    onWeightChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onSexSelected: (UserSex?) -> Unit,
    onSave: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit,
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
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        if (!isOnboarding) {
            OutlinedButton(onClick = onBack) {
                Text(stringResource(R.string.back))
            }
        }
        Text(
            text = stringResource(if (isOnboarding) R.string.onboarding_title else R.string.profile),
            color = Color(0xFFFFD166),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = stringResource(R.string.profile_intro),
            color = Color.White.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodyLarge,
        )
        OutlinedTextField(
            value = form.weightText,
            onValueChange = onWeightChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.weight_label)) },
            placeholder = { Text(stringResource(R.string.weight_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = form.weightError,
            supportingText = if (form.weightError) {
                { Text(stringResource(R.string.weight_error)) }
            } else {
                null
            },
        )
        OutlinedTextField(
            value = form.heightText,
            onValueChange = onHeightChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.height_label)) },
            placeholder = { Text(stringResource(R.string.height_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = form.heightError,
            supportingText = if (form.heightError) {
                { Text(stringResource(R.string.height_error)) }
            } else {
                null
            },
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.sex_label),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SexChip(
                    label = stringResource(R.string.sex_unspecified),
                    selected = form.sex == null,
                    onClick = { onSexSelected(null) },
                    modifier = Modifier.weight(1f),
                )
                SexChip(
                    label = stringResource(R.string.sex_male),
                    selected = form.sex == UserSex.MALE,
                    onClick = { onSexSelected(UserSex.MALE) },
                    modifier = Modifier.weight(1f),
                )
                SexChip(
                    label = stringResource(R.string.sex_female),
                    selected = form.sex == UserSex.FEMALE,
                    onClick = { onSexSelected(UserSex.FEMALE) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD166),
                contentColor = Color(0xFF111111),
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            Text(
                text = stringResource(if (isOnboarding) R.string.continue_action else R.string.save),
                fontWeight = FontWeight.Bold,
            )
        }
        if (isOnboarding) {
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                Text(stringResource(R.string.skip))
            }
        }
    }
}

@Composable
private fun SexChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier,
    )
}
