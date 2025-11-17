package io.github.mgrablo.sudokucore

import io.github.mgrablo.sudokucore.dlxalgorithm.model.DataNode
import io.github.mgrablo.sudokucore.dlxalgorithm.model.HeaderNode
import io.github.mgrablo.sudokucore.dlxalgorithm.toRootNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DancingLinksMatrixTest {
	@Test
	fun `test empty matrix conversion`() {
		val emptyMatrix = Array(0) { BooleanArray(0) }
		val root = emptyMatrix.toRootNode()

		assertEquals(root, root.right)
		assertEquals(root, root.left)
	}

	@Test
	fun `test single row matrix conversion`() {
		val matrix = Array(1) { BooleanArray(3) { true } }
		val root = matrix.toRootNode()

		// Check headers
		var currentHeader = root.right
		var count = 0
		while (currentHeader != root) {
			assertEquals("H$count", (currentHeader as HeaderNode).name)
			count++
			currentHeader = currentHeader.right
		}
		assertEquals(3, count)
	}

	@Test
	fun `test 2x2 matrix conversion`() {
		val matrix =
			arrayOf(
				booleanArrayOf(true, false),
				booleanArrayOf(false, true),
			)
		val root = matrix.toRootNode()

		// Check first column
		val firstHeader = root.right as HeaderNode
		val firstDataNode = firstHeader.down as DataNode
		assertEquals("r0c0", firstDataNode.name)
		assertEquals(0, firstDataNode.rowId)

		// Check second column
		val secondHeader = firstHeader.right as HeaderNode
		val secondDataNode = secondHeader.down as DataNode
		assertEquals("r1c1", secondDataNode.name)
		assertEquals(1, secondDataNode.rowId)
	}

	@Test
	fun `test horizontal node connections`() {
		val matrix =
			arrayOf(
				booleanArrayOf(true, true, true),
			)
		val root = matrix.toRootNode()

		val firstNode = (root.right as HeaderNode).down as DataNode
		val secondNode = firstNode.right as DataNode
		val thirdNode = secondNode.right as DataNode

		assertEquals(firstNode, thirdNode.right)
		assertEquals(thirdNode, secondNode.right)
		assertEquals(secondNode, firstNode.right)
	}

	@Test
	fun `test vertical node connections`() {
		val matrix =
			arrayOf(
				booleanArrayOf(true),
				booleanArrayOf(true),
			)
		val root = matrix.toRootNode()

		val header = root.right as HeaderNode
		val firstNode = header.down as DataNode
		val secondNode = firstNode.down as DataNode

		assertEquals(header, secondNode.down)
		assertEquals(secondNode, firstNode.down)
		assertEquals(firstNode, header.down)
	}

	@Test
	fun `test header creation with correct names`() {
		val matrix = Array(1) { BooleanArray(4) { true } }
		val root = matrix.toRootNode()

		var header = root.right
		for (i in 0..3) {
			assertEquals("H$i", (header as HeaderNode).name)
			header = header.right
		}
	}

	@Test
	fun `test data node properties`() {
		val matrix =
			arrayOf(
				booleanArrayOf(true, false, true),
			)
		val root = matrix.toRootNode()

		val firstHeader = root.right as HeaderNode
		val firstNode = firstHeader.down as DataNode
		assertEquals("r0c0", firstNode.name)
		assertEquals(0, firstNode.rowId)
		assertEquals(firstHeader, firstNode.header)

		val thirdHeader = firstHeader.right.right as HeaderNode
		val thirdNode = thirdHeader.down as DataNode
		assertEquals("r0c2", thirdNode.name)
		assertEquals(0, thirdNode.rowId)
		assertEquals(thirdHeader, thirdNode.header)
	}
}
