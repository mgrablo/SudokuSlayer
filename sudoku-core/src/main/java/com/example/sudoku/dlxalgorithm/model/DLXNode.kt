package com.example.sudoku.dlxalgorithm.model

abstract class DLXNode {
	lateinit var left: DLXNode
		protected set
	lateinit var right: DLXNode
		protected set
	lateinit var up: DLXNode
		protected set
	lateinit var down: DLXNode
		protected set

	// Used for inserting column headers
	fun insertRight(node: DLXNode) {
		// Pointers of new node
		node.left = this
		node.right = this.right

		// Pointers of node that was to the right before insert
		this.right.left = node

		// Update this node to point to new node
		this.right = node
	}

	// Used for inserting matrix nodes
	fun insertDown(node: DLXNode) {
		// Set pointers of new node
		node.up = this
		node.down = this.down

		// Pointers of node that was below this node before insert
		this.down.up = node

		// Update this node
		this.down = node
	}

	// Hides column by pointing this node's neighbors at each other
	fun removeLeftRight() {
		left.right = right
		right.left = left
	}

	// Hides node by pointing (up, down) neighbors at each other
	fun removeUpDown() {
		up.down = down
		down.up = up
	}
}
