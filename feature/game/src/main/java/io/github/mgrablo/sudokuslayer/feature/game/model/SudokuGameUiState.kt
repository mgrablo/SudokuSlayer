package io.github.mgrablo.sudokuslayer.feature.game.model

import io.github.mgrablo.sudokucore.solver.Hint
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

internal data class SudokuGameUiState(
	val sudokuGridSize: SudokuGridSize = SudokuGridSize.NINE,
	val selectedCell: Pair<Int, Int>? = null,
	val gameState: GameState = GameState.LOADING,
	val isInNoteMode: Boolean = false,
	val lastHint: Hint? = null,
	val focusedCells: PersistentSet<Pair<Int, Int>> = persistentSetOf(),
	val isLeftHandMode: Boolean = false,
	val showActionButtonsOnTop: Boolean = false,
	val currentBestTime: Long? = null,
	val isNewBestTime: Boolean = false,
	val autoClearNotes: Boolean = true,
	val timerVisible: Boolean = true,
	val foundMistakes: PersistentList<Pair<Int, Int>> = persistentListOf(),
	val snackbarState: SnackbarState? = null,
)

internal enum class GameState {
	LOADING,
	PLAYING,
	VICTORY,
}

internal sealed interface SnackbarState {
	data class FoundMistakes(val count: Int) : SnackbarState
	data object NoMistakesFound : SnackbarState
}
