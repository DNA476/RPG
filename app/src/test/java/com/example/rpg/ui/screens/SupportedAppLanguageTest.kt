package com.example.rpg.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class SupportedAppLanguageTest {
    @Test
    fun resolvesSupportedLanguageWithRegion() {
        assertEquals(
            SupportedAppLanguage.PORTUGUESE,
            SupportedAppLanguage.fromLanguageTag("pt-BR"),
        )
    }

    @Test
    fun usesSystemForEmptyOrUnsupportedLanguage() {
        assertEquals(
            SupportedAppLanguage.SYSTEM,
            SupportedAppLanguage.fromLanguageTag(null),
        )
        assertEquals(
            SupportedAppLanguage.SYSTEM,
            SupportedAppLanguage.fromLanguageTag("it"),
        )
    }
}
