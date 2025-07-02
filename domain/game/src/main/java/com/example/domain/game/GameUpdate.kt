package com.example.domain.game

import com.example.domain.core.CellChange
import com.example.sudoku.model.SudokuGrid

/**
 * Represents the result of an operation that modifies the game state.
 *
 * @property resultingGrid The new state of the Sudoku grid after the operation.
 * @property changes A list of the specific, reversible operations that were performed.
 */
data class GameUpdate(val resultingGrid: SudokuGrid, val changes: List<CellChange>)
