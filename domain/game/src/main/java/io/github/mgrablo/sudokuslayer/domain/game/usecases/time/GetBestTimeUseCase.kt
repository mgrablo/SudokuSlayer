package io.github.mgrablo.sudokuslayer.domain.game.usecases.time

import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.domain.statistics.StatisticsRepository

class GetBestTimeUseCase(private val statisticsRepository: StatisticsRepository) {
	suspend operator fun invoke(difficulty: GameDifficulty, sudokuGridSize: SudokuGridSize): Long? =
		statisticsRepository.getBestTime(
			gameDifficulty = difficulty,
			gridSize = sudokuGridSize,
		)
}
