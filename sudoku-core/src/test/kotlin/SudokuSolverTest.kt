import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.solver.ClassicSudokuSolver
import io.github.mgrablo.sudokucore.solver.createConstraints
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("Sudoku Solver")
class SudokuSolverTest {
	companion object {
		@JvmStatic
		fun rowTestData(): Stream<Arguments> = Stream.of(
			Arguments.of(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), true),
			Arguments.of(intArrayOf(0, 2, 3, 0, 5, 6, 7, 8, 9), true),
			Arguments.of(intArrayOf(0, 2, 3, 4, 5, 6, 7, 8, 8), false),
			Arguments.of(intArrayOf(0, 0, 3, 0, 0, 3, 7, 0, 0), false),
		)

		@JvmStatic
		fun subgridTestData(): Stream<Arguments> = Stream.of(
			Arguments.of(
				arrayOf(
					intArrayOf(1, 2, 3),
					intArrayOf(4, 5, 6),
					intArrayOf(7, 8, 9),
				),
				true,
			),
			Arguments.of(
				arrayOf(
					intArrayOf(1, 2, 3),
					intArrayOf(4, 5, 6),
					intArrayOf(7, 8, 8),
				),
				false,
			),
			Arguments.of(
				arrayOf(
					intArrayOf(1, 0, 0),
					intArrayOf(0, 5, 0),
					intArrayOf(0, 0, 9),
				),
				true,
			),
			Arguments.of(
				arrayOf(
					intArrayOf(1, 0, 0),
					intArrayOf(0, 0, 1),
					intArrayOf(0, 0, 9),
				),
				false,
			),
		)
	}

	@Nested
	@DisplayName("Row Validation")
	inner class RowValidation {
		@ParameterizedTest(name = "Row {0} should be {1}")
		@MethodSource("SudokuSolverTest#rowTestData")
		fun validateRow(row: IntArray, expected: Boolean) {
			assertEquals(expected, ClassicSudokuSolver.checkRow(row))
		}
	}

	@Nested
	@DisplayName("Subgrid Validation")
	inner class SubgridValidation {
		@ParameterizedTest(name = "Subgrid case {index}")
		@MethodSource("SudokuSolverTest#subgridTestData")
		fun validateSubgrid(subgrid: Array<IntArray>, expected: Boolean) {
			assertEquals(
				expected,
				ClassicSudokuSolver.checkSubgrid(subgrid.flatMap { it.toList() }.toIntArray()),
				"Subgrid validation failed",
			)
		}
	}

	@Nested
	@DisplayName("Move Validation")
	inner class MoveValidation {
		private val sudoku =
			SudokuGrid.fromIntArray(
				listOf(
					intArrayOf(5, 3, 0, 0, 7, 0, 0, 0, 0),
					intArrayOf(6, 0, 0, 1, 9, 5, 0, 0, 0),
					intArrayOf(0, 9, 8, 0, 0, 0, 0, 6, 0),
					intArrayOf(8, 0, 0, 0, 6, 0, 0, 0, 3),
					intArrayOf(4, 0, 0, 8, 0, 3, 0, 0, 1),
					intArrayOf(7, 0, 0, 0, 2, 0, 0, 0, 6),
					intArrayOf(0, 6, 0, 0, 0, 0, 2, 8, 0),
					intArrayOf(0, 0, 0, 4, 1, 9, 0, 0, 5),
					intArrayOf(0, 0, 0, 0, 8, 0, 0, 7, 9),
				),
			)

		@Test
		@DisplayName("Should reject invalid row move")
		fun invalidRowMove() {
			assertEquals(
				false,
				ClassicSudokuSolver.isValidMove(sudoku, 0, 2, 3),
				"Move should be invalid due to row conflict",
			)
		}

		@Test
		@DisplayName("Should reject invalid column move")
		fun invalidColumnMove() {
			assertEquals(
				false,
				ClassicSudokuSolver.isValidMove(sudoku, 2, 4, 7),
				"Move should be invalid due to column conflict",
			)
		}

		@Test
		@DisplayName("Should reject invalid subgrid move")
		fun invalidSubgridMove() {
			assertEquals(
				false,
				ClassicSudokuSolver.isValidMove(sudoku, 1, 1, 6),
				"Move should be invalid due to subgrid conflict",
			)
		}

		@Test
		@DisplayName("Should accept valid move")
		fun validMove() {
			assertEquals(
				true,
				ClassicSudokuSolver.isValidMove(sudoku, 0, 2, 1),
				"Move should be valid",
			)
		}
	}

	@Nested
	@DisplayName("Grid Filling")
	inner class GridFilling {
		@Test
		@DisplayName("Should correctly fill partially complete grid")
		fun fillPartialGrid() = runBlocking {
			val grid =
				SudokuGrid.fromIntArray(
					listOf(
						intArrayOf(5, 3, 0, 0, 7, 0, 0, 0, 0),
						intArrayOf(6, 0, 0, 1, 9, 5, 0, 0, 0),
						intArrayOf(0, 9, 8, 0, 0, 0, 0, 6, 0),
						intArrayOf(8, 0, 0, 0, 6, 0, 0, 0, 3),
						intArrayOf(4, 0, 0, 8, 0, 3, 0, 0, 1),
						intArrayOf(7, 0, 0, 0, 2, 0, 0, 0, 6),
						intArrayOf(0, 6, 0, 0, 0, 0, 2, 8, 0),
						intArrayOf(0, 0, 0, 4, 1, 9, 0, 0, 5),
						intArrayOf(0, 0, 0, 0, 8, 0, 0, 7, 9),
					),
				)

			val solvedGrid = ClassicSudokuSolver.fillGrid(grid)
			assertNotNull(solvedGrid, "Should return a solved grid")
			assertTrue(
				ClassicSudokuSolver.isValidSolution(solvedGrid!!),
				"Filled grid should be a valid solution",
			)
		}
	}

	@Nested
	@DisplayName("Unique Solution Validation")
	inner class UniqueSolutionValidation {
		@Test
		@DisplayName("Should detect non-unique solution")
		fun detectNonUniqueSolution() = runBlocking {
			val grid =
				SudokuGrid.fromIntArray(
					listOf(
						intArrayOf(9, 2, 6, 5, 7, 1, 4, 8, 3),
						intArrayOf(3, 5, 1, 4, 8, 6, 2, 7, 9),
						intArrayOf(8, 7, 4, 9, 2, 3, 5, 1, 6),
						intArrayOf(5, 8, 2, 3, 6, 7, 1, 9, 4),
						intArrayOf(1, 4, 9, 2, 5, 8, 3, 6, 7),
						intArrayOf(7, 6, 3, 1, 0, 0, 8, 2, 5),
						intArrayOf(2, 3, 8, 7, 0, 0, 6, 5, 1),
						intArrayOf(6, 1, 7, 8, 3, 5, 9, 4, 2),
						intArrayOf(4, 9, 5, 6, 1, 2, 7, 3, 8),
					),
				)
			assertEquals(false, ClassicSudokuSolver.hasUniqueSolution(grid))
		}

		@Test
		@DisplayName("Should detect unique solution")
		fun detectUniqueSolution() = runBlocking {
			val grid =
				SudokuGrid.fromIntArray(
					listOf(
						intArrayOf(5, 3, 0, 0, 7, 0, 0, 0, 0),
						intArrayOf(6, 0, 0, 1, 9, 5, 0, 0, 0),
						intArrayOf(0, 9, 8, 0, 0, 0, 0, 6, 0),
						intArrayOf(8, 0, 0, 0, 6, 0, 0, 0, 3),
						intArrayOf(4, 0, 0, 8, 0, 3, 0, 0, 1),
						intArrayOf(7, 0, 0, 0, 2, 0, 0, 0, 6),
						intArrayOf(0, 6, 0, 0, 0, 0, 2, 8, 0),
						intArrayOf(0, 0, 0, 4, 1, 9, 0, 0, 5),
						intArrayOf(0, 0, 0, 0, 8, 0, 0, 7, 9),
					),
				)

			assertTrue(
				ClassicSudokuSolver.hasUniqueSolution(grid),
				"Grid should have unique solution",
			)
		}
	}

	@Nested
	@DisplayName("Edge Cases")
	inner class EdgeCases {
		@Test
		@DisplayName("Should handle empty grid")
		fun handleEmptyGrid() = runBlocking {
			val grid = SudokuGrid()
			val filledGrid = ClassicSudokuSolver.fillGrid(grid)
			println(grid)
			println("===")
			println(filledGrid)
			assertNotNull(filledGrid, "Should return filled grid")
			assertEquals(true, ClassicSudokuSolver.isValidSolution(filledGrid!!))
		}

		@Test
		@DisplayName("Should handle nearly complete grid")
		fun handleNearlyCompleteGrid() = runBlocking {
			val grid =
				SudokuGrid.fromIntArray(
					listOf(
						intArrayOf(5, 3, 4, 6, 7, 8, 9, 1, 2),
						intArrayOf(6, 7, 2, 1, 9, 5, 3, 4, 8),
						intArrayOf(1, 9, 8, 3, 4, 2, 5, 6, 7),
						intArrayOf(8, 5, 9, 7, 6, 1, 4, 2, 3),
						intArrayOf(4, 2, 6, 8, 5, 3, 7, 9, 1),
						intArrayOf(7, 1, 3, 9, 2, 4, 8, 5, 6),
						intArrayOf(9, 6, 1, 5, 3, 7, 2, 8, 4),
						intArrayOf(2, 8, 7, 4, 1, 9, 6, 3, 0), // Only one cell empty
						intArrayOf(3, 4, 5, 2, 8, 6, 1, 7, 9),
					),
				)
			val solvedGrid = ClassicSudokuSolver.solve(grid)
			assertNotNull(solvedGrid, "Should return solved grid")
			assertEquals(
				true,
				ClassicSudokuSolver.isValidSolution(solvedGrid!!),
				"Should be valid solution",
			)
		}

		@Nested
		@DisplayName("Unsolvable Grids")
		inner class UnsolvableGrids {
			@Test
			@DisplayName("Should detect grid with no valid moves for a cell")
			fun detectNoValidMovesGrid() = runBlocking {
				val grid =
					SudokuGrid.fromIntArray(
						listOf(
							intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 0),
							intArrayOf(4, 5, 6, 7, 8, 9, 1, 2, 3),
							intArrayOf(7, 8, 9, 1, 2, 3, 4, 5, 6),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 9),
						),
					)
				assertNull(ClassicSudokuSolver.fillGrid(grid))
			}

			@Test
			@DisplayName("Should detect grid with invalid initial state")
			fun detectInvalidInitialState() = runBlocking {
				val grid =
					SudokuGrid.fromIntArray(
						listOf(
							intArrayOf(1, 1, 0, 0, 0, 0, 0, 0, 0), // Duplicate 1s in first row
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
						),
					)
				assertNull(ClassicSudokuSolver.fillGrid(grid))
			}
		}

		@Nested
		@DisplayName("Performance Tests")
		inner class PerformanceTests {
			@Test
			@DisplayName("Should solve anti-brute-force puzzle")
			fun solveAntiBruteForceGrid() = runBlocking {
				// This is the puzzle designed to be hard for brute force algorithms
				// from https://en.wikipedia.org/wiki/Sudoku_solving_algorithms
				val grid =
					SudokuGrid.fromIntArray(
						listOf(
							intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 0, 3, 0, 8, 5),
							intArrayOf(0, 0, 1, 0, 2, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 5, 0, 7, 0, 0, 0),
							intArrayOf(0, 0, 4, 0, 0, 0, 1, 0, 0),
							intArrayOf(0, 9, 0, 0, 0, 0, 0, 0, 0),
							intArrayOf(5, 0, 0, 0, 0, 0, 0, 7, 3),
							intArrayOf(0, 0, 2, 0, 1, 0, 0, 0, 0),
							intArrayOf(0, 0, 0, 0, 4, 0, 0, 0, 9),
						),
					)

				val solved = ClassicSudokuSolver.solve(grid)
				assertNotNull(solved, "Sudoku should be solvable")
				assertEquals(
					true,
					ClassicSudokuSolver.isValidSolution(solved!!),
					"Solution should be valid",
				)
			}
		}

		@Nested
		inner class CreateConstraintsTest {
			@Test
			fun `test createConstraints for top-left cell in 9x9 grid`() {
				val result = createConstraints(0, 0, 0, 9)
				// Calculation:
				// rowColConstraint = 0 * 9 + 0 = 0
				// rowNumConstraint = 9 * 9 + 0 * 9 + 0 = 81
				// colNumConstraint = 2 * 9 * 9 + 0 * 9 + 0 = 162
				// getBoxIndex(0, 0) = (0 / 3) * 3 + (0 / 3) = 0, boxNumConstraint = 3 * 9 * 9 + 0 * 9 + 0 = 243
				val expected = listOf(0, 81, 162, 243)
				assertEquals(expected, result)
			}

			@Test
			fun `test createConstraints for bottom-right cell in 9x9 grid`() {
				val result = createConstraints(8, 8, 8, 9)
				// Calculation:
				// rowColConstraint = 8 * 9 + 8 = 80
				// rowNumConstraint = 9 * 9 + 8 * 9 + 8 = 81 + 72 + 8 = 161
				// colNumConstraint = 2 * 9 * 9 + 8 * 9 + 8 = 162 + 72 + 8 = 242
				// getBoxIndex(8, 8) = (8 / 3) * 3 + (8 / 3) = 2 * 3 + 2 = 8,
				// boxNumConstraint = 3 * 9 * 9 + 8 * 9 + 8 = 243 + 72 + 8 = 323
				val expected = listOf(80, 161, 242, 323)
				assertEquals(expected, result)
			}

			@Test
			fun `test createConstraints for a cell in 4x4 grid`() {
				val result = createConstraints(1, 2, 3, 4)
				// For gridSize 4: subgridSize = sqrt(4) = 2
				// rowColConstraint = 1 * 4 + 2 = 6
				// rowNumConstraint = 4 * 4 + 1 * 4 + 3 = 16 + 4 + 3 = 23
				// colNumConstraint = 2 * 4 * 4 + 2 * 4 + 3 = 32 + 8 + 3 = 43
				// getBoxIndex(1, 2) = (1 / 2) * 2 + (2 / 2) = 0 * 2 + 1 = 1,
				// boxNumConstraint = 3 * 4 * 4 + 1 * 4 + 3 = 48 + 4 + 3 = 55
				val expected = listOf(6, 23, 43, 55)
				assertEquals(expected, result)
			}
		}
	}
}
