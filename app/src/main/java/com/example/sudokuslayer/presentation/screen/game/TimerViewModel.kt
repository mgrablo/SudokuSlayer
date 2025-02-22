package com.example.sudokuslayer.presentation.screen.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sudokuslayer.data.datastore.SudokuDataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerViewModel(
	private val dataStoreRepository: SudokuDataStoreRepository,
) : ViewModel() {
	private var isRunning = false
	private val _elapsedTime = MutableStateFlow(0L)
	val elapsedTime: StateFlow<Long> = _elapsedTime

	init {
		loadSavedTime()
	}

	fun loadSavedTime() {
		viewModelScope.launch {
			dataStoreRepository.elapsedTimeProto.collect {
				_elapsedTime.value = it
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
		viewModelScope.launch(Dispatchers.IO) {
			dataStoreRepository.updateElapsedTime(_elapsedTime.value)
		}
	}
}

class TimerViewModelFactory(
	private val dataStoreRepository: SudokuDataStoreRepository,
) : ViewModelProvider.NewInstanceFactory() {
	override fun <T : ViewModel> create(modelClass: Class<T>): T = TimerViewModel(dataStoreRepository) as T
}
