package io.github.mgrablo.sudokuslayer.data.game.mappers

import io.github.mgrablo.sudokucore.model.SolutionGrid
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokuslayer.data.game.ProtoGrid
import io.github.mgrablo.sudokuslayer.data.game.ProtoSolutionGrid
import kotlin.math.sqrt

fun ProtoGrid.toSudokuGrid(): SudokuGrid = SudokuGrid
	.fromCellData(cellList.map { it.toSudokuCellData() })
	.withSeed(seed)

fun SudokuGrid.toProtoGrid(): ProtoGrid = ProtoGrid
	.newBuilder()
	.addAllCell(getArray().map { it.toProtoCell() })
	.setGridSize(gridSize)
	.setSeed(seed ?: 0)
	.build()

fun ProtoSolutionGrid.toSolutionGrid(): SolutionGrid {
	require(
		sqrt(this.valuesList.size.toFloat()).toInt().let {
			it * it == this.valuesCount
		},
	) { "Solution grid must be square" }
	return SolutionGrid(
		values = valuesList.toIntArray(),
		size = sqrt(valuesList.size.toFloat()).toInt(),
	)
}

fun SolutionGrid.toProtoSolutionGrid(): ProtoSolutionGrid = ProtoSolutionGrid.newBuilder()
	.addAllValues(getArray().toList())
	.build()
