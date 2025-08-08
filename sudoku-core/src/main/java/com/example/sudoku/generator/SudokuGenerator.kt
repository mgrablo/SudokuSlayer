package com.example.sudoku.generator

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.SudokuPuzzle

interface SudokuGenerator {
	suspend fun createSudoku(cellsToRemove: Int, seed: Long): SudokuPuzzle

	suspend fun generateFullSudokuGrid(seed: Long): SudokuGrid

	suspend fun removeNumbers(grid: SudokuGrid, cellsToRemove: Int): SudokuGrid

	suspend fun randomize(grid: SudokuGrid): SudokuGrid
}
