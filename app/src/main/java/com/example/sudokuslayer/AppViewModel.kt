package com.example.sudokuslayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.core.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppUiState(val hasActiveGame: Boolean = false)

class AppViewModel(gameRepository: GameRepository) : ViewModel() {
	private val _uiState = MutableStateFlow(AppUiState())
	val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			gameRepository.hasActiveGame().collect { hasActiveGame ->
				_uiState.value = _uiState.value.copy(
					hasActiveGame = hasActiveGame,
				)
			}
		}
	}
}
