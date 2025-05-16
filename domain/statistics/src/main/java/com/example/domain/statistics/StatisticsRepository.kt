package com.example.domain.statistics

import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize

interface StatisticsRepository {
	suspend fun getAllGameResults(): List<GameResult>
	suspend fun getGameResultById(id: String): GameResult?
	suspend fun deleteGameResult(id: String)
	suspend fun getGameResultsByDifficulty(gameDifficulty: GameDifficulty): List<GameResult>
	suspend fun getGameResultsByGridSize(gridSize: SudokuGridSize): List<GameResult>
	suspend fun getFilteredGameResults(filter: GameResultFilter): List<GameResult>
	suspend fun getTotalGameResults(): Long
	suspend fun getTotalTimeSpent(): Long
	suspend fun clearAll()
}

data class GameResultFilter(
	val difficulties: Set<GameDifficulty> = GameDifficulty.entries.toSet(),
	val gridSizes: Set<SudokuGridSize> = SudokuGridSize.entries.toSet(),
	val minCompletionTime: Long? = null,
	val maxCompletionTime: Long? = null,
	val minHintsUsed: Int? = null,
	val maxHintsUsed: Int? = null,
)
