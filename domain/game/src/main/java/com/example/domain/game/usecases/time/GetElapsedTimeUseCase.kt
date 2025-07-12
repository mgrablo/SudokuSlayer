package com.example.domain.game.usecases.time

import com.example.domain.game.usecases.game.GetGameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetElapsedTimeUseCase(private val getGameUseCase: GetGameUseCase) {
	operator fun invoke(): Flow<Long> = getGameUseCase().map { game ->
		game.elapsedTime
	}
}
