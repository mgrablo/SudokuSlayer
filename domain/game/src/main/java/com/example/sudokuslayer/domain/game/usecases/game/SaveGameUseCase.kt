package com.example.sudokuslayer.domain.game.usecases.game

import com.example.sudokuslayer.domain.core.Game
import com.example.sudokuslayer.domain.core.GameRepository

class SaveGameUseCase(private val gameRepository: GameRepository) {
	suspend operator fun invoke(game: Game) {
		gameRepository.saveGame(game)
	}
}
