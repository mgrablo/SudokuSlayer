package com.example.feature.game.model

import com.example.sudoku.solver.Hint

internal data class SudokuGameUiState(
	val selectedCell: Pair<Int, Int>? = null,
	val gameState: GameState = GameState.PLAYING,
	val isInNoteMode: Boolean = false,
	val lastHint: Hint? = null,
	val isLeftHandMode: Boolean = false,
	val showActionButtonsOnTop: Boolean = false,
	val currentBestTime: Long? = null,
	val isNewBestTime: Boolean = false,
	val autoClearNotes: Boolean = true,
)

internal enum class GameState {
	LOADING,
	PLAYING,
	VICTORY,
}
