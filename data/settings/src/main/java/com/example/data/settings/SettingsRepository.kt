package com.example.data.settings

import com.example.data.core.model.ColorScheme
import com.example.data.core.model.DarkMode
import com.example.data.core.preferences.PreferenceStorage
import com.example.data.core.preferences.PreferenceStorageSingleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository {
	private val preferenceStorage: PreferenceStorage = PreferenceStorageSingleton.getInstance("settings")

	val darkMode: Flow<DarkMode> =
		preferenceStorage
			.getAsFlow(SettingsPreferenceKeys.DarkMode)
			.map { it.orEmpty() }
			.map { DarkMode.fromName(it) }

	val darkModeColorScheme: Flow<ColorScheme> =
		preferenceStorage
			.getAsFlow(SettingsPreferenceKeys.DarkColorScheme)
			.map { it.orEmpty() }
			.map { ColorScheme.fromName(it) }

	val lightModeColorScheme: Flow<ColorScheme> =
		preferenceStorage
			.getAsFlow(SettingsPreferenceKeys.LightColorScheme)
			.map { it.orEmpty() }
			.map { ColorScheme.fromName(it) }

	val language: Flow<String> = preferenceStorage.getAsFlow(SettingsPreferenceKeys.Language).map { it.orEmpty() }
	val leftHandMode: Flow<Boolean> = preferenceStorage.getAsFlow(SettingsPreferenceKeys.LeftHandMode).map { it == true }
	val showActionButtonsOnTop: Flow<Boolean> =
		preferenceStorage.getAsFlow(SettingsPreferenceKeys.ShowActionButtonsOnTop).map { it == true }

	suspend fun setDarkMode(darkMode: DarkMode) {
		preferenceStorage.set(SettingsPreferenceKeys.DarkMode, darkMode.displayName)
	}

	suspend fun setDarkColorScheme(colorScheme: ColorScheme) {
		preferenceStorage.set(SettingsPreferenceKeys.DarkColorScheme, colorScheme.name)
	}

	suspend fun setLightColorScheme(colorScheme: ColorScheme) {
		preferenceStorage.set(SettingsPreferenceKeys.LightColorScheme, colorScheme.name)
	}

	suspend fun setLanguage(language: String) {
		preferenceStorage.set(SettingsPreferenceKeys.Language, language)
	}

	suspend fun setLeftHandMode(leftHandMode: Boolean) {
		preferenceStorage.set(SettingsPreferenceKeys.LeftHandMode, leftHandMode)
	}

	suspend fun setShowActionButtonsOnTop(actionButtonsOnTop: Boolean) {
		preferenceStorage.set(SettingsPreferenceKeys.ShowActionButtonsOnTop, actionButtonsOnTop)
	}

	fun getAvailableColorSchemes(): List<String> = ColorScheme.getAvailableColorSchemes()

	fun getDarkColorSchemes(): List<String> = ColorScheme.getDarkColorSchemes()

	fun getLightColorSchemes(): List<String> = ColorScheme.getLightColorSchemes()
}
