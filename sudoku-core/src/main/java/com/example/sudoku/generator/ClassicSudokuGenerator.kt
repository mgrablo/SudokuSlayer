package com.example.sudoku.generator

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.solver.ClassicSudokuSolver
import kotlin.math.sqrt
import kotlin.random.Random

class ClassicSudokuGenerator(private val gridSize: Int = 9) : SudokuGenerator {
	private val subgridSize = sqrt(gridSize.toDouble()).toInt()

	override suspend fun createSudoku(
		cellsToRemove: Int,
		seed: Long,
	): SudokuGrid {
		val fullGrid = generateFullSudokuGrid(seed)
		val randomized = randomize(fullGrid)
		return removeNumbers(randomized, cellsToRemove)
	}

	override suspend fun generateFullSudokuGrid(seed: Long): SudokuGrid {
		var sudoku = SudokuGrid(gridSize).withSeed(seed)
		sudoku = ClassicSudokuSolver.fillGrid(sudoku) ?: sudoku
		return sudoku
	}

	override suspend fun removeNumbers(
		sudoku: SudokuGrid,
		cellsToRemove: Int,
	): SudokuGrid {
		var removedGrid = sudoku
		val totalCells = gridSize * gridSize
		var removedCount = 0

		val cellIndices = (0 until totalCells).shuffled(sudoku.random)
		for (index in cellIndices) {
			if (removedCount >= cellsToRemove) break

			val row = index / gridSize
			val col = index % gridSize
			val originalValue = removedGrid.getCellAt(row, col).number

			removedGrid = removedGrid.withValue(row, col, 0)

			if (!ClassicSudokuSolver.hasUniqueSolution(removedGrid)) {
				removedGrid = removedGrid.withValue(row, col, originalValue)
			} else {
				removedCount++
			}
		}

		return removedGrid
	}

	override suspend fun randomize(grid: SudokuGrid): SudokuGrid {
		var randomized = grid
		val random = grid.random

		repeat(20) {
			when (random.nextInt(4)) {
				0 -> randomized = swapRowsWithinBlock(randomized, random)
				1 -> randomized = swapColumnsWithinBlock(randomized, random)
				2 -> randomized = swapRowsOfBlocks(randomized, random)
				3 -> randomized = swapColumnsOfBlocks(randomized, random)
			}
		}

		return randomized
	}

	// Swaps two rows within the same block
	private fun swapRowsWithinBlock(
		grid: SudokuGrid,
		random: Random,
	): SudokuGrid {
		val block = random.nextInt(subgridSize)
		val rowStart = block * subgridSize
		val row1 = rowStart + random.nextInt(subgridSize)
		var row2 = rowStart + random.nextInt(subgridSize)
		while (row1 == row2) {
			row2 = rowStart + random.nextInt(subgridSize)
		}
		return swapRows(grid, row1, row2)
	}

	// Swaps two Columns within the same block
	private fun swapColumnsWithinBlock(
		grid: SudokuGrid,
		random: Random,
	): SudokuGrid {
		val block = random.nextInt(subgridSize)
		val colStart = block * subgridSize
		val col1 = colStart + random.nextInt(subgridSize)
		var col2 = colStart + random.nextInt(subgridSize)
		while (col1 == col2) {
			col2 = colStart + random.nextInt(subgridSize)
		}
		return swapColumns(grid, col1, col2)
	}

	// Swap two entire rows of blocks
	private fun swapRowsOfBlocks(
		grid: SudokuGrid,
		random: Random,
	): SudokuGrid {
		var randomized = grid
		val block1 = random.nextInt(subgridSize)
		var block2 = random.nextInt(subgridSize)

		while (block1 == block2) {
			block2 = random.nextInt(subgridSize)
		}
		val rowStart1 = block1 * subgridSize
		val rowStart2 = block2 * subgridSize

		for (i in 0 until subgridSize) {
			randomized = swapRows(randomized, rowStart1 + i, rowStart2 + i)
		}

		return randomized
	}

	// Swap two entire columns of blocks
	private fun swapColumnsOfBlocks(
		grid: SudokuGrid,
		random: Random,
	): SudokuGrid {
		var randomized = grid
		val block1 = random.nextInt(subgridSize)
		var block2 = random.nextInt(subgridSize)

		while (block1 == block2) {
			block2 = random.nextInt(subgridSize)
		}
		val colStart1 = block1 * subgridSize
		val colStart2 = block2 * subgridSize

		for (i in 0 until subgridSize) {
			randomized = swapColumns(randomized, colStart1 + i, colStart2 + i)
		}

		return randomized
	}

	private fun swapRows(
		grid: SudokuGrid,
		row1: Int,
		row2: Int,
	): SudokuGrid {
		var swappedGrid = grid
		val cellsRow1 = grid.getRow(row1)
		val cellsRow2 = grid.getRow(row2)
		cellsRow1.forEachIndexed { id, cell ->
			swappedGrid = swappedGrid.withReplacedCell(cellsRow2[id].row, cellsRow2[id].col, cell)
		}
		cellsRow2.forEachIndexed { id, cell ->
			swappedGrid = swappedGrid.withReplacedCell(cellsRow1[id].row, cellsRow1[id].col, cell)
		}
		return swappedGrid
	}

	private fun swapColumns(
		grid: SudokuGrid,
		col1: Int,
		col2: Int,
	): SudokuGrid {
		var swappedGrid = grid
		val cellsCol1 = grid.getColumn(col1)
		val cellsCol2 = grid.getColumn(col2)
		cellsCol1.forEachIndexed { id, cell ->
			swappedGrid = swappedGrid.withReplacedCell(cellsCol2[id].row, cellsCol2[id].col, cell)
		}
		cellsCol2.forEachIndexed { id, cell ->
			swappedGrid = swappedGrid.withReplacedCell(cellsCol1[id].row, cellsCol1[id].col, cell)
		}
		return swappedGrid
	}
}
