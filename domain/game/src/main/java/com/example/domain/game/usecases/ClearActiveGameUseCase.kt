package com.example.domain.game.usecases

import com.example.domain.core.GameRepository

class ClearActiveGameUseCase(private val gameRepository: GameRepository) {
	suspend operator fun invoke() {
		gameRepository.clearActiveGame()
	}
}
