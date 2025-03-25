package com.example.sudokuslayer.presentation.screen.game.model

import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.solver.Hint

data class SudokuGameUiState(
	val selectedCell: Pair<Int, Int>? = null,
	val gameState: GameState = GameState.PLAYING,
	val isInNoteMode: Boolean = false,
	val lastHint: Hint? = null,
	val isLeftHandMode: Boolean = false,
	val showActionButtonsOnTop: Boolean = false
)

data class SudokuMove(val previousCellData: SudokuCellData, val newCellData: SudokuCellData)

enum class GameState {
	LOADING,
	PLAYING,
	VICTORY
}
