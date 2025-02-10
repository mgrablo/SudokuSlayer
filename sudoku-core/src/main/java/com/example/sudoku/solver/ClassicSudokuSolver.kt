package com.example.sudoku.solver

import com.example.sudoku.dlxalgorithm.DLXAlgorithm.solveSuspend
import com.example.sudoku.dlxalgorithm.DancingLinksMatrix
import com.example.sudoku.model.SudokuGrid
import kotlinx.coroutines.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.take
import kotlin.random.Random

object ClassicSudokuSolver : SudokuSolver {
	var random: Random = Random.Default

	// Bitwise mask for numbers 1 to gridSize
	private fun getValidMask(gridSize: Int): Long = (1L shl (gridSize + 1)) - 2L

	private fun numbersToMask(numbers: Iterable<Int>): Long =
		numbers.fold(0L) { acc, num -> acc or (1L shl num) }

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
		num: Int
	): Boolean {
		val gridSize = sudoku.gridSize
		val subgridSize = sudoku.subgridSize
		val rowMask = numbersToMask(sudoku.getRow(rowNum).map { it.number })
		if (rowMask and (1L shl num) != 0L) return false

		val colMask = numbersToMask(sudoku.getCol(colNum).map { it.number })
		if (colMask and (1L shl num) != 0L) return false

		val subgridMask = numbersToMask(
			sudoku.getSubgrid(rowNum, colNum, subgridSize).map { it.number },
		)
		return subgridMask and (1L shl num) == 0L
	}

	override fun checkGrid(sudoku: SudokuGrid): Boolean {
		// Check rows and columns
		for (i in 0 until sudoku.gridSize) {
			if (!checkRow(sudoku.getRow(i).map { it.number }.toIntArray())) return false
			if (!checkColumn(sudoku.getCol(i).map { it.number }.toIntArray())) return false
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
						sudoku[row, col].number == 0 && isValidMove(sudoku, row, col, num)
					}) {
					return false
				}
			}
			
			// Check columns
			for (col in 0 until sudoku.gridSize) {
				if (!sudoku.getCol(col).any { it.number == num } && 
					(0 until sudoku.gridSize).none { row -> 
						sudoku[row, col].number == 0 && isValidMove(sudoku, row, col, num)
					}) {
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
						}) {
						return false
					}
				}
			}
		}

		return true
	}

	private val moveCache = mutableMapOf<Triple<Int, Int, Int>, List<Int>>()

	private fun getValidMovesWithCache(sudoku: SudokuGrid, row: Int, col: Int): List<Int> {
		val key = Triple(row, col, sudoku.hashCode())
		return moveCache.getOrPut(key) {
			getValidMoves(sudoku, row, col)
		}
	}

	private fun fillGridWithoutCheck(sudoku: SudokuGrid): Boolean {
		val bestCell = findBestCell(sudoku) ?: return true
		val (row, col) = bestCell

		val validMoves = getValidMovesWithCache(sudoku, row, col)
		if (validMoves.isEmpty()) return false

		for (num in validMoves.shuffled(sudoku.random)) {
			sudoku[row, col] = num
			if (fillGridWithoutCheck(sudoku)) {
				moveCache.clear() // Clear cache after successful fill
				return true
			}
			sudoku[row, col] = 0
		}

		return false
	}

	// Helper extension function
	private fun SudokuGrid.copyFrom(other: SudokuGrid) {
		other.getArray().forEach { cell ->
			this[cell.row, cell.col] = cell.number
		}
	}

	override suspend fun fillGrid(sudoku: SudokuGrid): Boolean = coroutineScope {
		if (!isSolvable(sudoku))
			return@coroutineScope false

		val result = solve(sudoku)
		moveCache.clear()
		return@coroutineScope result
	}


	suspend fun solve(sudoku: SudokuGrid): Boolean = coroutineScope {
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

		if (result.isEmpty()) false

		result.toSudokuGrid(sudoku).getArray().forEach { cell ->
			sudoku[cell.row, cell.col] = cell.number
		}
		true
	}

	private fun getValidMoves(sudoku: SudokuGrid, row: Int, col: Int): List<Int> {
		val rowMask = numbersToMask(sudoku.getRow(row).map { it.number })
		val colMask = numbersToMask(sudoku.getCol(col).map { it.number })
		val subgridMask =
			numbersToMask(sudoku.getSubgrid(row, col, sudoku.subgridSize).map { it.number })

		val invalidMask = rowMask or colMask or subgridMask
		val validMask = getValidMask(sudoku.gridSize) and invalidMask.inv()
		return (1..sudoku.gridSize).filter { validMask and (1L shl it) != 0L }
	}

	override suspend fun hasUniqueSolution(sudoku: SudokuGrid): Boolean = coroutineScope {
		val dancingLinksMatrix = DancingLinksMatrix.fromSudoku(sudoku)
		val result = mutableListOf<List<Int>>()

		solveSuspend(dancingLinksMatrix.rootNode)
			.consumeAsFlow()
			.take(2)
			.collect { result.add(it) }

		result.size == 1
	}

	fun findBestCell(sudoku: SudokuGrid): Pair<Int, Int>? {
		var bestRow = -1
		var bestCol = -1
		var minPossibilities = Int.MAX_VALUE
		val gridSize = sudoku.gridSize - 1

		for (row in 0..gridSize) {
			for (col in 0..gridSize) {
				if (sudoku[row, col].number == 0) {
					val possibilities = getValidMoves(sudoku, row, col).size
					if (possibilities < minPossibilities) {
						minPossibilities = possibilities
						bestRow = row
						bestCol = col
						if (possibilities == 1) return bestRow to bestCol // Early termination
					}
				}
			}
		}
		return if (bestRow != -1 && bestCol != -1) bestRow to bestCol else null
	}
}

private suspend fun List<Deferred<Boolean>>.awaitAny(): Boolean = coroutineScope {
	val results = this@awaitAny.map { deferred ->
		async {
			try {
				deferred.await()
			} catch (e: Exception) {
				false
			}
		}
	}
	results.awaitAll().any { it }
}