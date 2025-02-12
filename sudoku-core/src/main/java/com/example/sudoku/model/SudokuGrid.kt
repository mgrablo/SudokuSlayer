package com.example.sudoku.model

import androidx.annotation.Size
import kotlin.math.sqrt
import kotlin.random.Random

class SudokuGrid(
	val gridSize: Int = 9,
	private val data: Array<SudokuCellData> = createEmptyGrid(gridSize)
) {
	var seed: Long? = null
	var random: Random = Random.Default
	val subgridSize: Int = sqrt(gridSize.toDouble()).toInt()
	private val cellManager = CellManager(data, gridSize, subgridSize)

	operator fun get(row: Int, col: Int): SudokuCellData {
		requireValidIndex(row, col)
		return data[getIndex(row, col)]
	}

	operator fun set(row: Int, col: Int, value: Int) {
		requireValidIndex(row, col)
		val index = getIndex(row, col)
		data[index] = data[index].copy(
			number = value
		)
	}

	fun replaceCell(row: Int, col: Int, cellData: SudokuCellData) {
		val index = getIndex(row, col)
		data[index] = data[index].copy(
			number = cellData.number,
			cornerNotes = cellData.cornerNotes,
			candidates = cellData.candidates,
		)
	}

	fun getRow(row: Int): Array<SudokuCellData> {
		require(row in 0 until gridSize) { "Index out of bounds for row: $row and gridSize: $gridSize" }
		return data.filter { it.row == row }.toTypedArray()
	}

	fun getCol(col: Int): Array<SudokuCellData> {
		require(col in 0 until gridSize) { "Index out of bounds" }
		return data.filter { it.col == col }.toTypedArray()
	}

	fun getSubgrid(rowNum: Int, colNum: Int, subgridSize: Int = 3): Array<SudokuCellData> {
		requireValidIndex(rowNum, colNum)
		val startRow = (rowNum / subgridSize) * subgridSize
		val startCol = (colNum / subgridSize) * subgridSize
		return data.filter { it.row in startRow until startRow + subgridSize && it.col in startCol until startCol + subgridSize }
			.toTypedArray()
	}

	fun getEmptyCellsCount(): Int = data.count { it.number == 0 }

	fun getArray(): Array<SudokuCellData> = data

	fun clone(): SudokuGrid = SudokuGrid(gridSize, data.clone())

	override fun toString(): String = data.groupBy { it.row }
		.values.joinToString("\n") { row -> row.joinToString(" ") { it.number.toString() } }

	companion object {
		private fun createEmptyGrid(gridSize: Int = 9): Array<SudokuCellData> =
			Array(gridSize * gridSize) { index ->
				SudokuCellData(
					row = index / gridSize,
					col = index % gridSize,
					number = 0
				)
			}

		private fun intArrayToData(intArray: Array<IntArray>): Array<SudokuCellData> =
			intArray.flatMapIndexed { rowIndex, row ->
				row.mapIndexed { colIndex, value -> SudokuCellData(rowIndex, colIndex, value) }
			}.toTypedArray()

		fun fromIntArray(intArrayData: Array<IntArray>, gridSize: Int = 9): SudokuGrid {
			require(intArrayData.size == gridSize && intArrayData.all { it.size == gridSize })
			return SudokuGrid(
				gridSize = gridSize,
				data = intArrayToData(intArrayData)
			)
		}

		fun SudokuGrid.withSeed(seed: Long): SudokuGrid = this.apply {
			this.seed = seed
			this.random = Random(seed)
		}

		fun fromCellData(cells: Array<SudokuCellData>): SudokuGrid {
			val gridSize = sqrt(cells.size.toDouble()).toInt()
			require(gridSize * gridSize == cells.size) { "Grid size must be a perfect square" }
			require(sqrt(gridSize.toDouble()).toInt() * sqrt(gridSize.toDouble()).toInt() == gridSize) { "Grid size must be divisible into equal subgrids" }
			return SudokuGrid(gridSize, cells.clone())
		}

		fun fromStringArray(@Size(9) gridData: Array<String>): SudokuGrid =
			fromIntArray(gridData.map { row -> row.map { it.toString().toInt() }.toIntArray() }
				.toTypedArray())
	}

	// Delegating Cell Manager functionality
	fun addAttribute(row: Int, col: Int, attribute: CellAttributes) {
		requireValidIndex(row, col)
		cellManager.addAttribute(row, col, attribute)
	}

	fun removeAttribute(row: Int, col: Int, attribute: CellAttributes) {
		requireValidIndex(row, col)
		cellManager.removeAttribute(row, col, attribute)
	}

	fun addCornerNote(row: Int, col: Int, noteNumber: Int) {
		requireValidIndex(row, col)
		cellManager.addCornerNote(row, col, noteNumber)
	}

	fun removeCornerNote(row: Int, col: Int, noteNumber: Int) {
		requireValidIndex(row, col)
		cellManager.removeCornerNote(row, col, noteNumber)
	}

	fun clearCornerNotes(row: Int, col: Int) {
		requireValidIndex(row, col)
		cellManager.clearCornerNotes(row, col)
	}

	fun highlightMatchingCells(number: Int) {
		cellManager.highlightMatchingCells(number)
	}

	fun highlightRowAndColumn(row: Int, col: Int) {
		requireValidIndex(row, col)
		cellManager.highlightRowAndColumn(row, col)
	}

	fun clearNumberHighlight() {
		cellManager.clearNumberHighlight()
	}

	fun clearRowColumnHighlight() {
		cellManager.clearRowColumnHighlight()
	}

	fun lockGeneratedCells() = cellManager.lockGeneratedCells()

	fun fillNotes() = cellManager.fillNotes()

	fun resetGame() = cellManager.resetGame()

	fun clearNotes() = cellManager.clearNotes()

	fun markRuleBreakingCells() = cellManager.markRuleBreakingCells()

	fun clearRuleBreakingCells() = cellManager.clearRuleBreakingCells()

	// Utility functions
	private fun getIndex(row: Int, col: Int): Int = row * gridSize + col

	private fun requireValidIndex(row: Int, col: Int) {
		require(row in 0 until gridSize && col in 0 until gridSize) { "Index out of bounds: row=$row, col=$col" }
	}
}