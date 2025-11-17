package com.example.sudokuslayer.domain.game.usecases.time

import com.example.sudokuslayer.domain.core.GameRepository

class SaveElapsedTimeUseCase(private val gameRepository: GameRepository) {
	suspend operator fun invoke(elapsedTime: Long) {
		gameRepository.updateElapsedTime(elapsedTime)
	}
}
