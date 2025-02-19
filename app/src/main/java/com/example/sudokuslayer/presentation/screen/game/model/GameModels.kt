package com.example.sudokuslayer.presentation.screen.game.model

import androidx.compose.runtime.Stable
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
	val inputMode: InputMode = InputMode.NUMBER,
	val difficulty: SudokuDifficulty = SudokuDifficulty.EASY,
	val lastHint: Hint? = null,
	val hintLogs: PersistentList<HintLog> = persistentListOf(),
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

enum class InputMode {
	NUMBER,
	NOTE,
	COLOR,
}

@Stable
data class HintLog(
	val hint: Hint,
	val isUserGuessed: Boolean,
	val isRevealed: Boolean,
	val explanation: PersistentList<String>,
)
