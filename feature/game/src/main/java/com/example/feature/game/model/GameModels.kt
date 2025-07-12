package com.example.feature.game.model

import com.example.sudoku.solver.Hint
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

internal data class SudokuGameUiState(
	val selectedCell: Pair<Int, Int>? = null,
	val gameState: GameState = GameState.PLAYING,
	val isInNoteMode: Boolean = false,
	val lastHint: Hint? = null,
	val focusedCells: PersistentSet<Pair<Int, Int>> = persistentSetOf(),
	val isLeftHandMode: Boolean = false,
	val showActionButtonsOnTop: Boolean = false,
	val currentBestTime: Long? = null,
	val isNewBestTime: Boolean = false,
	val autoClearNotes: Boolean = true,
	val timerVisible: Boolean = true,
)

internal enum class GameState {
	LOADING,
	PLAYING,
	VICTORY,
}
