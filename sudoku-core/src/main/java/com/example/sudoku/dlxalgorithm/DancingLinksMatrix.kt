package com.example.sudoku.dlxalgorithm

import com.example.sudoku.dlxalgorithm.model.DLXNode
import com.example.sudoku.dlxalgorithm.model.DataNode
import com.example.sudoku.dlxalgorithm.model.HeaderNode
import com.example.sudoku.dlxalgorithm.model.RootNode

class DancingLinksMatrix(val rootNode: RootNode) {
	companion object { }
}

fun ExactCoverMatrix.toDancingLinksMatrix(): DancingLinksMatrix = DancingLinksMatrix(matrix.toRootNode())

// Converts exact cover boolean matrix into dancing links matrix
fun Array<BooleanArray>.toRootNode(): RootNode =
	RootNode().apply {
		val columnHeaders = ArrayList<HeaderNode>()
		val numOfColumns =
			when {
				this@toRootNode.isNotEmpty() -> this@toRootNode[0].size
				else -> 0
			}

		// Convert headers
		for (i in 0..<numOfColumns) {
			columnHeaders.add(HeaderNode(name = "H$i"))
			// Insert new columns to the right
			if (i > 0) {
				columnHeaders[i - 1].insertRight(columnHeaders[i])
			} else {
				this.right.insertRight(columnHeaders[0])
			}
		}

		// For each row in matrix
		forEachIndexed { rowIndex, row ->
			// For each column in matrix
			var prevNode: DLXNode? = null
			row.forEachIndexed { colIndex, conditionIsSet ->
				if (conditionIsSet) {
					val header = columnHeaders[colIndex]
					val newNode = DataNode(name = "r${rowIndex}c$colIndex", rowId = rowIndex, header = header)
					// Loop is going top to bottom, so inserting below node that is above header(bottom of column)
					// will always insert at the bottom of the column
					header.up.insertDown(newNode)
					header.numOfNodes++

					// Loop is going left to right, so inserting will always be to the right of the previous node
					prevNode?.insertRight(newNode)
					prevNode = newNode
				}
			}
		}
	}
