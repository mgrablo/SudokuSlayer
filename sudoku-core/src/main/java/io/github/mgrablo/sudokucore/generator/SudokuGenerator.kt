package io.github.mgrablo.sudokucore.generator

import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.model.SudokuPuzzle

interface SudokuGenerator {
	suspend fun createSudoku(cellsToRemove: Int, seed: Long): SudokuPuzzle

	suspend fun generateFullSudokuGrid(seed: Long): SudokuGrid

	suspend fun removeNumbers(grid: SudokuGrid, cellsToRemove: Int): SudokuGrid

	suspend fun randomize(grid: SudokuGrid): SudokuGrid
}
