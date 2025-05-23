package com.example.feature.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.settings.SettingsRepository
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
	private val settingsRepository: SettingsRepository,
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
	private fun <T> Flow<T>.stateInViewModel(initialValue: T): StateFlow<T> = this.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000L),
		initialValue = initialValue,
	)

	val darkMode: StateFlow<DarkMode> =
		settingsRepository.darkMode.stateInViewModel(
			initialValue = DarkMode.SYSTEM,
		)

	val darkColorScheme: StateFlow<ColorScheme> =
		settingsRepository.darkModeColorScheme.stateInViewModel(
			initialValue = ColorScheme.fromName("mocha"),
		)

	val lightColorScheme: StateFlow<ColorScheme> =
		settingsRepository.lightModeColorScheme.stateInViewModel(
			initialValue = ColorScheme.fromName("latte"),
		)

	val language: StateFlow<String> =
		settingsRepository.language.stateInViewModel(
			initialValue = "system",
		)

	val leftHandMode: StateFlow<Boolean> =
		settingsRepository.leftHandMode.stateInViewModel(
			initialValue = false,
		)

	val actionButtonsOnTop: StateFlow<Boolean> =
		settingsRepository.showActionButtonsOnTop.stateInViewModel(
			initialValue = false,
		)

	val insightsSummaryCompactLayout: StateFlow<Boolean> =
		settingsRepository.insightsSummaryCompactLayout.stateInViewModel(
			initialValue = true,
		)

	val lightColorSchemes = settingsRepository.getLightColorSchemes().toPersistentSet()
	val darkColorSchemes = settingsRepository.getDarkColorSchemes().toPersistentSet()

	sealed interface Event {
		data class SetDarkMode(val darkMode: String) : Event

		data class SetDarkColorScheme(val scheme: String) : Event

		data class SetLightColorScheme(val scheme: String) : Event

		data class SetLanguage(val language: String) : Event

		data class ToggleLeftHandMode(val leftHandMode: Boolean) : Event

		data class ToggleActionButtonsOnTop(val actionButtonsOnTop: Boolean) : Event

		data class ToggleInsightsSummaryCompactLayout(val compactLayout: Boolean) : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.SetDarkMode -> setDarkMode(event.darkMode)
			is Event.SetLightColorScheme -> setLightColorScheme(event.scheme)
			is Event.SetDarkColorScheme -> setDarkColorScheme(event.scheme)
			is Event.SetLanguage -> setLanguage(event.language)
			is Event.ToggleLeftHandMode -> toggleLeftHandMode(event.leftHandMode)
			is Event.ToggleActionButtonsOnTop -> toggleActionButtonsOnTop(event.actionButtonsOnTop)
			is Event.ToggleInsightsSummaryCompactLayout -> toggleInsightsSummaryCompactLayout(
				event.compactLayout,
			)
		}
	}

	private fun toggleInsightsSummaryCompactLayout(compactLayout: Boolean) {
		viewModelScope.launch {
			settingsRepository.setInsightsSummaryCompactLayout(compactLayout)
		}
	}

	private fun setDarkMode(darkMode: String) {
		viewModelScope.launch {
			settingsRepository.setDarkMode(DarkMode.fromName(darkMode))
		}
	}

	private fun setDarkColorScheme(colorScheme: String) {
		viewModelScope.launch {
			settingsRepository.setDarkColorScheme(ColorScheme.fromName(colorScheme))
		}
	}

	private fun setLightColorScheme(colorScheme: String) {
		viewModelScope.launch {
			settingsRepository.setLightColorScheme(ColorScheme.fromName(colorScheme))
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
}
