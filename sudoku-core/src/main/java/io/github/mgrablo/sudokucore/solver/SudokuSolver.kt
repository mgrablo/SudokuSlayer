package io.github.mgrablo.sudokucore.solver

import io.github.mgrablo.sudokucore.dlxalgorithm.DancingLinksMatrix
import io.github.mgrablo.sudokucore.dlxalgorithm.SudokuExactCoverMatrix
import io.github.mgrablo.sudokucore.dlxalgorithm.toDancingLinksMatrix
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import kotlin.math.sqrt

interface SudokuSolver {
	fun checkRow(row: IntArray): Boolean

	fun checkColumn(col: IntArray): Boolean

	fun checkSubgrid(subgrid: IntArray): Boolean

	fun isValidMove(sudoku: SudokuGrid, rowNum: Int, colNum: Int, num: Int): Boolean

	fun checkGrid(sudokuGrid: SudokuGrid): Boolean

	fun isValidSolution(sudokuGrid: SudokuGrid): Boolean

	suspend fun hasUniqueSolution(sudokuGrid: SudokuGrid): Boolean

	suspend fun fillGrid(sudokuGrid: SudokuGrid): SudokuGrid?

	suspend fun solve(sudokuGrid: SudokuGrid): SudokuGrid?
}

fun Collection<Int>.toSudokuGrid(inputGrid: SudokuGrid = SudokuGrid()): SudokuGrid {
	if (isEmpty()) {
		return inputGrid
	}
	var resultGrid = inputGrid
	val gridSize = resultGrid.gridSize
	for (pos in this) {
		val row = pos / (gridSize * gridSize)
		val col = (pos % (gridSize * gridSize)) / gridSize
		val num = pos % gridSize + 1
		if (inputGrid.getCellAt(row, col).number == 0) {
			resultGrid = resultGrid.withValue(row, col, num)
		}
	}

	return resultGrid
}

fun DancingLinksMatrix.Companion.fromSudoku(sudokuGrid: SudokuGrid): DancingLinksMatrix {
	// Convert existing numbers in the Sudoku grid to constraints
	val filledCells = getFilledCells(sudokuGrid.data)
	val exactCoverMatrix =
		SudokuExactCoverMatrix.create(sudokuGrid.gridSize, sudokuGrid.subgridSize).apply {
			coverAll(filledCells)
		}
	val dancingLinksMatrix = exactCoverMatrix.toDancingLinksMatrix()
	return dancingLinksMatrix
}

fun getFilledCells(sudokuGrid: Collection<SudokuCellData>): List<Triple<Int, Int, Int>> {
	val filledCells = mutableListOf<Triple<Int, Int, Int>>()
	for (cell in sudokuGrid) {
		val (row, col, num) = cell
		if (num != 0) { // If the cell is already filled
			filledCells.add(Triple(row, col, num))
		}
	}
	return filledCells
}

fun createConstraints(row: Int, col: Int, num: Int, gridSize: Int): List<Int> {
	val subgridSize = sqrt(gridSize.toDouble()).toInt()

	fun getBoxIndex(row: Int, col: Int): Int = (row / subgridSize) * subgridSize + (col / subgridSize)

	fun getCellConstraintIndex(row: Int, col: Int, num: Int): Int = row * gridSize + col

	fun getRowConstraintIndex(row: Int, num: Int): Int = gridSize * gridSize + row * gridSize + num

	fun getColConstraintIndex(col: Int, num: Int): Int = 2 * gridSize * gridSize + col * gridSize + num

	fun getBoxConstraintIndex(box: Int, num: Int): Int = 3 * gridSize * gridSize + box * gridSize + num

	val cellConstraint = getCellConstraintIndex(row, col, num)
	val rowConstraint = getRowConstraintIndex(row, num)
	val colConstraint = getColConstraintIndex(col, num)
	val boxConstraint = getBoxConstraintIndex(getBoxIndex(row, col), num)
	return listOf(cellConstraint, rowConstraint, colConstraint, boxConstraint)
}
