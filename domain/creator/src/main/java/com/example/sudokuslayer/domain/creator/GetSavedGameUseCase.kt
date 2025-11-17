package com.example.sudokuslayer.domain.creator

import com.example.sudokuslayer.domain.core.Game
import com.example.sudokuslayer.domain.core.GameRepository
import kotlinx.coroutines.flow.Flow

class GetSavedGameUseCase(private val gameRepository: GameRepository) {
	operator fun invoke(): Flow<Game> = gameRepository.getGame()
}
