package io.github.mgrablo.sudokuslayer.data.game.mappers

import data.game.ProtoCellChange
import data.game.ProtoOperation
import io.github.mgrablo.sudokuslayer.domain.core.CellChange
import io.github.mgrablo.sudokuslayer.domain.core.Operation

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
