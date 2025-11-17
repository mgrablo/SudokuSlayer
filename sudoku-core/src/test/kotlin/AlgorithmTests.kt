
import io.github.mgrablo.sudokucore.dlxalgorithm.DLXAlgorithm.solve
import io.github.mgrablo.sudokucore.dlxalgorithm.DLXAlgorithm.solveAll
import io.github.mgrablo.sudokucore.dlxalgorithm.model.HeaderNode
import io.github.mgrablo.sudokucore.dlxalgorithm.model.RootNode
import io.github.mgrablo.sudokucore.dlxalgorithm.toRootNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AlgorithmTests {
	@Nested
	@DisplayName("Matrix Conversion Tests")
	inner class MatrixConversionTests {
		@Test
		fun `should convert simple boolean matrix to DLX structure`() {
			val booleanMatrix =
				arrayOf(
					booleanArrayOf(true, false, true, true, false),
					booleanArrayOf(false, true, false, true, false),
					booleanArrayOf(false, true, true, true, false),
				)

			val root = booleanMatrix.toRootNode()

			// Verify structure
			assertEquals("root", root.name, "Root node name should be 'Root node'")
			assertEquals("H0", (root.right as HeaderNode).name, "First column header should be 'H0'")
			assertEquals("H1", (root.right.right as HeaderNode).name, "Second column header should be 'H1'")
			assertEquals(
				"H2",
				(root.right.right.right as HeaderNode).name,
				"Third column header should be 'H2'",
			)
			assertEquals(1, (root.right as HeaderNode).numOfNodes, "Column 0 should have 1 nodes")
			assertEquals(2, (root.right.right as HeaderNode).numOfNodes, "Column 1 should have 2 nodes")
			assertEquals(
				0,
				(root.right.right.right.right.right as HeaderNode).numOfNodes,
				"Column 4 should have 0 nodes",
			)
		}

		@Test
		fun `should handle empty matrix`() {
			val emptyMatrix = arrayOf<BooleanArray>()
			val root = emptyMatrix.toRootNode()
			assertEquals(root.right, root, "Right of root should be root itself for empty matrix")
			assertEquals(root.left, root, "Left of root should be root itself for empty matrix")
		}
	}

	@Nested
	@DisplayName("DLX Algorithm Tests")
	inner class DLXTests {
		@Test
		@DisplayName("Should find single solution for simple exact cover problem")
		fun testSimpleExactCover() {
			val root = createSimpleMatrix()

			val result = mutableListOf<List<Int>>()
			root.solveAll().also { result.addAll(it) }

			assertEquals(1, result.size, "Should find exactly one solution")
			assertTrue(
				result[0].containsAll(listOf(0, 2)),
				"Solution should include rows 0 and 2",
			)
		}

		@Test
		@DisplayName("Should find all solutions when no limit specified")
		fun testMultipleSolutions() {
			// Matrix with multiple solutions
			val root = createMatrixWithMultipleSolutions()

			val result = mutableSetOf<List<Int>>()
			root.solveAll().also { result.addAll(it) }

			assertTrue(result.size > 1, "Should find multiple solutions")
		}

		@Test
		@DisplayName("Should respect solution limit")
		fun testSolutionLimit() {
			val root = createMatrixWithMultipleSolutions()
			val limit = 2

			val result = mutableSetOf<List<Int>>()
			root.solveAll().also { result.addAll(it) }

			assertEquals(
				limit,
				result.size,
				"Should only find specified number of solutions",
			)
		}

		@Test
		@DisplayName("Should handle matrix with no possible solution")
		fun testNoSolutionMatrix() {
			// Matrix with conflicting constraints that cannot be satisfied
			// 1 1 0    Column 1 needs to be covered
			// 0 0 1    Column 2 needs to be covered
			// 0 0 1    But covering any row creates conflicts
			val noSolutionMatrix =
				arrayOf(
					booleanArrayOf(true, true, false),
					booleanArrayOf(false, true, true),
					booleanArrayOf(true, false, true),
				).toRootNode()

			val result = mutableListOf<Int>()
			noSolutionMatrix.solve {
				result.addAll(it)
			}

			assertEquals(
				0,
				result.size,
				"Matrix with no possible solution should have empty solutions list",
			)
		}

		@Test
		@DisplayName("Should handle empty matrix")
		fun testEmptyMatrix() {
			val emptyMatrix = arrayOf<BooleanArray>().toRootNode()

			val result = mutableListOf<Int>()
			emptyMatrix.solve {
				result.addAll(it)
			}

			assertEquals(
				0,
				result.size,
				"Empty matrix should have no solutions",
			)
		}

		@Test
		@DisplayName("Should handle matrix with all false values")
		fun testAllFalseMatrix() {
			val allFalseMatrix =
				arrayOf(
					booleanArrayOf(false, false, false),
					booleanArrayOf(false, false, false),
					booleanArrayOf(false, false, false),
				).toRootNode()

			val result = mutableListOf<List<Int>>()
			allFalseMatrix.solveAll().also { result.addAll(it) }

			assertEquals(
				0,
				result.size,
				"Matrix with all false values should have no solutions",
			)
		}

		private fun createLargeMatrix(rows: Int, cols: Int): RootNode {
			val matrix = Array(rows) { BooleanArray(cols) { row -> (row) % 2 == 0 } }
			val root = matrix.toRootNode()
			return root
		}

		private fun createSimpleMatrix(): RootNode {
			// Helper to create test matrix
			// Implementation details below
			// 1 0 0 1
			// 1 1 0 0
			// 0 1 1 0
			// 0 1 0 1

			val root =
				arrayOf(
					booleanArrayOf(true, false, false, true),
					booleanArrayOf(true, true, false, false),
					booleanArrayOf(false, true, true, false),
					booleanArrayOf(false, true, false, true),
				).toRootNode()

			return root
		}

		private fun createMatrixWithMultipleSolutions(): RootNode {
			// Helper to create test matrix with multiple solutions
			// 1 0 0 1
			// 0 1 0 0
			// 0 0 1 0
			// 0 1 1 0
			// 0 0 1 0

			val root =
				arrayOf(
					booleanArrayOf(true, false, false, true),
					booleanArrayOf(false, true, false, false),
					booleanArrayOf(false, false, true, false),
					booleanArrayOf(false, true, true, false),
				).toRootNode()

			return root
		}
	}
}
