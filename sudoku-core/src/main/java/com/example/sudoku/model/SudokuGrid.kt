package com.example.sudoku.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.sqrt
import kotlin.random.Random

@Stable
data class SudokuGrid(
	val gridSize: Int = 9,
	val seed: Long? = null,
	val data: PersistentList<SudokuCellData> = createEmptyGrid(gridSize),
) {
	val random: Random = seed?.let { Random(it) } ?: Random.Default
	val subgridSize: Int = sqrt(gridSize.toDouble()).toInt()

	fun getCellAt(row: Int, col: Int): SudokuCellData {
		requireValidIndex(row, col)
		return data[getIndex(row, col)]
	}

	// Returns new instance with updated cell
	fun updateCell(row: Int, col: Int, update: (SudokuCellData) -> SudokuCellData): SudokuGrid {
		requireValidIndex(row, col)
		val newData = data.toMutableList()
		val index = getIndex(row, col)
		newData[index] = update(newData[index])
		return copy(data = newData.toPersistentList())
	}

	fun withValue(row: Int, col: Int, value: Int): SudokuGrid =
		updateCell(row, col) { it.copy(number = value) }

	fun withReplacedCell(row: Int, col: Int, cellData: SudokuCellData): SudokuGrid =
		updateCell(row, col) {
			it.copy(
				number = cellData.number,
				cornerNotes = cellData.cornerNotes,
				candidates = cellData.candidates,
				attributes = cellData.attributes,
			)
		}

	fun getRow(row: Int): Array<SudokuCellData> {
		require(row in 0 until gridSize) { "Index out of bounds for row: $row and gridSize: $gridSize" }
		return data.filter { it.row == row }.toTypedArray()
	}

	fun getColumn(col: Int): Array<SudokuCellData> {
		require(col in 0 until gridSize) { "Index out of bounds" }
		return data.filter { it.col == col }.toTypedArray()
	}

	fun getSubgrid(rowNum: Int, colNum: Int, subgridSize: Int = 3): Array<SudokuCellData> {
		requireValidIndex(rowNum, colNum)
		val startRow = (rowNum / subgridSize) * subgridSize
		val startCol = (colNum / subgridSize) * subgridSize
		return data
			.filter {
				it.row in startRow until startRow + subgridSize &&
					it.col in startCol until startCol + subgridSize
			}
			.toTypedArray()
	}

	fun getEmptyCellsCount(): Int = data.count { it.number == 0 }

	fun getArray(): List<SudokuCellData> = data

	override fun toString(): String = data
		.groupBy { it.row }
		.values
		.joinToString("\n") { row -> row.joinToString(" ") { it.number.toString() } }

	fun withSeed(seed: Long): SudokuGrid = this.run {
		this.copy(seed = seed)
	}

	companion object {
		private fun createEmptyGrid(gridSize: Int = 9): PersistentList<SudokuCellData> =
			List(gridSize * gridSize) { index ->
				SudokuCellData(
					row = index / gridSize,
					col = index % gridSize,
					number = 0,
				)
			}.toPersistentList()

		private fun intArrayToData(intArray: Collection<IntArray>): PersistentList<SudokuCellData> =
			intArray
				.flatMapIndexed { rowIndex, row ->
					row.mapIndexed { colIndex, value -> SudokuCellData(rowIndex, colIndex, value) }
				}.toPersistentList()

		fun fromIntArray(intArrayData: Collection<IntArray>, gridSize: Int = 9): SudokuGrid {
			require(intArrayData.size == gridSize)
			require(intArrayData.all { it.size == gridSize })
			return SudokuGrid(
				gridSize = gridSize,
				data = intArrayToData(intArrayData),
			)
		}

		fun fromCellData(cells: Collection<SudokuCellData>): SudokuGrid {
			val gridSize = sqrt(cells.size.toDouble()).toInt()
			require(gridSize * gridSize == cells.size) { "Grid size must be a perfect square" }
			require(sqrt(gridSize.toDouble()).toInt() * sqrt(gridSize.toDouble()).toInt() == gridSize) {
				"Grid size must be divisible into equal subgrids"
			}
			return SudokuGrid(
				gridSize = gridSize,
				data = cells.toPersistentList(),
			)
		}

		fun fromStringArray(gridData: Collection<String>): SudokuGrid {
			require(gridData.size in listOf(4, 9, 16)) { "Grid size should be one of 4, 9, or 16" }
			return fromIntArray(
				gridData
					.map { row -> row.map { it.toString().toInt() }.toIntArray() }
					.toPersistentList(),
			)
		}
	}

	// Utility functions
	private fun getIndex(row: Int, col: Int): Int = row * gridSize + col

	private fun requireValidIndex(row: Int, col: Int) {
		require(row in 0 until gridSize && col in 0 until gridSize) {
			"Index out of bounds: row=$row, col=$col for grid size $gridSize"
		}
	}
}
