package com.example.data.game.mappers

import com.example.sudoku.model.SolutionGrid
import com.example.sudoku.model.SudokuGrid
import data.game.ProtoGrid
import data.game.ProtoSolutionGrid

fun ProtoGrid.toSudokuGrid(): SudokuGrid = SudokuGrid
	.fromCellData(cellList.map { it.toSudokuCellData() })
	.withSeed(seed)

fun SudokuGrid.toProtoGrid(): ProtoGrid = ProtoGrid
	.newBuilder()
	.addAllCell(getArray().map { it.toProtoCell() })
	.setGridSize(gridSize)
	.setSeed(seed ?: 0)
	.build()

fun ProtoSolutionGrid.toSolutionGrid(): SolutionGrid =
	SolutionGrid(valuesList.toIntArray(), valuesList.size)

fun SolutionGrid.toProtoSolutionGrid(): ProtoSolutionGrid = ProtoSolutionGrid.newBuilder()
	.addAllValues(getArray().toList())
	.build()
