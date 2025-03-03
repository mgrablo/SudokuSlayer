package com.example.data.settings

import com.example.data.core.preferences.PreferenceStorage

interface SettingsPreferenceKeys {
	data object Theme : PreferenceStorage.Key.StringKey(name = "theme", defaultValue = "system")

	data object Language : PreferenceStorage.Key.StringKey(name = "language", defaultValue = "system")

	data object LeftHandMode : PreferenceStorage.Key.BooleanKey(name = "left_hand_mode", defaultValue = false)
}
