package com.example.domain.statistics

import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
	suspend fun saveGame(finishedGame: FinishedGame)
	fun getAllGamesFlow(): Flow<List<FinishedGame>>
	suspend fun getAllGames(): List<FinishedGame>
	suspend fun getGameById(id: String): FinishedGame?
	suspend fun deleteGame(id: String)
	suspend fun getGamesByDifficulty(gameDifficulty: GameDifficulty): List<FinishedGame>
	suspend fun getGamesByGridSize(gridSize: SudokuGridSize): List<FinishedGame>
	suspend fun getTotalGamesFinished(): Int
	suspend fun getTotalTimeSpent(): Long
	suspend fun clearAll()
}
