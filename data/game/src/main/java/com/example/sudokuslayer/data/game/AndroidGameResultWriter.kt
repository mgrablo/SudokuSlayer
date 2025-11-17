package com.example.sudokuslayer.data.game

import com.example.data.core.AppDatabase
import com.example.sudokuslayer.domain.core.GameResult

class AndroidGameResultWriter(private val database: AppDatabase) :
	com.example.sudokuslayer.domain.game.repositories.GameResultWriter {
	override suspend fun saveGameResult(gameResult: GameResult) {
		database.gameResultQueries
			.insertGameResult(
				id = gameResult.id,
				timeInSeconds = gameResult.timeInSeconds,
				difficulty = gameResult.difficulty,
				gridSize = gameResult.gridSize,
				hintsUsed = gameResult.hintsUsed,
				completionDate = gameResult.completionDate,
				seed = gameResult.seed,
			)
	}
}
