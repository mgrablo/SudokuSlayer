package com.example.sudoku.dlxalgorithm.model

class DataNode(
	val header: HeaderNode,
	val rowId: Int,
	val name: String = "",
) : DLXNode() {
	init {
		left = this
		right = this
		up = this
		down = this
	}
}
