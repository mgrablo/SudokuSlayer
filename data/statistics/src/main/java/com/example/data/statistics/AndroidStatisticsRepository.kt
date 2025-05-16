package com.example.data.statistics

import com.example.data.core.AppDatabase
import com.example.data.core.GameResultEntity
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.domain.statistics.GameResultFilter
import com.example.domain.statistics.StatisticsRepository

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
		).executeAsList().map { it.toGameResult() }

	override suspend fun getTotalGameResults(): Long =
		database.gameResultQueries.getTotalGameResults().executeAsOne()

	override suspend fun getTotalTimeSpent(): Long = database.gameResultQueries
		.getTotalTimeSpent()
		.executeAsOneOrNull()?.SUM ?: 0L

	override suspend fun clearAll() {
		database.gameResultQueries.clearAll()
	}

	private fun GameResultEntity.toGameResult(): GameResult = GameResult(
		id = this.id,
		timeInSeconds = this.timeInSeconds,
		difficulty = this.difficulty,
		gridSize = this.gridSize,
		hintsUsed = this.hintsUsed,
		completionDate = this.completionDate,
	)
}
