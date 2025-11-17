package com.example.sudokuslayer.domain.game.usecases.time

import com.example.sudokuslayer.domain.core.GameDifficulty
import com.example.sudokuslayer.domain.core.SudokuGridSize
import com.example.sudokuslayer.domain.statistics.StatisticsRepository

class GetBestTimeUseCase(private val statisticsRepository: StatisticsRepository) {
	suspend operator fun invoke(difficulty: GameDifficulty, sudokuGridSize: SudokuGridSize): Long? =
		statisticsRepository.getBestTime(
			gameDifficulty = difficulty,
			gridSize = sudokuGridSize,
		)
}
