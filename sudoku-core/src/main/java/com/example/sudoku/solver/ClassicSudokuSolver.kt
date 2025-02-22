package com.example.sudoku.solver

import com.example.sudoku.dlxalgorithm.DLXAlgorithm.solveSuspend
import com.example.sudoku.dlxalgorithm.DancingLinksMatrix
import com.example.sudoku.model.SudokuGrid
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.take

object ClassicSudokuSolver : SudokuSolver {
	// Bitwise mask for numbers 1 to gridSize
	private fun getValidMask(gridSize: Int): Long = (1L shl (gridSize + 1)) - 2L

	private fun numbersToMask(numbers: Iterable<Int>): Long = numbers.fold(0L) { acc, num -> acc or (1L shl num) }

	override fun checkRow(row: IntArray): Boolean {
		var mask = 0
		row.forEach { num ->
			if (num != 0) {
				if (mask and (1 shl num) != 0) return false
				mask = mask or (1 shl num)
			}
		}
		return true
	}

	override fun checkColumn(col: IntArray): Boolean = checkRow(col)

	override fun checkSubgrid(subgrid: IntArray): Boolean = checkRow(subgrid)

	override fun isValidMove(
		sudoku: SudokuGrid,
		rowNum: Int,
		colNum: Int,
		num: Int,
	): Boolean {
		val gridSize = sudoku.gridSize
		val subgridSize = sudoku.subgridSize
		val rowMask = numbersToMask(sudoku.getRow(rowNum).map { it.number })
		if (rowMask and (1L shl num) != 0L) return false

		val colMask = numbersToMask(sudoku.getColumn(colNum).map { it.number })
		if (colMask and (1L shl num) != 0L) return false

		val subgridMask =
			numbersToMask(
				sudoku.getSubgrid(rowNum, colNum, subgridSize).map { it.number },
			)
		return subgridMask and (1L shl num) == 0L
	}

	override fun checkGrid(sudoku: SudokuGrid): Boolean {
		// Check rows and columns
		for (i in 0 until sudoku.gridSize) {
			if (!checkRow(sudoku.getRow(i).map { it.number }.toIntArray())) return false
			if (!checkColumn(sudoku.getColumn(i).map { it.number }.toIntArray())) return false
		}

		// Check subgrids
		val subgridSize = sudoku.subgridSize
		for (row in 0 until sudoku.gridSize step subgridSize) {
			for (col in 0 until sudoku.gridSize step subgridSize) {
				val subgrid = sudoku.getSubgrid(row, col, subgridSize).map { it.number }.toIntArray()
				if (!checkSubgrid(subgrid)) return false
			}
		}
		return true
	}

	override fun isValidSolution(sudoku: SudokuGrid): Boolean {
		val validGrid = checkGrid(sudoku)
		val noZeros = 0 !in sudoku.getArray().map { it.number }
		return validGrid && noZeros
	}

	private fun isSolvable(sudoku: SudokuGrid): Boolean {
		// First, check if current state is valid
		if (!checkGrid(sudoku)) return false

		// Check if any cell has no valid options
		val emptyCells = sudoku.getArray().filter { it.number == 0 }
		if (emptyCells.isEmpty()) return true

		// For each empty cell, check if it has at least one valid number
		for (cell in emptyCells) {
			val validMoves = getValidMoves(sudoku, cell.row, cell.col)
			if (validMoves.isEmpty()) return false
		}

		// Additional check: verify that each number can be placed somewhere in each row/column/subgrid
		for (num in 1..sudoku.gridSize) {
			// Check rows
			for (row in 0 until sudoku.gridSize) {
				if (!sudoku.getRow(row).any { it.number == num } &&
					(0 until sudoku.gridSize).none { col ->
						sudoku.getCellAt(row, col).number == 0 && isValidMove(sudoku, row, col, num)
					}
				) {
					return false
				}
			}

			// Check columns
			for (col in 0 until sudoku.gridSize) {
				if (!sudoku.getColumn(col).any { it.number == num } &&
					(0 until sudoku.gridSize).none { row ->
						sudoku.getCellAt(row, col).number == 0 && isValidMove(sudoku, row, col, num)
					}
				) {
					return false
				}
			}

			// Check subgrids
			val subgridSize = sudoku.subgridSize
			for (blockRow in 0 until sudoku.gridSize step subgridSize) {
				for (blockCol in 0 until sudoku.gridSize step subgridSize) {
					val subgrid = sudoku.getSubgrid(blockRow, blockCol, subgridSize)
					if (!subgrid.any { it.number == num } &&
						subgrid.none { cell ->
							cell.number == 0 && isValidMove(sudoku, cell.row, cell.col, num)
						}
					) {
						return false
					}
				}
			}
		}

		return true
	}

	override suspend fun fillGrid(sudoku: SudokuGrid): SudokuGrid? =
		coroutineScope {
			if (!isSolvable(sudoku)) {
				return@coroutineScope null
			}

			val result = solve(sudoku)
			return@coroutineScope result
		}

	override suspend fun solve(sudoku: SudokuGrid): SudokuGrid? =
		coroutineScope {
			val dlxMatrix = DancingLinksMatrix.fromSudoku(sudoku)
			val result = mutableListOf<Int>()
			dlxMatrix.rootNode.printNotEmptyNodes()

			val channel = solveSuspend(dlxMatrix.rootNode, 1)

			channel.consumeAsFlow()
				.take(1)
				.collect {
					result.addAll(it)
				}
			channel.cancel()

			if (result.isEmpty()) {
				null
			} else {
				result.toSudokuGrid(sudoku)
			}
		}

	fun getValidMoves(
		sudoku: SudokuGrid,
		row: Int,
		col: Int,
	): List<Int> {
		val rowMask = numbersToMask(sudoku.getRow(row).map { it.number })
		val colMask = numbersToMask(sudoku.getColumn(col).map { it.number })
		val subgridMask =
			numbersToMask(sudoku.getSubgrid(row, col, sudoku.subgridSize).map { it.number })

		val invalidMask = rowMask or colMask or subgridMask
		val validMask = getValidMask(sudoku.gridSize) and invalidMask.inv()
		return (1..sudoku.gridSize).filter { validMask and (1L shl it) != 0L }
	}

	override suspend fun hasUniqueSolution(sudoku: SudokuGrid): Boolean =
		coroutineScope {
			val dancingLinksMatrix = DancingLinksMatrix.fromSudoku(sudoku)
			val result = mutableListOf<List<Int>>()

			solveSuspend(dancingLinksMatrix.rootNode)
				.consumeAsFlow()
				.take(2)
				.collect { result.add(it) }

			result.size == 1
		}
}
