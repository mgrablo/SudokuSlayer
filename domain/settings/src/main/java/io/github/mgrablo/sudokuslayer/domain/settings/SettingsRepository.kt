package io.github.mgrablo.sudokuslayer.domain.settings

import io.github.mgrablo.sudokuslayer.domain.settings.models.ColorScheme
import io.github.mgrablo.sudokuslayer.domain.settings.models.DarkMode
import io.github.mgrablo.sudokuslayer.domain.settings.models.Language
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
	val darkMode: Flow<DarkMode>
	val darkModeColorScheme: Flow<ColorScheme>
	val lightModeColorScheme: Flow<ColorScheme>
	val language: Flow<Language>
	val leftHandMode: Flow<Boolean>
	val showActionButtonsOnTop: Flow<Boolean>
	val insightsSummaryCompactLayout: Flow<Boolean>
	val autoClearNotes: Flow<Boolean>
	val highlightMatchingNumbers: Flow<Boolean>
	val highlightInvalidNumbers: Flow<Boolean>
	val timerVisibility: Flow<Boolean>
	val remainingDigitCounts: Flow<Boolean>

	suspend fun setDarkMode(darkMode: DarkMode)

	suspend fun setDarkColorScheme(colorScheme: ColorScheme)

	suspend fun setLightColorScheme(colorScheme: ColorScheme)

	suspend fun setLanguage(language: Language)

	suspend fun setLeftHandMode(leftHandMode: Boolean)

	suspend fun setShowActionButtonsOnTop(actionButtonsOnTop: Boolean)

	suspend fun setInsightsSummaryCompactLayout(compactLayout: Boolean)

	suspend fun setAutoClearNotes(autoClearNotes: Boolean)

	suspend fun setHighlightMatchingNumbers(highlightMatchingNumbers: Boolean)

	suspend fun setHighlightInvalidNumbers(highlightInvalidNumbers: Boolean)

	suspend fun setTimerVisibility(timerVisibility: Boolean)

	suspend fun setRemainingDigitCounts(remainingDigitCounts: Boolean)

	fun getAvailableColorSchemes(): List<ColorScheme>

	fun getAvailableLanguages(): List<Language>

	fun getDarkColorSchemes(): List<ColorScheme>

	fun getLightColorSchemes(): List<ColorScheme>
}
