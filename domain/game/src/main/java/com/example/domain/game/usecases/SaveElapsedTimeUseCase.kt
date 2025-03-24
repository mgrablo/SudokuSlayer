package com.example.domain.game.usecases

import com.example.domain.core.GameRepository

class SaveElapsedTimeUseCase(
	private val gameRepository: GameRepository,
) {
	suspend operator fun invoke(elapsedTime: Long) {
		gameRepository.updateElapsedTime(elapsedTime)
	}
}
