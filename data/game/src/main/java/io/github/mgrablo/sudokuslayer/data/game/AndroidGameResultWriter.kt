package io.github.mgrablo.sudokuslayer.data.game

import io.github.mgrablo.sudokuslayer.data.core.AppDatabase
import io.github.mgrablo.sudokuslayer.domain.core.GameResult

class AndroidGameResultWriter(private val database: AppDatabase) :
	io.github.mgrablo.sudokuslayer.domain.game.repositories.GameResultWriter {
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
