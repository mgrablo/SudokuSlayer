package com.example.sudoku.generator

import com.example.sudoku.model.SudokuGrid

interface SudokuGenerator {
	suspend fun createSudoku(
		cellsToRemove: Int,
		seed: Long,
	): SudokuGrid

	suspend fun generateFullSudokuGrid(seed: Long): SudokuGrid

	suspend fun removeNumbers(
		grid: SudokuGrid,
		cellsToRemove: Int,
	): SudokuGrid

	suspend fun randomize(grid: SudokuGrid): SudokuGrid
}
