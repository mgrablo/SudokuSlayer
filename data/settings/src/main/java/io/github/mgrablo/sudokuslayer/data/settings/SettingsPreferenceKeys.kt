package io.github.mgrablo.sudokuslayer.data.settings

import io.github.mgrablo.sudokuslayer.data.core.preferences.PreferenceStorage

interface SettingsPreferenceKeys {
	data object DarkMode :
		PreferenceStorage.Key.StringKey(name = "dark_mode", defaultValue = "system")

	data object DarkColorScheme : PreferenceStorage.Key.StringKey(
		name = "dark_scheme",
		defaultValue = "mocha",
	)

	data object LightColorScheme : PreferenceStorage.Key.StringKey(
		name = "light_scheme",
		defaultValue = "latte",
	)

	data object Language :
		PreferenceStorage.Key.StringKey(name = "language", defaultValue = "system")

	data object LeftHandMode : PreferenceStorage.Key.BooleanKey(
		name = "left_hand_mode",
		defaultValue = false,
	)

	data object ShowActionButtonsOnTop : PreferenceStorage.Key.BooleanKey(
		name = "action_buttons_on_top",
		defaultValue = false,
	)

	data object InsightsSummaryCompactLayout : PreferenceStorage.Key.BooleanKey(
		name = "insights_summary_compact_layout",
		defaultValue = true,
	)

	data object AutoClearNotes : PreferenceStorage.Key.BooleanKey(
		name = "auto_clear_notes",
		defaultValue = true,
	)

	data object HighlightMatchingNumbers : PreferenceStorage.Key.BooleanKey(
		name = "highlight_matching_numbers",
		defaultValue = true,
	)

	data object HighlightInvalidNumbers : PreferenceStorage.Key.BooleanKey(
		name = "highlight_invalid_numbers",
		defaultValue = true,
	)

	data object TimerVisibility : PreferenceStorage.Key.BooleanKey(
		name = "timer_visibility",
		defaultValue = true,
	)

	data object RemainingDigitCounts : PreferenceStorage.Key.BooleanKey(
		name = "remaining_digit_counts",
		defaultValue = true,
	)
}
