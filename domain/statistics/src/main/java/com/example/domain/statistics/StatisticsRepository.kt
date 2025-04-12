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
	suspend fun getTotalGameResults(): Long
	suspend fun getTotalTimeSpent(): Long
	suspend fun clearAll()
}
