package com.example.domain.game.usecases.time

import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.domain.statistics.StatisticsRepository

class GetBestTimeUseCase(private val statisticsRepository: StatisticsRepository) {
	suspend operator fun invoke(difficulty: GameDifficulty, sudokuGridSize: SudokuGridSize): Long? =
		statisticsRepository.getBestTime(
			gameDifficulty = difficulty,
			gridSize = sudokuGridSize,
		)
}
