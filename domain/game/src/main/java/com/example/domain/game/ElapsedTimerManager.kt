package com.example.domain.game

import com.example.domain.game.usecases.GetElapsedTimeUseCase
import com.example.domain.game.usecases.SaveElapsedTimeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ElapsedTimerManager(
	private val getElapsedTimeUseCase: GetElapsedTimeUseCase,
	private val saveElapsedTimeUseCase: SaveElapsedTimeUseCase,
) {
	private var isRunning = false
	private var _elapsedTime = MutableStateFlow<Long>(0L)
	private var timerJob: Job? = null
	private var saveJob: Job? = null
	val elapsedTime: StateFlow<Long>
		get() {
			return _elapsedTime
		}

	init {
		CoroutineScope(Dispatchers.Default).launch {
			_elapsedTime.update {
				getElapsedTimeUseCase().firstOrNull() ?: 0L
			}
		}
	}

	fun startTracking() {
		if (isRunning) return

		isRunning = true
		timerJob =
			CoroutineScope(Dispatchers.Default).launch {
				while (isRunning) {
					delay(1000)
					_elapsedTime.update { it + 1 }
				}
			}
		saveJob =
			CoroutineScope(Dispatchers.Default).launch {
				while (isRunning) {
					delay(10000L)
					if (isRunning) {
						saveElapsedTimeUseCase(elapsedTime.value)
					}
				}
			}
	}

	suspend fun stopTracking() {
		if (isRunning) {
			isRunning = false
			timerJob?.cancel()
			saveJob?.cancel()
			saveElapsedTimeUseCase(elapsedTime.value)
		}
	}

	suspend fun resetTimer() {
		_elapsedTime.update {
			0L
		}
		saveElapsedTimeUseCase(elapsedTime.value)
	}
}
