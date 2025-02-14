import com.example.sudoku.dlxalgorithm.SudokuExactCoverMatrix
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SudokuExactCoverMatrixTest {
	@Nested
	inner class Creation {
		@Test
		fun `classic 9x9 matrix should initialize with correct dimensions`() {
			val matrix = SudokuExactCoverMatrix.createClassic()
			assertEquals(729, matrix.totalOptions, "Should have 9x9x9 options")
			assertEquals(324, matrix.totalConstraints, "Should have 9x9x4 constraints")
		}

		@Test
		fun `custom 4x4 matrix should initialize with correct dimensions`() {
			val matrix = SudokuExactCoverMatrix.create(4, 2)
			assertEquals(64, matrix.totalOptions, "Should have 4x4x4 options")
			assertEquals(64, matrix.totalConstraints, "Should have 4x4x4 constraints")
		}
	}

	@Nested
	inner class CoveringOperations {
		@Test
		fun `covering single cell should update all related constraints`() {
			val matrix = SudokuExactCoverMatrix.createClassic()
			val result = matrix.cover(0, 0, 1)

			// Row-column constraints: for cell (0,0), number 1 should be excluded, others should be covered
			assertTrue(result[0][0], "Constraint for number 1 in cell (0,0) should be excluded")
			assertFalse(
				result.sliceArray(1..8).all { it[0] },
				"Constraints for other numbers in cell (0,0) should be covered",
			)

			// Row-number constraints: for row 0 and number 1 should be excluded, others should be covered
			assertTrue(result[0][81], "Constraint for number 1 in row 0 should be excluded")
			assertFalse(
				result.sliceArray(1..8).all { it[81] },
				"Constraints for other numbers in row 0 should be covered",
			)

			// Column-number constraints: for column 0 and number 1 should be excluded, others should be covered
			assertTrue(result[0][162], "Constraint for number 1 in column 0 should be excluded")
			assertFalse(
				result.sliceArray(1..8).all { it[162] },
				"Constraints for other numbers in column 0 should be covered",
			)

			// Box-number constraints: for box containing cell (0,0) and number 1 should be excluded, others should be covered
			assertTrue(result[0][243], "Constraint for number 1 in its box should be excluded")
			assertFalse(
				result.sliceArray(1..8).all { it[243] },
				"Constraints for other numbers in the box should be covered",
			)
		}

		@Test
		fun `covering multiple cells should update all constraints correctly`() {
			val matrix = SudokuExactCoverMatrix.createClassic()
			val cells =
				listOf(
					Triple(0, 0, 1),
					Triple(1, 1, 2),
					Triple(2, 2, 3),
				)
			matrix.coverAll(cells)

			// For classic sudoku, each cell has 9 option rows.
			// The option row for cell (r, c) and number num is:
			//   baseOptionRow = (r * 9 + c) * 9
			//   assignedOptionRow = baseOptionRow + (num - 1)
			//
			// The constraints are mapped as follows:
			//   Row-column constraint:          col = r * 9 + c
			//   Row-number constraint:          col = 81 + (r * 9 + (num - 1))
			//   Column-number constraint:       col = 162 + (c * 9 + (num - 1))
			//   Box-number constraint:          Let boxIndex = (r / 3) * 3 + (c / 3),
			//                                   then col = 243 + (boxIndex * 9 + (num - 1))
			//
			// In the covered cells, the constraint corresponding to the given number
			// should be excluded (true), while other numbers in the same cell should be covered (false).

			fun checkCell(
				r: Int,
				c: Int,
				num: Int,
			) {
				val baseOptionRow = (r * 9 + c) * 9
				val assignedOptionRow = baseOptionRow + (num - 1)
				val rowColConstraint = r * 9 + c
				val rowNumConstraint = 81 + (r * 9 + (num - 1))
				val colNumConstraint = 162 + (c * 9 + (num - 1))
				val boxIndex = (r / 3) * 3 + (c / 3)
				val boxNumConstraint = 243 + (boxIndex * 9 + (num - 1))

				for (i in 0 until 9) {
					val optionRow = baseOptionRow + i
					if (i == (num - 1)) {
						// The constraint for the given number should be excluded (true)
						assertTrue(
							matrix.matrix[optionRow][rowColConstraint],
							"Constraint (row-column) for cell ($r,$c) with number $num should be excluded",
						)
						assertTrue(
							matrix.matrix[optionRow][rowNumConstraint],
							"Constraint (row-number) for cell ($r,$c) with number $num should be excluded",
						)
						assertTrue(
							matrix.matrix[optionRow][colNumConstraint],
							"Constraint (column-number) for cell ($r,$c) with number $num should be excluded",
						)
						assertTrue(
							matrix.matrix[optionRow][boxNumConstraint],
							"Constraint (box-number) for cell ($r,$c) with number $num should be excluded",
						)
					} else {
						// Other numbers in the same cell should be covered (false)
						assertFalse(
							matrix.matrix[optionRow][rowColConstraint],
							"Constraint (row-column) for cell ($r,$c) with number ${i + 1} should be covered",
						)
						assertFalse(
							matrix.matrix[optionRow][rowNumConstraint],
							"Constraint (row-number) for cell ($r,$c) with number ${i + 1} should be covered",
						)
						assertFalse(
							matrix.matrix[optionRow][colNumConstraint],
							"Constraint (column-number) for cell ($r,$c) with number ${i + 1} should be covered",
						)
						assertFalse(
							matrix.matrix[optionRow][boxNumConstraint],
							"Constraint (box-number) for cell ($r,$c) with number ${i + 1} should be covered",
						)
					}
				}
			}

			cells.forEach { (r, c, num) ->
				checkCell(r, c, num)
			}
		}

		@Test
		fun `covering adjacent cells should handle overlapping constraints`() {
			val matrix = SudokuExactCoverMatrix.createClassic()
			val cells =
				listOf(
					Triple(0, 0, 1),
					Triple(0, 1, 2),
				)
			matrix.coverAll(cells)

			// For cell (0,0,1)
			val baseRowCell1 = (0 * 9 + 0) * 9
			val selectedRowCell1 = baseRowCell1 + (1 - 1) // option row for chosen number 1

			// For cell (0,1,2)
			val baseRowCell2 = (0 * 9 + 1) * 9
			val selectedRowCell2 = baseRowCell2 + (2 - 1) // option row for chosen number 2

			// In cell (0,0), all non-selected option rows should be covered (false)
			for (i in 0 until 9) {
				if (i != 0) {
					assertFalse(matrix.matrix[baseRowCell1 + i][(0 * 9 + 0)], "Non-selected option in cell (0,0) should be covered")
					// Also, their row-number constraint (index 81) should be covered since only the selected row holds the uncovered constraint.
					assertFalse(
						matrix.matrix[baseRowCell1 + i][81 + (0 * 9 + 0)],
						"Non-selected row-number constraint in cell (0,0) should be covered",
					)
				} else {
					// For the selected option, the cell and row-number constraints should be excluded (true)
					assertTrue(matrix.matrix[selectedRowCell1][(0 * 9 + 0)], "Selected cell constraint in cell (0,0) should be excluded")
					assertTrue(
						matrix.matrix[selectedRowCell1][81 + (0 * 9 + 0)],
						"Selected row-number constraint in cell (0,0) should be excluded",
					)
				}
			}

			// In cell (0,1), similar expectations apply
			for (i in 0 until 9) {
				if (i != 1) {
					assertFalse(matrix.matrix[baseRowCell2 + i][(0 * 9 + 1)], "Non-selected option in cell (0,1) should be covered")
					assertFalse(
						matrix.matrix[baseRowCell2 + i][81 + (0 * 9 + i % 9)],
						"Non-selected row-number constraint in cell (0,1) should be covered",
					)
				} else {
					assertTrue(matrix.matrix[selectedRowCell2][(0 * 9 + 1)], "Selected cell constraint in cell (0,1) should be excluded")
					assertTrue(
						matrix.matrix[selectedRowCell2][81 + (0 * 9 + 1)],
						"Selected row-number constraint in cell (0,1) should be excluded",
					)
				}
			}

			// Shared constraint check:
			// Both covering operations are in row 0, so any option row outside the selected ones for row-number constraints should remain covered.
			// For cell (0,0), for example, only the row-number constraint in the selected option row (index 0, col 81) is excluded.
			// Any other option row in cell (0,0) should have that constraint covered.
			for (i in 1 until 9) {
				assertFalse(
					matrix.matrix[baseRowCell1 + i][81 + (0 * 9 + 0)],
					"Shared row-number constraint in cell (0,0) should be covered in non-selected option rows",
				)
			}
		}
	}
}
