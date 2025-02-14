import com.example.sudoku.dlxalgorithm.BaseExactCoverMatrix
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BaseExactCoverMatrixTest {
	@Test
	fun fillMatrixRowShouldSetCorrectConstraints() {
		val totalOptions = 4
		val totalConstraints = 4
		val exactCoverMatrix = BaseExactCoverMatrix(totalOptions, totalConstraints)
		val matrixRow = BooleanArray(totalConstraints)
		val constraints = listOf(0, 2)

		exactCoverMatrix.fillMatrixRow(matrixRow, constraints)

		assertTrue(matrixRow[0])
		assertFalse(matrixRow[1])
		assertTrue(matrixRow[2])
		assertFalse(matrixRow[3])
	}

	@Test
	fun createShouldInitializeCorrectly() {
		val totalOptions = 3
		val totalConstraints = 2
		val exactCoverMatrix = BaseExactCoverMatrix.create(totalOptions, totalConstraints)
		assertArrayEquals(
			Array(totalOptions) { BooleanArray(totalConstraints) { false } },
			exactCoverMatrix.matrix,
		)
	}

	@Test
	fun createWithMatrixShouldInitializeCorrectly() {
		val matrix =
			arrayOf(
				booleanArrayOf(true, false, true),
				booleanArrayOf(false, true, false),
			)
		val exactCoverMatrix = BaseExactCoverMatrix.createWithMatrix(matrix)
		assertArrayEquals(matrix, exactCoverMatrix.matrix)
	}

	@Test
	fun createWithMatrixShouldRejectEmptyGrid() {
		val matrix = arrayOf<BooleanArray>()
		assertThrows<IllegalArgumentException>(IllegalArgumentException::class.java) {
			BaseExactCoverMatrix.createWithMatrix(matrix)
		}
	}

	@Test
	fun coverShouldExcludeSpecifiedRow() {
		val matrix =
			arrayOf(
				booleanArrayOf(true, false, true),
				booleanArrayOf(false, true, false),
				booleanArrayOf(true, true, false),
			)
		val expected =
			arrayOf(
				booleanArrayOf(true, false, true),
				booleanArrayOf(false, true, false),
				booleanArrayOf(false, false, false),
			)
		val exactCoverMatrix = BaseExactCoverMatrix.createWithMatrix(matrix)
		exactCoverMatrix.cover(matrix, 0, 0)
		assertArrayEquals(expected, matrix)
	}

	@Test
	fun coverShouldHandleSingleColumnMatrix() {
		val matrix =
			arrayOf(
				booleanArrayOf(true),
				booleanArrayOf(false),
				booleanArrayOf(true),
			)
		val expected =
			arrayOf(
				booleanArrayOf(false),
				booleanArrayOf(false),
				booleanArrayOf(false),
			)
		val exactCoverMatrix = BaseExactCoverMatrix.createWithMatrix(matrix)
		exactCoverMatrix.cover(matrix, 0, -1)
		assertArrayEquals(expected, matrix)
	}

	@Test
	fun coverShouldCoverCorrectly() {
		val matrix =
			arrayOf(
				booleanArrayOf(true, false, true),
				booleanArrayOf(false, true, false),
				booleanArrayOf(true, true, false),
			)
		val expected =
			arrayOf(
				booleanArrayOf(false, false, false),
				booleanArrayOf(false, true, false),
				booleanArrayOf(false, false, false),
			)
		val exactCoverMatrix = BaseExactCoverMatrix.createWithMatrix(matrix)
		exactCoverMatrix.cover(matrix, 0, -1)
		assertArrayEquals(expected, matrix)
	}
}
