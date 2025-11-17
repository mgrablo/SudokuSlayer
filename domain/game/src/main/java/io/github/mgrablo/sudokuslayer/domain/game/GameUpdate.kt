package io.github.mgrablo.sudokuslayer.domain.game

import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokuslayer.domain.core.CellChange

/**
 * Represents the result of an operation that modifies the game state.
 *
 * @property resultingGrid The new state of the Sudoku grid after the operation.
 * @property changes A list of the specific, reversible operations that were performed.
 */
data class GameUpdate(val resultingGrid: SudokuGrid, val changes: List<CellChange>)
