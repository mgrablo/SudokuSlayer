package com.example.data.settings

import com.example.data.core.preferences.PreferenceStorage

interface SettingsPreferenceKeys {
	data object DarkMode : PreferenceStorage.Key.StringKey(name = "dark_mode", defaultValue = "system")

	data object DarkColorScheme : PreferenceStorage.Key.StringKey(name = "dark_scheme", defaultValue = "mocha")

	data object LightColorScheme : PreferenceStorage.Key.StringKey(name = "light_scheme", defaultValue = "latte")

	data object Language : PreferenceStorage.Key.StringKey(name = "language", defaultValue = "system")

	data object LeftHandMode : PreferenceStorage.Key.BooleanKey(name = "left_hand_mode", defaultValue = false)
}
