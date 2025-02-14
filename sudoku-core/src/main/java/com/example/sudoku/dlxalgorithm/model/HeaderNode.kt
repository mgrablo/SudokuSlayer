package com.example.sudoku.dlxalgorithm.model

class HeaderNode(val name: String) : DLXNode() {
	init {
		left = this
		right = this
		up = this
		down = this
	}

	var numOfNodes: Int = 0

	// Removes column from the matrix, as well as every row that has node in said column
	fun cover() {
		// Hides column by pointing this node's neighbors at each other
		removeLeftRight()

		// Going top to bottom
		var colNode = this.down // node in column
		while (colNode != this) {
			// Going left to right
			var rowNode = colNode.right // node in row
			while (rowNode != colNode) {
				// Hides node by pointing (up, down) neighbors at each other
				rowNode.removeUpDown()
// 				println((rowNode as DataNode).name)
				(rowNode as DataNode).header.run {
					numOfNodes = numOfNodes - 1
// 					println("header: ${rowNode.header.name} nodes: ${rowNode.header.numOfNodes}")
				}
				// Move right
				rowNode = rowNode.right
				// Move down
			}
			colNode = colNode.down
		}
	}

	// Reinserts column to the matrix as well as every covered node, by doing cover function backwards
	fun uncover() {
		// Going bottom to top
		var colNode = this.up
		while (colNode != this) {
			// Going left to right
			var rowNode = colNode.left
			while (rowNode != colNode) {
				rowNode.up.insertDown(rowNode)
				(rowNode as DataNode).header.numOfNodes++
				// Move left
				rowNode = rowNode.left
			}
			// Move up
			colNode = colNode.up
		}

		// Change this column's neighbors points back to this node
		this.left.insertRight(this)
	}
}

// Searches for column with min number of nodes
