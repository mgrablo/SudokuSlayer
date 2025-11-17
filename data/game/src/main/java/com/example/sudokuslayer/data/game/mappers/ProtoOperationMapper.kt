package com.example.sudokuslayer.data.game.mappers

import com.example.sudokuslayer.domain.core.CellChange
import com.example.sudokuslayer.domain.core.Operation
import data.game.ProtoCellChange
import data.game.ProtoOperation

internal fun Operation.toProtoOperation(): ProtoOperation = ProtoOperation
	.newBuilder()
	.setId(id)
	.addAllChanges(changes.map { it.toProtoCellChange() })
	.build()

internal fun ProtoOperation.toOperation(): Operation = Operation(
	id = id,
	changes = changesList.map { it.toCellChange() },
)

internal fun ProtoCellChange.toCellChange(): CellChange = CellChange(
	oldCell = oldCell.toSudokuCellData(),
	newCell = newCell.toSudokuCellData(),
)

internal fun CellChange.toProtoCellChange(): ProtoCellChange = ProtoCellChange
	.newBuilder()
	.setOldCell(oldCell.toProtoCell())
	.setNewCell(newCell.toProtoCell())
	.build()
