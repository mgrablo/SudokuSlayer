package com.example.domain.game

import com.example.domain.game.usecases.GetElapsedTimeUseCase
import com.example.domain.game.usecases.SaveElapsedTimeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class ElapsedTimerManager(
	private val scope: CoroutineScope,
	private val getElapsedTimeUseCase: GetElapsedTimeUseCase,
	private val saveElapsedTimeUseCase: SaveElapsedTimeUseCase,
) {
	private val isRunning = AtomicBoolean(false)
	private val _elapsedTime = MutableStateFlow<Long>(0L)
	private var timerJob: Job? = null
	private var saveJob: Job? = null
	val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

	init {
		scope.launch {
			_elapsedTime.update {
				getElapsedTimeUseCase().firstOrNull() ?: 0L
			}
		}
	}

	fun startTracking() {
		if (isRunning.getAndSet(true)) return
		scope.launch {
			_elapsedTime.update {
				getElapsedTimeUseCase().firstOrNull() ?: 0L
			}
		}

		timerJob =
			scope.launch(Dispatchers.Default) {
				while (isRunning.get()) {
					delay(1000)
					_elapsedTime.update { it + 1 }
				}
			}
		saveJob =
			scope.launch(Dispatchers.Default) {
				while (isRunning.get()) {
					delay(10000L)
					if (isRunning.get()) {
						saveElapsedTimeUseCase(elapsedTime.value)
					}
				}
			}
	}

	suspend fun stopTracking() {
		if (isRunning.getAndSet(false)) {
			timerJob?.cancelAndJoin()
			saveJob?.cancelAndJoin()
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
