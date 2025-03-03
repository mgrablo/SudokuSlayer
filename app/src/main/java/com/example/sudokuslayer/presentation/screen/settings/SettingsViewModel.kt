package com.example.sudokuslayer.presentation.screen.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.data.core.preferences.PreferencesManager
import com.example.sudokuslayer.App

class SettingsViewModel(
	preferencesManager: PreferencesManager,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {
	companion object {
		val Factory:
			ViewModelProvider.Factory =
			object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(
					modelClass: Class<T>,
					extras: CreationExtras,
				): T {
					val application = checkNotNull(extras[APPLICATION_KEY])
					val savedStateHandle = extras.createSavedStateHandle()

					return SettingsViewModel(
						preferencesManager = (application as App).preferencesManager,
						savedStateHandle,
					) as T
				}
			}
	}
}
