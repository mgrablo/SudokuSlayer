package com.example.data.game.mappers

import com.example.sudoku.model.SudokuGrid
import data.game.ProtoGrid

fun ProtoGrid.toSudokuGrid(): SudokuGrid = SudokuGrid
	.fromCellData(cellList.map { it.toSudokuCellData() })
	.withSeed(seed)

fun SudokuGrid.toProtoGrid(): ProtoGrid = ProtoGrid
	.newBuilder()
	.addAllCell(getArray().map { it.toProtoCell() })
	.setGridSize(gridSize)
	.setSeed(seed ?: 0)
	.build()
