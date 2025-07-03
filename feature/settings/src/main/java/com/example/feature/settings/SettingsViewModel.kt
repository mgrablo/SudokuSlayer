package com.example.feature.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.combine
import com.example.domain.settings.SettingsRepository
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
	val darkMode: DarkMode = DarkMode.SYSTEM,
	val darkColorScheme: ColorScheme = ColorScheme.fromName("mocha"),
	val lightColorScheme: ColorScheme = ColorScheme.fromName("latte"),
	val language: String = "system",
	val leftHandMode: Boolean = false,
	val actionButtonsOnTop: Boolean = false,
	val insightsSummaryCompactLayout: Boolean = true,
	val autoClearNotes: Boolean = true,
)

class SettingsViewModel(
	private val settingsRepository: SettingsRepository,
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
	val lightColorSchemes = settingsRepository.getLightColorSchemes().toPersistentSet()
	val darkColorSchemes = settingsRepository.getDarkColorSchemes().toPersistentSet()

	val uiState: StateFlow<SettingsUiState> = combine(
		settingsRepository.darkMode,
		settingsRepository.darkModeColorScheme,
		settingsRepository.lightModeColorScheme,
		settingsRepository.language,
		settingsRepository.leftHandMode,
		settingsRepository.showActionButtonsOnTop,
		settingsRepository.insightsSummaryCompactLayout,
		settingsRepository.autoClearNotes,
	) {
			darkMode,
			darkColorScheme,
			lightColorScheme,
			language,
			leftHandMode,
			actionButtonsOnTop,
			insightsSummaryCompactLayout,
			autoClearNotes,
		->
		SettingsUiState(
			darkMode = darkMode,
			darkColorScheme = darkColorScheme,
			lightColorScheme = lightColorScheme,
			language = language,
			leftHandMode = leftHandMode,
			actionButtonsOnTop = actionButtonsOnTop,
			insightsSummaryCompactLayout = insightsSummaryCompactLayout,
			autoClearNotes = autoClearNotes,
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
}
