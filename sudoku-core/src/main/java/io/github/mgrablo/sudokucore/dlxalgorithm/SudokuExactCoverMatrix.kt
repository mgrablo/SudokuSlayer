package io.github.mgrablo.sudokucore.dlxalgorithm

class SudokuExactCoverMatrix private constructor(val gridSize: Int, val subgridSize: Int) :
	ExactCoverMatrix {
	override val totalOptions = gridSize * gridSize * gridSize
	override val totalConstraints = gridSize * gridSize * 4
	private val exactCoverMatrix = BaseExactCoverMatrix(totalOptions, totalConstraints)
	override val matrix get() = exactCoverMatrix.matrix

	fun coverAll(sudokuGrid: Collection<Triple<Int, Int, Int>>) {
		sudokuGrid.forEach { (row, col, num) ->
			cover(row, col, num)
		}
	}

	fun cover(row: Int, col: Int, num: Int): Array<BooleanArray> {
		val rowIndex = cellToRowIndex(row, col, num)
		val matrixRow = exactCoverMatrix.getMatrixRow(rowIndex)
		matrixRow.forEachIndexed { col, filled ->
			if (filled) {
				exactCoverMatrix.cover(matrix, col, rowIndex)
			}
		}
		return exactCoverMatrix.matrix
	}

	private fun buildBase(): Array<BooleanArray> {
		var rowIndex = 0
		for (r in 0 until gridSize) {
			for (c in 0 until gridSize) {
				for (n in 0 until gridSize) {
					val constraints = createConstraints(r, c, n)
					exactCoverMatrix.fillMatrixRow(matrix[rowIndex], constraints)
					rowIndex++
				}
			}
		}
		return matrix
	}

	private fun cellToRowIndex(row: Int, col: Int, digit: Int): Int =
		row * (gridSize * gridSize) + col * gridSize + (digit - 1)

	private fun createConstraints(row: Int, col: Int, num: Int): List<Int> {
		val rowColConstraint = row * gridSize + col
		val rowNumConstraint = gridSize * gridSize + row * gridSize + num
		val colNumConstraint = 2 * gridSize * gridSize + col * gridSize + num
		val boxNumConstraint = 3 * gridSize * gridSize + getBoxIndex(row, col) * gridSize + num
		return listOf(rowColConstraint, rowNumConstraint, colNumConstraint, boxNumConstraint)
	}

	private fun getBoxIndex(row: Int, col: Int): Int =
		(row / subgridSize) * subgridSize + (col / subgridSize)

	companion object {
		fun createClassic(): SudokuExactCoverMatrix {
			val matrix = SudokuExactCoverMatrix(9, 3)
			matrix.buildBase()
			return matrix
		}

		fun create(gridSize: Int, subgridSize: Int): SudokuExactCoverMatrix {
			val matrix = SudokuExactCoverMatrix(gridSize, subgridSize)
			matrix.buildBase()
			return matrix
		}
	}
}
