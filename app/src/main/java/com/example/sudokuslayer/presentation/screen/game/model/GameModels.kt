package com.example.sudokuslayer.presentation.screen.game.model

import com.example.data.game.models.HintLog
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.solver.Hint
import com.example.sudokuslayer.presentation.screen.sudokucreator.SudokuDifficulty
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class SudokuGameUiState(
	val sudoku: SudokuGrid = SudokuGrid(),
	val selectedCell: Pair<Int, Int>? = null,
	val gameState: GameState = GameState.PLAYING,
	val isInNoteMode: Boolean = false,
	val difficulty: SudokuDifficulty = SudokuDifficulty.EASY,
	val lastHint: Hint? = null,
	val hintLogs: PersistentList<HintLog> = persistentListOf(),
	val isLeftHandMode: Boolean = false,
	val showActionButtonsOnTop: Boolean = false,
)

data class SudokuMove(
	val previousCellData: SudokuCellData,
	val newCellData: SudokuCellData,
)

enum class GameState {
	LOADING,
	PLAYING,
	VICTORY,
}
