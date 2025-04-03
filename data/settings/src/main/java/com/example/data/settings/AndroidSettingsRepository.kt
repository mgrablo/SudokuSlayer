package com.example.data.settings

import com.example.data.core.preferences.PreferenceStorage
import com.example.domain.settings.SettingsRepository
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
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

	override val language: Flow<String> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.Language,
	).map {
		it.orEmpty()
	}
	override val leftHandMode: Flow<Boolean> = preferenceStorage.getAsFlow(
		SettingsPreferenceKeys.LeftHandMode,
	).map {
		it == true
	}
	override val showActionButtonsOnTop: Flow<Boolean> =
		preferenceStorage.getAsFlow(SettingsPreferenceKeys.ShowActionButtonsOnTop).map { it == true }

	override suspend fun setDarkMode(darkMode: DarkMode) {
		preferenceStorage.set(SettingsPreferenceKeys.DarkMode, darkMode.displayName)
	}

	override suspend fun setDarkColorScheme(colorScheme: ColorScheme) {
		preferenceStorage.set(SettingsPreferenceKeys.DarkColorScheme, colorScheme.name)
	}

	override suspend fun setLightColorScheme(colorScheme: ColorScheme) {
		preferenceStorage.set(SettingsPreferenceKeys.LightColorScheme, colorScheme.name)
	}

	override suspend fun setLanguage(language: String) {
		preferenceStorage.set(SettingsPreferenceKeys.Language, language)
	}

	override suspend fun setLeftHandMode(leftHandMode: Boolean) {
		preferenceStorage.set(SettingsPreferenceKeys.LeftHandMode, leftHandMode)
	}

	override suspend fun setShowActionButtonsOnTop(actionButtonsOnTop: Boolean) {
		preferenceStorage.set(SettingsPreferenceKeys.ShowActionButtonsOnTop, actionButtonsOnTop)
	}

	override fun getAvailableColorSchemes(): List<String> = ColorScheme.getAvailableColorSchemes()

	override fun getDarkColorSchemes(): List<String> = ColorScheme.getDarkColorSchemes()

	override fun getLightColorSchemes(): List<String> = ColorScheme.getLightColorSchemes()
}
