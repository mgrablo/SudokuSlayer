package com.example.domain.game.repositories

import com.example.domain.core.GameDifficulty
import com.example.domain.game.models.Game
import com.example.domain.game.models.HintLog
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import kotlinx.coroutines.flow.Flow

interface GameRepository {
	fun getGame(): Flow<Game>

	suspend fun saveGame(game: Game)

	suspend fun updateGrid(sudokuGrid: SudokuGrid)

	suspend fun updateElapsedTime(elapsedTime: Long)

	suspend fun updateHintsUsed(hintsUsed: Int)

	suspend fun updateCell(
		row: Int,
		column: Int,
		cellData: SudokuCellData,
	)

	suspend fun updateGameDifficulty(gameDifficulty: GameDifficulty)

	suspend fun addHintLog(hintLog: HintLog)

	suspend fun updateHintLog(hintLog: HintLog)
}
