package io.github.mgrablo.sudokucore.dlxalgorithm.model

class DataNode(val header: HeaderNode, val rowId: Int, val name: String = "") : DLXNode() {
	init {
		left = this
		right = this
		up = this
		down = this
	}
}
