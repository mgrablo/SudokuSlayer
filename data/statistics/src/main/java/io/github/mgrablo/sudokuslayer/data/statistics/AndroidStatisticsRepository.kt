package io.github.mgrablo.sudokuslayer.data.statistics

import io.github.mgrablo.sudokuslayer.data.core.AppDatabase
import io.github.mgrablo.sudokuslayer.data.core.GameResultEntity
import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import io.github.mgrablo.sudokuslayer.domain.core.GameResult
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.domain.statistics.GameResultFilter
import io.github.mgrablo.sudokuslayer.domain.statistics.StatisticsRepository

class AndroidStatisticsRepository(private val database: AppDatabase) : StatisticsRepository {
	override suspend fun getAllGameResults(): List<GameResult> = database.gameResultQueries
		.getAllGameResults()
		.executeAsList()
		.map { it.toGameResult() }

	override suspend fun getGameResultById(id: String): GameResult? = database.gameResultQueries
		.getGameResultById(id)
		.executeAsOneOrNull()
		?.toGameResult()

	override suspend fun deleteGameResult(id: String) {
		database.gameResultQueries
			.deleteGameResult(id)
	}

	override suspend fun getGameResultsByDifficulty(gameDifficulty: GameDifficulty): List<GameResult> =
		database.gameResultQueries
			.getGameResultsByDifficulty(gameDifficulty)
			.executeAsList()
			.map { it.toGameResult() }

	override suspend fun getGameResultsByGridSize(gridSize: SudokuGridSize): List<GameResult> =
		database.gameResultQueries
			.getGameResultsByGridSize(gridSize)
			.executeAsList()
			.map { it.toGameResult() }

	override suspend fun getFilteredGameResults(filter: GameResultFilter): List<GameResult> =
		database.gameResultQueries.getFilteredGameResults(
			difficulties = filter.difficulties,
			gridSizes = filter.gridSizes,
			minCompletionTime = filter.minCompletionTime,
			maxCompletionTime = filter.maxCompletionTime,
			minHintsUsed = filter.minHintsUsed,
			maxHintsUsed = filter.maxHintsUsed,
			dateRangeStart = filter.dateRangeStart,
			dateRangeEnd = filter.dateRangeEnd,
		).executeAsList().map { it.toGameResult() }

	override suspend fun getTotalGameResults(): Long =
		database.gameResultQueries.getTotalGameResults().executeAsOne()

	override suspend fun getTotalTimeSpent(): Long = database.gameResultQueries
		.getTotalTimeSpent()
		.executeAsOneOrNull()?.SUM ?: 0L

	override suspend fun clearAll() {
		database.gameResultQueries.clearAll()
	}

	override suspend fun getBestTime(gameDifficulty: GameDifficulty, gridSize: SudokuGridSize): Long? =
		database.gameResultQueries.getBestTime(gameDifficulty, gridSize).executeAsOneOrNull()?.MIN

	private fun GameResultEntity.toGameResult(): GameResult = GameResult(
		id = this.id,
		timeInSeconds = this.timeInSeconds,
		difficulty = this.difficulty,
		gridSize = this.gridSize,
		hintsUsed = this.hintsUsed,
		completionDate = this.completionDate,
		seed = seed,
	)
}
