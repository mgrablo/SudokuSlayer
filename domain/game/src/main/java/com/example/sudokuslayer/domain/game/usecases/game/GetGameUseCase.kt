package com.example.sudokuslayer.domain.game.usecases.game

import com.example.sudokuslayer.domain.core.Game
import com.example.sudokuslayer.domain.core.GameRepository
import kotlinx.coroutines.flow.Flow

class GetGameUseCase(private val gameRepository: GameRepository) {
	operator fun invoke(): Flow<Game> = gameRepository.getGame()
}
