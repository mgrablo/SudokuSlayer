package com.example.domain.game.usecases.game

import com.example.domain.core.Game
import com.example.domain.core.GameRepository
import kotlinx.coroutines.flow.Flow

class GetGameUseCase(private val gameRepository: GameRepository) {
	operator fun invoke(): Flow<Game> = gameRepository.getGame()
}
