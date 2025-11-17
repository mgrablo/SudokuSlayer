package io.github.mgrablo.sudokucore.model

import androidx.compose.runtime.Immutable

@Immutable
data class SolutionGrid(private val values: IntArray, private val size: Int) {
	fun getValue(row: Int, column: Int): Int {
		require(row in 0 until size) { "Invalid row index $row (must be between 0 and ${size - 1})" }
		require(column in 0 until size) {
			"Invalid column index $column (must be between 0 and ${size - 1})"
		}
		return values[row * size + column]
	}

	fun getArray(): IntArray = values

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as SolutionGrid

		if (size != other.size) return false
		if (!values.contentEquals(other.values)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = size
		result = 31 * result + values.contentHashCode()
		return result
	}
}
