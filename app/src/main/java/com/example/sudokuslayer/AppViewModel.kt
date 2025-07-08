package com.example.sudokuslayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.core.GameRepository
import com.example.domain.settings.SettingsRepository
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

internal class AppViewModel(
	gameRepository: GameRepository,
	settingsRepository: SettingsRepository,
) : ViewModel() {
	val hasActiveGame = gameRepository.hasActiveGame().stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = false,
	)
	val darkMode: StateFlow<DarkMode> = settingsRepository.darkMode.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = DarkMode.SYSTEM,
	)

	val darkModeColorScheme: StateFlow<ColorScheme> =
		settingsRepository.darkModeColorScheme.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = ColorScheme.Mocha(),
		)

	val lightModeColorScheme: StateFlow<ColorScheme> =
		settingsRepository.lightModeColorScheme.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = ColorScheme.Latte(),
		)
}
