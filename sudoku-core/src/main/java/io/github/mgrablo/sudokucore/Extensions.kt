package io.github.mgrablo.sudokucore

import io.github.mgrablo.sudokucore.model.SolutionGrid
import io.github.mgrablo.sudokucore.model.SudokuGrid

fun symmetricDifference(lists: List<Set<Int>>): Set<Int> = lists
	.flatMap { it.toSet() }
	.groupingBy { it }
	.eachCount()
	.filter { it.value == 1 }
	.keys

fun SudokuGrid.toSolutionGrid(): SolutionGrid {
	val values = IntArray(gridSize * gridSize)
	for (cell in data) {
		val index = cell.row * gridSize + cell.col
		if (index in values.indices) {
			values[index] = cell.number
		}
	}
	return SolutionGrid(values, gridSize)
}
