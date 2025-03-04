package com.example.sudokuslayer.presentation.screen.settings

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.data.core.model.ColorScheme
import com.example.data.core.model.DarkMode
import com.example.data.settings.SettingsRepository
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

	val lightColorSchemes = settingsRepository.getLightColorSchemes().toPersistentSet()
	val darkColorSchemes = settingsRepository.getDarkColorSchemes().toPersistentSet()

	sealed interface Event {
		data class SetDarkMode(
			val darkMode: String,
		) : Event

		data class SetDarkColorScheme(
			val scheme: String,
		) : Event

		data class SetLightColorScheme(
			val scheme: String,
		) : Event

		data class SetLanguage(
			val language: String,
		) : Event

		data class ToggleLeftHandMode(
			val leftHandMode: Boolean,
		) : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.SetDarkMode -> setDarkMode(event.darkMode)
			is Event.SetLightColorScheme -> setLightColorScheme(event.scheme)
			is Event.SetDarkColorScheme -> setDarkColorScheme(event.scheme)
			is Event.SetLanguage -> setLanguage(event.language)
			is Event.ToggleLeftHandMode -> toggleLeftHandMode(event.leftHandMode)
		}
	}

	private fun setDarkMode(darkMode: String) {
		viewModelScope.launch {
			settingsRepository.setDarkMode(DarkMode.fromName(darkMode))
			Log.d("test", darkMode)
			Log.d("test", DarkMode.fromName(darkMode).toString())
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

	companion object {
		val Factory:
			ViewModelProvider.Factory =
			object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(
					modelClass: Class<T>,
					extras: CreationExtras,
				): T {
					val savedStateHandle = extras.createSavedStateHandle()

					return SettingsViewModel(
						settingsRepository = SettingsRepository(),
						savedStateHandle = savedStateHandle,
					) as T
				}
			}
	}
}
