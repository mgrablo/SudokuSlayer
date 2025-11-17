import io.github.mgrablo.sudokucore.model.SudokuGrid
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SudokuGrid")
class SudokuGridTest {
	private lateinit var grid: SudokuGrid

	@BeforeEach
	fun setup() {
		grid =
			SudokuGrid.fromIntArray(
				listOf(
					intArrayOf(5, 3, 4, 6, 7, 8, 9, 1, 2),
					intArrayOf(6, 7, 2, 1, 9, 5, 3, 4, 8),
					intArrayOf(1, 9, 8, 3, 4, 2, 5, 6, 7),
					intArrayOf(8, 5, 9, 7, 6, 1, 4, 2, 3),
					intArrayOf(4, 2, 6, 8, 5, 3, 7, 9, 1),
					intArrayOf(7, 1, 3, 9, 2, 4, 8, 5, 6),
					intArrayOf(9, 6, 1, 5, 3, 7, 2, 8, 4),
					intArrayOf(2, 8, 7, 4, 1, 9, 6, 3, 5),
					intArrayOf(3, 4, 5, 2, 8, 6, 1, 7, 9),
				),
			)
	}

	@Nested
	@DisplayName("Grid section retrieval")
	inner class GridRetrieval {
		@Test
		@DisplayName("should return correct row")
		fun getRow() {
			val expected = intArrayOf(2, 8, 7, 4, 1, 9, 6, 3, 5)
			val result = grid.getRow(7).map { it.number }.toIntArray()

			assertArrayEquals(
				expected,
				result,
				"Row 7 should match expected values",
			)
		}

		@Test
		@DisplayName("should return correct column")
		fun getColumn() {
			val expected = intArrayOf(5, 6, 1, 8, 4, 7, 9, 2, 3)
			val result = grid.getColumn(0).map { it.number }.toIntArray()

			assertArrayEquals(
				expected,
				result,
				"Column 0 should match expected values",
			)
		}

		@Test
		@DisplayName("should return correct subgrid")
		fun getSubgrid() {
			val expected =
				arrayOf(
					intArrayOf(5, 3, 7),
					intArrayOf(4, 1, 9),
					intArrayOf(2, 8, 6),
				).flatMap { it.toList() }.toIntArray()

			val result = grid.getSubgrid(8, 5).map { it.number }.toIntArray()

			assertArrayEquals(
				expected,
				result,
				"Subgrid at (8,5) should match expected values",
			)
		}
	}
}
