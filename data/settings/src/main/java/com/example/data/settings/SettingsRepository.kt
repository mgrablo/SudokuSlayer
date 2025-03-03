package com.example.data.settings

import com.example.data.core.preferences.PreferenceStorage
import com.example.data.core.preferences.PreferenceStorageSingleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository {
	private val preferenceStorage: PreferenceStorage = PreferenceStorageSingleton.getInstance("settings")

	val theme: Flow<String> = preferenceStorage.getAsFlow(SettingsPreferenceKeys.Theme).map { it.orEmpty() }
	val language: Flow<String> = preferenceStorage.getAsFlow(SettingsPreferenceKeys.Language).map { it.orEmpty() }
	val leftHandMode: Flow<Boolean> = preferenceStorage.getAsFlow(SettingsPreferenceKeys.LeftHandMode).map { it == true }

	suspend fun setTheme(theme: String) {
		preferenceStorage.set(SettingsPreferenceKeys.Theme, theme)
	}

	suspend fun setLanguage(language: String) {
		preferenceStorage.set(SettingsPreferenceKeys.Language, language)
	}

	suspend fun setLeftHandMode(leftHandMode: Boolean) {
		preferenceStorage.set(SettingsPreferenceKeys.LeftHandMode, leftHandMode)
	}
}
