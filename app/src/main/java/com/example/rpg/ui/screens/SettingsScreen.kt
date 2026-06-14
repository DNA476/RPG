package com.example.rpg.ui.screens

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.core.os.LocaleListCompat
import com.example.rpg.R

enum class SupportedAppLanguage(
    val languageTag: String?,
    @param:StringRes val labelResource: Int,
) {
    SYSTEM(null, R.string.language_system),
    RUSSIAN("ru", R.string.language_russian),
    ENGLISH("en", R.string.language_english),
    GERMAN("de", R.string.language_german),
    SPANISH("es", R.string.language_spanish),
    FRENCH("fr", R.string.language_french),
    PORTUGUESE("pt", R.string.language_portuguese);

    companion object {
        fun fromLanguageTag(languageTag: String?): SupportedAppLanguage {
            val language = languageTag
                ?.substringBefore('-')
                ?.substringBefore('_')
                ?.lowercase()
                .orEmpty()
            return entries.firstOrNull { it.languageTag == language } ?: SYSTEM
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedLanguage = SupportedAppLanguage.fromLanguageTag(
        AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag(),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF17352B), Color(0xFF0E1714), Color(0xFF080A09)),
                ),
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        OutlinedButton(onClick = onBack) {
            Text(stringResource(R.string.back))
        }
        Text(
            text = stringResource(R.string.settings),
            color = Color(0xFFFFD166),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.app_language),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.app_language_description),
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SupportedAppLanguage.entries.forEach { language ->
                LanguageOption(
                    language = language,
                    selected = language == selectedLanguage,
                    onClick = {
                        val locales = language.languageTag?.let(LocaleListCompat::forLanguageTags)
                            ?: LocaleListCompat.getEmptyLocaleList()
                        AppCompatDelegate.setApplicationLocales(locales)
                    },
                )
            }
        }
    }
}

@Composable
private fun LanguageOption(
    language: SupportedAppLanguage,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (selected) {
            Color(0xFFFFD166).copy(alpha = 0.18f)
        } else {
            Color.White.copy(alpha = 0.08f)
        },
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
            )
            Text(
                text = stringResource(language.labelResource),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}
