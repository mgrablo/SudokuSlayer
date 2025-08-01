package com.example.sudoku.generator

import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.updateCells
import com.example.sudoku.solver.ClassicSudokuSolver
import kotlinx.collections.immutable.plus
import kotlin.math.sqrt
import kotlin.random.Random

class ClassicSudokuGenerator(private val gridSize: Int = 9) : SudokuGenerator {
	private val subgridSize = sqrt(gridSize.toDouble()).toInt()

	override suspend fun createSudoku(cellsToRemove: Int, seed: Long): SudokuGrid {
		val fullGrid = generateFullSudokuGrid(seed)
		val randomized = randomize(fullGrid)
		val removedNumbers = removeNumbers(randomized, cellsToRemove)
		val lockedNumbers = removedNumbers.updateCells(
			{ it.number != 0 },
			{ it.copy(attributes = it.attributes + CellAttributes.GENERATED) },
		)
		return lockedNumbers
	}

	override suspend fun generateFullSudokuGrid(seed: Long): SudokuGrid {
		var sudoku = SudokuGrid(gridSize).withSeed(seed)
		sudoku = ClassicSudokuSolver.fillGrid(sudoku) ?: sudoku
		return sudoku
	}

	override suspend fun removeNumbers(grid: SudokuGrid, cellsToRemove: Int): SudokuGrid {
		var removedGrid = grid
		val totalCells = gridSize * gridSize
		var removedCount = 0

		val cellIndices = (0 until totalCells)
			.shuffled(grid.random)
		val batchSize = if (gridSize > 9) 4 else 1
		var i = 0
		while (i < cellIndices.size && removedCount < cellsToRemove) {
			val batch = mutableListOf<Pair<Int, Int>>()
			var tempGrid = removedGrid

			var j = 0
			while (j < batchSize && i + j < cellIndices.size && removedCount + batch.size < cellsToRemove) {
				val index = cellIndices[i + j]
				val row = index / gridSize
				val col = index % gridSize

				if (tempGrid.getCellAt(row, col).number != 0) {
					tempGrid = tempGrid.withValue(row, col, 0)
					batch.add(row to col)
				}
				j++
			}

			if (batch.isNotEmpty() && ClassicSudokuSolver.hasUniqueSolution(tempGrid)) {
				removedGrid = tempGrid
				removedCount += batch.size
			} else {
				for ((row, col) in batch) {
					val originalValue = removedGrid.getCellAt(row, col).number
					if (originalValue == 0) continue

					val newGrid = removedGrid.withValue(row, col, 0)
					if (ClassicSudokuSolver.hasUniqueSolution(newGrid)) {
						removedGrid = newGrid
						removedCount++
					}
				}
			}
			i += j
		}

		return removedGrid
	}

	override suspend fun randomize(grid: SudokuGrid): SudokuGrid {
		var randomized = grid
		val random = grid.random
		val operations = 10 + gridSize * 2

		repeat(operations) {
			when (random.nextInt(5)) {
				0 -> randomized = swapRowsWithinBlock(randomized, random)
				1 -> randomized = swapColumnsWithinBlock(randomized, random)
				2 -> randomized = swapRowsOfBlocks(randomized, random)
				3 -> randomized = swapColumnsOfBlocks(randomized, random)
				4 -> randomized = numberSubstitution(randomized, random)
			}
		}

		return randomized
	}

	// Swaps two rows within the same block
	private fun swapRowsWithinBlock(grid: SudokuGrid, random: Random): SudokuGrid {
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
	private fun swapColumnsWithinBlock(grid: SudokuGrid, random: Random): SudokuGrid {
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
	private fun swapRowsOfBlocks(grid: SudokuGrid, random: Random): SudokuGrid {
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
	private fun swapColumnsOfBlocks(grid: SudokuGrid, random: Random): SudokuGrid {
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

	private fun numberSubstitution(grid: SudokuGrid, random: Random): SudokuGrid {
		val num1 = random.nextInt(gridSize) + 1
		var num2 = random.nextInt(gridSize) + 1
		while (num1 == num2) {
			num2 = random.nextInt(gridSize) + 1
		}
		var result = grid
		for (row in 0 until gridSize) {
			for (col in 0 until gridSize) {
				val cell = result.getCellAt(row, col)
				when (cell.number) {
					num1 -> result = result.withValue(row, col, num2)
					num2 -> result = result.withValue(row, col, num1)
				}
			}
		}
		return result
	}

	private fun swapRows(grid: SudokuGrid, row1: Int, row2: Int): SudokuGrid {
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

	private fun swapColumns(grid: SudokuGrid, col1: Int, col2: Int): SudokuGrid {
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
