import com.example.sudoku.dlxalgorithm.model.HeaderNode
import com.example.sudoku.dlxalgorithm.model.findBestColumn
import com.example.sudoku.dlxalgorithm.toRootNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class NodeTest {
	private val testMatrix =
		arrayOf(
			booleanArrayOf(true, false, true),
			booleanArrayOf(true, true, false),
			booleanArrayOf(false, true, true),
		)

	@Test
	fun `cover should remove column`() {
		val rootNode = testMatrix.toRootNode()
		val firstColumn = rootNode.right as HeaderNode
		firstColumn.cover()

		assertNotEquals(
			firstColumn,
			rootNode.right,
			"First column should be removed from the matrix",
		)
		assertEquals("H1", (rootNode.right as HeaderNode).name, "Next column header should be 'H1'")
		assertEquals(
			"H2",
			(rootNode.right.right as HeaderNode).name,
			"Next column header should be 'H2'",
		)
	}

	@Test
	fun `uncover should restore covered columns`() {
		val rootNode = testMatrix.toRootNode()
		val firstColumn = rootNode.right as HeaderNode
		rootNode.printAllNodes()
		firstColumn.cover()

		firstColumn.uncover()

		rootNode.printAllNodes()

		assertEquals(firstColumn, rootNode.right, "First column should be restored to the matrix")
	}

	@Test
	fun `uncover should restore covered rows`() {
		val rootNode = testMatrix.toRootNode()
		val firstColumn = rootNode.right as HeaderNode
		val secondColumn = rootNode.right.right as HeaderNode

		println("Cover first column")
		firstColumn.cover()
		println("Cover second column")
		secondColumn.cover()

		println("Uncover second column")
		secondColumn.uncover()

		assertEquals(2, firstColumn.numOfNodes, "First column should have 2 node after uncovering second column")
		assertEquals(1, secondColumn.numOfNodes, "Second column should have 2 nodes after uncovering second column")

		println("Uncover first column")
		firstColumn.uncover()

		assertEquals(2, firstColumn.numOfNodes, "First column should have 1 node after uncover")
		assertEquals(2, secondColumn.numOfNodes, "Second column should have 2 nodes after uncover")
	}

	@Test
	fun `cover should remove affected rows`() {
		val matrix =
			arrayOf(
				booleanArrayOf(false, false, true),
				booleanArrayOf(true, true, false),
				booleanArrayOf(false, true, true),
			)
		val rootNode = matrix.toRootNode()
		val firstColumn = rootNode.right as HeaderNode
		firstColumn.cover()
		val secondColumn = rootNode.right as HeaderNode
		val thirdColumn = rootNode.right.right as HeaderNode
		assertEquals(1, firstColumn.numOfNodes, "First column should have 1 node after cover")
		assertEquals(1, secondColumn.numOfNodes, "Second column should have 1 node after cover")
		assertEquals(2, thirdColumn.numOfNodes, "Third column should have 2 node after cover")
	}

	@Test
	fun `insertRight should insert node to the right`() {
		val firstNode = HeaderNode("H1")
		val secondNode = HeaderNode("H2")
		val newNode = HeaderNode("H3")

		firstNode.insertRight(secondNode)
		firstNode.insertRight(newNode)

		assertEquals(newNode, firstNode.right, "New node should be to the right of the first node")
		assertEquals(newNode, secondNode.left, "First node should be to the left of the second node")
		assertEquals(secondNode, newNode.right, "Second node should be to the left of the new node")
		assertEquals(firstNode, newNode.left, "First node should be to the right of the new node")
	}

	@Test
	fun `insertDown should insert node below`() {
		val firstNode = HeaderNode("H1")
		val secondNode = HeaderNode("H2")
		val newNode = HeaderNode("H3")

		firstNode.insertDown(secondNode)
		firstNode.insertDown(newNode)

		assertEquals(newNode, firstNode.down, "New node should be below the first node")
		assertEquals(newNode, secondNode.up, "First node should be above the second node")
		assertEquals(secondNode, newNode.down, "Second node should be above the new node")
		assertEquals(firstNode, newNode.up, "First node should be below the new node")
	}

	@Test
	fun `removeLeftRight should remove node from horizontal list`() {
		val firstNode = HeaderNode("H1")
		val secondNode = HeaderNode("H2")
		val thirdNode = HeaderNode("H3")

		firstNode.insertRight(secondNode)
		secondNode.insertRight(thirdNode)

		secondNode.removeLeftRight()

		assertEquals(firstNode, thirdNode.left, "First node should be to the left of the third node")
		assertEquals(thirdNode, firstNode.right, "Third node should be to the right of the first node")
	}

	@Test
	fun `removeUpDown should remove node from vertical list`() {
		val firstNode = HeaderNode("H1")
		val secondNode = HeaderNode("H2")
		val thirdNode = HeaderNode("H3")

		firstNode.insertDown(secondNode)
		secondNode.insertDown(thirdNode)

		secondNode.removeUpDown()

		assertEquals(firstNode, thirdNode.up, "First node should be above the third node")
		assertEquals(thirdNode, firstNode.down, "Third node should be below the first node")
	}

	@Test
	fun `should handle empty matrix`() {
		val emptyMatrix = arrayOf<BooleanArray>()
		val root = emptyMatrix.toRootNode()
		assertEquals(root.right, root, "Right of root should be root itself for empty matrix")
		assertEquals(root.left, root, "Left of root should be root itself for empty matrix")
	}

	@Test
	fun `should find best column`() {
		val matrix =
			arrayOf(
				booleanArrayOf(true, false, false),
				booleanArrayOf(true, true, false),
				booleanArrayOf(false, true, true),
			)
		val root = matrix.toRootNode()
		val bestColumn = root.findBestColumn() as HeaderNode
		assertEquals("H2", bestColumn.name, "Best column should be 'H0'")
	}
}
