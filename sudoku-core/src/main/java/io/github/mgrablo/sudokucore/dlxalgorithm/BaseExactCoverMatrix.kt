package io.github.mgrablo.sudokucore.dlxalgorithm

import java.util.Arrays

interface ExactCoverMatrix {
	val totalOptions: Int
	val totalConstraints: Int
	val matrix: Array<BooleanArray>
}

class BaseExactCoverMatrix(override val totalOptions: Int, override val totalConstraints: Int) :
	ExactCoverMatrix {
	override val matrix = Array(totalOptions) { BooleanArray(totalConstraints) { false } }

	init {
		require(totalOptions > 0) { "totalOptions must be greater than 0" }
		require(totalConstraints > 0) { "totalConstraints must be greater than 0" }
	}

	fun getMatrixRow(row: Int) = matrix[row]

	fun fillMatrixRow(matrixRow: BooleanArray, constraints: List<Int>) {
		for (constraint in constraints) {
			matrixRow[constraint] = true
		}
	}

	fun cover(matrix: Array<BooleanArray>, column: Int, excludeRow: Int) {
		for (i in matrix.indices) {
			if (i != excludeRow && matrix[i][column]) {
				Arrays.fill(matrix[i], false)
			}
		}
	}

	companion object {
		fun create(totalOptions: Int, totalConstraints: Int): BaseExactCoverMatrix =
			BaseExactCoverMatrix(totalOptions, totalConstraints)

		fun createWithMatrix(matrix: Array<BooleanArray>): BaseExactCoverMatrix {
			require(matrix.isNotEmpty()) { "Matrix must not be empty" }
			return BaseExactCoverMatrix(matrix.size, matrix[0].size).apply {
				for (i in matrix.indices) {
					matrix[i].copyInto(this.matrix[i])
				}
			}
		}
	}
}
