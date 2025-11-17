package io.github.mgrablo.sudokuslayer.domain.game.usecases.game

import io.github.mgrablo.sudokuslayer.domain.core.GameRepository

class ClearActiveGameUseCase(private val gameRepository: GameRepository) {
	suspend operator fun invoke() {
		gameRepository.clearActiveGame()
	}
}
