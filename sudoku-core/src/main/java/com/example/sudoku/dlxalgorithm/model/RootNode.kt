package com.example.sudoku.dlxalgorithm.model

import org.jetbrains.annotations.TestOnly

class RootNode(val name: String = "root") : DLXNode() {
	init {
		left = this
		right = this
		up = this
		down = this
	}

	@TestOnly
	fun printAllNodes() {
		var column = this.right
		while (column != this) {
			val headerNode = column as HeaderNode
			println("Column: ${headerNode.name} | nodes: ${headerNode.numOfNodes}")
			var row = column.down
			while (row != column) {
				val dataNode = row as DataNode
				print(" ${dataNode.name} ")
				row = row.down
			}
			column = column.right
		}
	}

	@TestOnly
	fun printNotEmptyNodes() {
		var column = this.right
		while (column != this) {
			val headerNode = column as HeaderNode
			if (headerNode.numOfNodes == 0)
				{
					column = column.right
					continue
				}
			var row = column.down
			while (row != column) {
				row = row.down
			}
			column = column.right
		}
	}
}

fun RootNode.findBestColumn(): HeaderNode? {
	var header: HeaderNode? = null
	var minNodes = Int.MAX_VALUE
	var next = right
	while (next != this && minNodes > 1) {
		val numOfNodes = (next as HeaderNode).numOfNodes
		if (numOfNodes < minNodes)
			{
				minNodes = next.numOfNodes
				header = next
			}
		next = next.right
	}
	return header
}
