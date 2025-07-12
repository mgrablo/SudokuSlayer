package com.example.domain.settings

import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
	val darkMode: Flow<DarkMode>
	val darkModeColorScheme: Flow<ColorScheme>
	val lightModeColorScheme: Flow<ColorScheme>
	val language: Flow<String>
	val leftHandMode: Flow<Boolean>
	val showActionButtonsOnTop: Flow<Boolean>
	val insightsSummaryCompactLayout: Flow<Boolean>
	val autoClearNotes: Flow<Boolean>
	val highlightMatchingNumbers: Flow<Boolean>
	val highlightInvalidNumbers: Flow<Boolean>
	val timerVisibility: Flow<Boolean>

	suspend fun setDarkMode(darkMode: DarkMode)

	suspend fun setDarkColorScheme(colorScheme: ColorScheme)

	suspend fun setLightColorScheme(colorScheme: ColorScheme)

	suspend fun setLanguage(language: String)

	suspend fun setLeftHandMode(leftHandMode: Boolean)

	suspend fun setShowActionButtonsOnTop(actionButtonsOnTop: Boolean)

	suspend fun setInsightsSummaryCompactLayout(compactLayout: Boolean)

	suspend fun setAutoClearNotes(autoClearNotes: Boolean)

	suspend fun setHighlightMatchingNumbers(highlightMatchingNumbers: Boolean)

	suspend fun setHighlightInvalidNumbers(highlightInvalidNumbers: Boolean)

	suspend fun setTimerVisibility(timerVisibility: Boolean)

	fun getAvailableColorSchemes(): List<ColorScheme>

	fun getDarkColorSchemes(): List<ColorScheme>

	fun getLightColorSchemes(): List<ColorScheme>
}
