package com.example.domain.creator

import com.example.domain.core.Game
import com.example.domain.core.GameRepository
import kotlinx.coroutines.flow.Flow

class GetSavedGameUseCase(private val gameRepository: GameRepository) {
	operator fun invoke(): Flow<Game> = gameRepository.getGame()
}
