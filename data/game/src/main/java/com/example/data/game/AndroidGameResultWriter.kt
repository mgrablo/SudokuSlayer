package com.example.data.game

import com.example.data.core.AppDatabase
import com.example.domain.core.GameResult
import com.example.domain.game.repositories.GameResultWriter

class AndroidGameResultWriter(private val database: AppDatabase) : GameResultWriter {
	override suspend fun saveGameResult(gameResult: GameResult) {
		database.gameResultQueries
			.insertGameResult(
				id = gameResult.id,
				timeInSeconds = gameResult.timeInSeconds,
				difficulty = gameResult.difficulty,
				gridSize = gameResult.gridSize,
				hintsUsed = gameResult.hintsUsed,
				completedAt = gameResult.completedAt,
			)
	}
}
