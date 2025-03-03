package com.example.sudokuslayer.presentation.screen.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.data.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
	val settingsRepository: SettingsRepository,
	val savedStateHandle: SavedStateHandle,
) : ViewModel() {
	val theme: StateFlow<String> =
		settingsRepository.theme.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = "system",
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

	fun setTheme(theme: String) {
		viewModelScope.launch {
			settingsRepository.setTheme(theme)
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
