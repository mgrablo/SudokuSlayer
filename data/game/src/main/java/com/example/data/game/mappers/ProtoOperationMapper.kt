package com.example.data.game.mappers

import com.example.domain.game.repositories.Operation
import data.game.ProtoOperation

fun Operation.toProtoOperation(): ProtoOperation = ProtoOperation
	.newBuilder()
	.setId(id)
	.setCell(cell.toProtoCell())
	.setOldCell(oldCell.toProtoCell())
	.build()

fun ProtoOperation.toOperation(): Operation = Operation(
	id = id,
	cell = cell.toSudokuCellData(),
	oldCell = oldCell.toSudokuCellData(),
)
