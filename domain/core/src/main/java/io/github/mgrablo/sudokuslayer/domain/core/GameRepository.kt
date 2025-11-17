package io.github.mgrablo.sudokuslayer.domain.core

import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import kotlinx.coroutines.flow.Flow

interface GameRepository {
	fun getGame(): Flow<Game>

	fun hasActiveGame(): Flow<Boolean>

	suspend fun saveGame(game: Game)

	suspend fun clearActiveGame()

	suspend fun updateGrid(sudokuGrid: SudokuGrid)

	suspend fun updateElapsedTime(elapsedTime: Long)

	suspend fun updateHintsUsed(hintsUsed: Int)

	suspend fun updateCell(row: Int, column: Int, cellData: SudokuCellData)

	suspend fun updateGameDifficulty(gameDifficulty: GameDifficulty)

	suspend fun addHintLog(hintLog: HintLog)

	suspend fun updateHintLog(hintLog: HintLog)
}
