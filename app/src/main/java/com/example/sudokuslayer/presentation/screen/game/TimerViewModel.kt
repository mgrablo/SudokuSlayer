package com.example.sudokuslayer.presentation.screen.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.game.ProtoGameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerViewModel(
	private val dataStoreRepository: ProtoGameRepository,
) : ViewModel() {
	private val _elapsedTime = MutableStateFlow(0L)
	val elapsedTime: StateFlow<Long> = _elapsedTime

	init {
		loadSavedTime()
	}

	fun loadSavedTime() {
		viewModelScope.launch {
			dataStoreRepository.getGame().collect {
				_elapsedTime.value = it.elapsedTime
			}
		}
	}

	fun resetTimer() {
		viewModelScope.launch {
			_elapsedTime.value = 0
			saveElapsedTime()
		}
	}

	fun updateElapsedTime(time: Long) {
		viewModelScope.launch {
			_elapsedTime.value = time
			saveElapsedTime()
		}
	}

	fun saveElapsedTime() {
		viewModelScope.launch {
			dataStoreRepository.updateElapsedTime(_elapsedTime.value)
		}
	}
}
