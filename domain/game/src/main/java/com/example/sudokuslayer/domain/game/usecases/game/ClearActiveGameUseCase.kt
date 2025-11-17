package com.example.sudokuslayer.domain.game.usecases.game

import com.example.sudokuslayer.domain.core.GameRepository

class ClearActiveGameUseCase(private val gameRepository: GameRepository) {
	suspend operator fun invoke() {
		gameRepository.clearActiveGame()
	}
}
