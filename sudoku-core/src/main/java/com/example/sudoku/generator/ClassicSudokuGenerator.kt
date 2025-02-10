package com.example.sudoku.generator

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.SudokuGrid.Companion.withSeed
import com.example.sudoku.solver.ClassicSudokuSolver
import kotlin.math.sqrt

class ClassicSudokuGenerator(private val gridSize: Int = 9) : SudokuGenerator {
	private val subgridSize = sqrt(gridSize.toDouble()).toInt()

	override suspend fun createSudoku(cellsToRemove: Int, seed: Long): SudokuGrid {
		val fullGrid = generateFullSudokuGrid(seed)
		return removeNumbers(fullGrid, cellsToRemove)
	}

	override suspend fun generateFullSudokuGrid(seed: Long): SudokuGrid {
		val sudoku = SudokuGrid(gridSize).withSeed(seed)
		ClassicSudokuSolver.fillGrid(sudoku)
		return sudoku
	}

	override suspend fun removeNumbers(
		sudoku: SudokuGrid,
		cellsToRemove: Int
	): SudokuGrid {
		val removedGrid = sudoku.clone()
		val totalCells = gridSize * gridSize
		var removedCount = 0

		val cellIndices = (0 until totalCells).shuffled(sudoku.random)
		for (index in cellIndices) {
			if (removedCount >= cellsToRemove) break

			val row = index / gridSize
			val col = index % gridSize
			val originalValue = removedGrid[row, col].number

			removedGrid[row, col] = 0

			if (!ClassicSudokuSolver.hasUniqueSolution(removedGrid)) {
				removedGrid[row, col] = originalValue
			} else {
				removedCount++
			}
		}

		return removedGrid
	}
}