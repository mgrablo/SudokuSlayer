package com.example.sudokuslayer.domain.game

import com.example.sudoku.model.SudokuGrid
import com.example.sudokuslayer.domain.core.CellChange

/**
 * Represents the result of an operation that modifies the game state.
 *
 * @property resultingGrid The new state of the Sudoku grid after the operation.
 * @property changes A list of the specific, reversible operations that were performed.
 */
data class GameUpdate(val resultingGrid: SudokuGrid, val changes: List<CellChange>)
