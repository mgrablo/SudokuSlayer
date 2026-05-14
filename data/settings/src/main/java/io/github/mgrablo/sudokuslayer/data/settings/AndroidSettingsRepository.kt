package io.github.mgrablo.sudokuslayer.data.settings

import io.github.mgrablo.sudokuslayer.data.core.preferences.PreferenceStorage
import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
import io.github.mgrablo.sudokuslayer.domain.settings.models.ColorScheme
import io.github.mgrablo.sudokuslayer.domain.settings.models.DarkMode
import io.github.mgrablo.sudokuslayer.domain.settings.models.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AndroidSettingsRepository(private val preferenceStorage: PreferenceStorage) :
	SettingsRepository {
	override val darkMode: Flow<DarkMode> =
		preferenceStorage
			.getAsFlow(SettingsPreferenceKeys.DarkMode)
			.map { it.orEmpty() }
			.map { DarkMode.fromName(it) }

	override val darkModeColorScheme: Flow<ColorScheme> =
		preferenceStorage
			.getAsFlow(SettingsPreferenceKeys.DarkColorScheme)
			.map { it.orEmpty() }
			.map { ColorScheme.fromName(it) }

	override val lightModeColorScheme: Flow<ColorScheme> =
		preferenceStorage
			.getAsFlow(SettingsPreferenceKeys.LightColorScheme)
			.map { it.orEmpty() }
			.map { ColorScheme.fromName(it) }

	override val language: Flow<Language> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.Language,
	).map {
		Language.fromTag(it.orEmpty())
	}
	override val leftHandMode: Flow<Boolean> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.LeftHandMode,
	).map {
		it == true
	}
	override val showActionButtonsOnTop: Flow<Boolean> =
		preferenceStorage.getAsFlow(SettingsPreferenceKeys.ShowActionButtonsOnTop)
			.map { it == true }

	override val insightsSummaryCompactLayout: Flow<Boolean> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.InsightsSummaryCompactLayout,
	).map { it == true }

	override val autoClearNotes: Flow<Boolean> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.AutoClearNotes,
	).map { it == true }

	override val highlightMatchingNumbers: Flow<Boolean> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.HighlightMatchingNumbers,
	).map { it == true }

	override val highlightInvalidNumbers: Flow<Boolean> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.HighlightInvalidNumbers,
	).map { it == true }

	override val timerVisibility: Flow<Boolean> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.TimerVisibility,
	).map { it == true }

	override val remainingDigitCounts: Flow<Boolean> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.RemainingDigitCounts,
	).map { it == true }

	override suspend fun setDarkMode(darkMode: DarkMode) {
		preferenceStorage.set(SettingsPreferenceKeys.DarkMode, darkMode.displayName)
	}

	override suspend fun setDarkColorScheme(colorScheme: ColorScheme) {
		preferenceStorage.set(SettingsPreferenceKeys.DarkColorScheme, colorScheme.name)
	}

	override suspend fun setLightColorScheme(colorScheme: ColorScheme) {
		preferenceStorage.set(SettingsPreferenceKeys.LightColorScheme, colorScheme.name)
	}

	override suspend fun setLanguage(language: Language) {
		preferenceStorage.set(SettingsPreferenceKeys.Language, language.tag)
	}

	override suspend fun setLeftHandMode(leftHandMode: Boolean) {
		preferenceStorage.set(SettingsPreferenceKeys.LeftHandMode, leftHandMode)
	}

	override suspend fun setShowActionButtonsOnTop(actionButtonsOnTop: Boolean) {
		preferenceStorage.set(SettingsPreferenceKeys.ShowActionButtonsOnTop, actionButtonsOnTop)
	}

	override suspend fun setInsightsSummaryCompactLayout(compactLayout: Boolean) {
		preferenceStorage.set(SettingsPreferenceKeys.InsightsSummaryCompactLayout, compactLayout)
	}

	override suspend fun setAutoClearNotes(autoClearNotes: Boolean) {
		preferenceStorage.set(SettingsPreferenceKeys.AutoClearNotes, autoClearNotes)
	}

	override suspend fun setHighlightMatchingNumbers(highlightMatchingNumbers: Boolean) {
		preferenceStorage.set(
			SettingsPreferenceKeys.HighlightMatchingNumbers,
			highlightMatchingNumbers,
		)
	}

	override suspend fun setHighlightInvalidNumbers(highlightInvalidNumbers: Boolean) {
		preferenceStorage.set(
			SettingsPreferenceKeys.HighlightInvalidNumbers,
			highlightInvalidNumbers,
		)
	}

	override suspend fun setTimerVisibility(timerVisibility: Boolean) {
		preferenceStorage.set(
			SettingsPreferenceKeys.TimerVisibility,
			timerVisibility,
		)
	}

	override suspend fun setRemainingDigitCounts(remainingDigitCounts: Boolean) {
		preferenceStorage.set(
			SettingsPreferenceKeys.RemainingDigitCounts,
			remainingDigitCounts,
		)
	}

	override fun getAvailableColorSchemes(): List<ColorScheme> = ColorScheme.getAvailableColorSchemes()

	override fun getAvailableLanguages(): List<Language> = Language.getAvailableLanguages()

	override fun getDarkColorSchemes(): List<ColorScheme> = ColorScheme.getDarkColorSchemes()

	override fun getLightColorSchemes(): List<ColorScheme> = ColorScheme.getLightColorSchemes()
}
