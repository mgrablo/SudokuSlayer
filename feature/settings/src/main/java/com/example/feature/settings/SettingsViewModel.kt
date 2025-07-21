package com.example.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.settings.SettingsRepository
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal data class SettingsUiState(
	val appearance: AppearanceSettings = AppearanceSettings(),
	val accessibility: AccessibilitySettings = AccessibilitySettings(),
	val gameplay: GameplaySettings = GameplaySettings(),
)

internal data class AppearanceSettings(
	val darkMode: DarkMode = DarkMode.SYSTEM,
	val darkColorScheme: ColorScheme = ColorScheme.fromName("mocha"),
	val lightColorScheme: ColorScheme = ColorScheme.fromName("latte"),
	val insightsSummaryCompactLayout: Boolean = true,
	val language: String = "system",
)

internal data class AccessibilitySettings(
	val leftHandMode: Boolean = false,
	val actionButtonsOnTop: Boolean = false,
)

internal data class GameplaySettings(
	val autoClearNotes: Boolean = true,
	val highlightMatching: Boolean = true,
	val highlightInvalid: Boolean = true,
	val timerVisibility: Boolean = true,
	val remainingDigitCounts: Boolean = true,
)

internal class SettingsViewModel(private val settingsRepository: SettingsRepository) :
	ViewModel() {
	val lightColorSchemes = settingsRepository.getLightColorSchemes().toPersistentSet()
	val darkColorSchemes = settingsRepository.getDarkColorSchemes().toPersistentSet()

	private val appearenceState: StateFlow<AppearanceSettings> = combine(
		settingsRepository.darkMode,
		settingsRepository.darkModeColorScheme,
		settingsRepository.lightModeColorScheme,
		settingsRepository.language,
		settingsRepository.insightsSummaryCompactLayout,
	) { darkMode, darkColorScheme, lightColorScheme, language, insightsSummaryCompactLayout ->
		AppearanceSettings(
			darkMode = darkMode,
			darkColorScheme = darkColorScheme,
			lightColorScheme = lightColorScheme,
			language = language,
			insightsSummaryCompactLayout = insightsSummaryCompactLayout,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000L),
		initialValue = AppearanceSettings(),
	)

	private val accessibilityState: StateFlow<AccessibilitySettings> = combine(
		settingsRepository.leftHandMode,
		settingsRepository.showActionButtonsOnTop,
	) { leftHandMode, actionButtonsOnTop ->
		AccessibilitySettings(
			leftHandMode = leftHandMode,
			actionButtonsOnTop = actionButtonsOnTop,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000L),
		initialValue = AccessibilitySettings(),
	)

	private val gameplayState: StateFlow<GameplaySettings> = combine(
		settingsRepository.autoClearNotes,
		settingsRepository.highlightMatchingNumbers,
		settingsRepository.highlightInvalidNumbers,
		settingsRepository.timerVisibility,
		settingsRepository.remainingDigitCounts,
	) { autoClearNotes, highlightMatching, highlightInvalid, timerVisibility, remainingDigitCounts ->
		GameplaySettings(
			autoClearNotes = autoClearNotes,
			highlightMatching = highlightMatching,
			highlightInvalid = highlightInvalid,
			timerVisibility = timerVisibility,
			remainingDigitCounts = remainingDigitCounts,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000L),
		initialValue = GameplaySettings(),
	)

	val uiState: StateFlow<SettingsUiState> = combine(
		appearenceState,
		accessibilityState,
		gameplayState,
	) { appearence, accessibility, gameplay ->
		SettingsUiState(
			appearance = appearence,
			accessibility = accessibility,
			gameplay = gameplay,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000L),
		initialValue = SettingsUiState(),
	)

	sealed interface Event {
		data class SetDarkMode(val darkMode: DarkMode) : Event

		data class SetDarkColorScheme(val colorScheme: ColorScheme) : Event

		data class SetLightColorScheme(val colorScheme: ColorScheme) : Event

		data class SetLanguage(val language: String) : Event

		data class ToggleLeftHandMode(val leftHandMode: Boolean) : Event

		data class ToggleActionButtonsOnTop(val actionButtonsOnTop: Boolean) : Event

		data class ToggleInsightsSummaryCompactLayout(val compactLayout: Boolean) : Event
		data class ToggleAutoClearNotes(val autoClearNotes: Boolean) : Event
		data class ToggleHighlightMatching(val highlightMatching: Boolean) : Event
		data class ToggleHighlightInvalid(val highlightInvalid: Boolean) : Event
		data class ToggleTimerVisibility(val timerVisibility: Boolean) : Event
		data class ToggleRemainingDigitCounts(val remainingDigitCounts: Boolean) : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.SetDarkMode -> setDarkMode(event.darkMode)
			is Event.SetLightColorScheme -> setLightColorScheme(event.colorScheme)
			is Event.SetDarkColorScheme -> setDarkColorScheme(event.colorScheme)
			is Event.SetLanguage -> setLanguage(event.language)
			is Event.ToggleLeftHandMode -> toggleLeftHandMode(event.leftHandMode)
			is Event.ToggleActionButtonsOnTop -> toggleActionButtonsOnTop(event.actionButtonsOnTop)
			is Event.ToggleInsightsSummaryCompactLayout -> toggleInsightsSummaryCompactLayout(
				event.compactLayout,
			)

			is Event.ToggleAutoClearNotes -> toggleAutoClearNotes(event.autoClearNotes)
			is Event.ToggleHighlightMatching -> toggleHighlightMatching(event.highlightMatching)
			is Event.ToggleHighlightInvalid -> toggleHighlightInvalid(event.highlightInvalid)
			is Event.ToggleTimerVisibility -> toggleTimerVisibility(event.timerVisibility)
			is Event.ToggleRemainingDigitCounts -> toggleRemainingDigitCounts(event.remainingDigitCounts)
		}
	}

	private fun toggleInsightsSummaryCompactLayout(compactLayout: Boolean) {
		viewModelScope.launch {
			settingsRepository.setInsightsSummaryCompactLayout(compactLayout)
		}
	}

	private fun setDarkMode(darkMode: DarkMode) {
		viewModelScope.launch {
			settingsRepository.setDarkMode(darkMode)
		}
	}

	private fun setDarkColorScheme(colorScheme: ColorScheme) {
		viewModelScope.launch {
			settingsRepository.setDarkColorScheme(colorScheme)
		}
	}

	private fun setLightColorScheme(colorScheme: ColorScheme) {
		viewModelScope.launch {
			settingsRepository.setLightColorScheme(colorScheme)
		}
	}

	private fun setLanguage(language: String) {
		viewModelScope.launch {
			settingsRepository.setLanguage(language)
		}
	}

	private fun toggleLeftHandMode(leftHandMode: Boolean) {
		viewModelScope.launch {
			settingsRepository.setLeftHandMode(leftHandMode)
		}
	}

	private fun toggleActionButtonsOnTop(actionButtonsOnTop: Boolean) {
		viewModelScope.launch {
			settingsRepository.setShowActionButtonsOnTop(actionButtonsOnTop)
		}
	}

	private fun toggleAutoClearNotes(autoClearNotes: Boolean) {
		viewModelScope.launch {
			settingsRepository.setAutoClearNotes(autoClearNotes)
		}
	}

	private fun toggleHighlightMatching(highlightMatching: Boolean) {
		viewModelScope.launch {
			settingsRepository.setHighlightMatchingNumbers(highlightMatching)
		}
	}

	private fun toggleHighlightInvalid(highlightInvalid: Boolean) {
		viewModelScope.launch {
			settingsRepository.setHighlightInvalidNumbers(highlightInvalid)
		}
	}

	private fun toggleTimerVisibility(timerVisibility: Boolean) {
		viewModelScope.launch {
			// When timerVisibility is true (user wants to hide), we save visibility as false.
			settingsRepository.setTimerVisibility(!timerVisibility)
		}
	}

	private fun toggleRemainingDigitCounts(remainingDigitCounts: Boolean) {
		viewModelScope.launch {
			settingsRepository.setRemainingDigitCounts(remainingDigitCounts)
		}
	}
}
