package com.example.sudokuslayer.presentation.screen.game

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sudokuslayer.data.datastore.SudokuDataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TimerViewModel(
	private val dataStoreRepository: SudokuDataStoreRepository,
) : ViewModel(), DefaultLifecycleObserver {
	private var isRunning = false
	private val _elapsedTime = MutableStateFlow(0L)
	val elapsedTime: StateFlow<Long> = _elapsedTime

	fun startTimer() {
		if (isRunning) return
		isRunning = true
		viewModelScope.launch {
			while (isRunning) {
				delay(1000L)
				_elapsedTime.value += 1
			}
		}
	}

	fun pauseTimer() {
		isRunning = false
	}

	fun resetTimer() {
		_elapsedTime.value = 0
		saveElapsedTime()
	}

	fun loadSavedTime() {
		viewModelScope.launch {
			_elapsedTime.value = dataStoreRepository.elapsedTimeProto.firstOrNull() ?: 0
		}
	}

	fun saveElapsedTime() {
		viewModelScope.launch(Dispatchers.IO) {
			dataStoreRepository.updateElapsedTime(_elapsedTime.value)
		}
	}

	override fun onPause(owner: LifecycleOwner) {
		super.onPause(owner)
		pauseTimer()
		saveElapsedTime()
	}

	override fun onResume(owner: LifecycleOwner) {
		super.onResume(owner)
		loadSavedTime()
		startTimer()
	}
}

class TimerViewModelFactory(
	private val dataStoreRepository: SudokuDataStoreRepository,
) : ViewModelProvider.NewInstanceFactory() {
	override fun <T : ViewModel> create(modelClass: Class<T>): T = TimerViewModel(dataStoreRepository) as T
}
