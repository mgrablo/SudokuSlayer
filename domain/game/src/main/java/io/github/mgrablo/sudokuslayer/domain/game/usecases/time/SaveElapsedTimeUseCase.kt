package io.github.mgrablo.sudokuslayer.domain.game.usecases.time

import io.github.mgrablo.sudokuslayer.domain.core.GameRepository

class SaveElapsedTimeUseCase(private val gameRepository: GameRepository) {
	suspend operator fun invoke(elapsedTime: Long) {
		gameRepository.updateElapsedTime(elapsedTime)
	}
}
