package com.example.feature.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.settings.SettingsRepository
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
	val settingsRepository: SettingsRepository,
	val savedStateHandle: SavedStateHandle,
) : ViewModel() {
	val darkMode: StateFlow<DarkMode> =
		settingsRepository.darkMode.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = DarkMode.SYSTEM,
		)

	val darkColorScheme: StateFlow<ColorScheme> =
		settingsRepository.darkModeColorScheme.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = ColorScheme.fromName("mocha"),
		)

	val lightColorScheme: StateFlow<ColorScheme> =
		settingsRepository.lightModeColorScheme.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = ColorScheme.fromName("latte"),
		)

	val language: StateFlow<String> =
		settingsRepository.language.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = "system",
		)

	val leftHandMode: StateFlow<Boolean> =
		settingsRepository.leftHandMode.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = false,
		)

	val actionButtonsOnTop: StateFlow<Boolean> =
		settingsRepository.showActionButtonsOnTop.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = false,
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
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.SetDarkMode -> setDarkMode(event.darkMode)
			is Event.SetLightColorScheme -> setLightColorScheme(event.scheme)
			is Event.SetDarkColorScheme -> setDarkColorScheme(event.scheme)
			is Event.SetLanguage -> setLanguage(event.language)
			is Event.ToggleLeftHandMode -> toggleLeftHandMode(event.leftHandMode)
			is Event.ToggleActionButtonsOnTop -> toggleActionButtonsOnTop(event.actionButtonsOnTop)
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
