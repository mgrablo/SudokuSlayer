package io.github.mgrablo.sudokuslayer.data.game.mappers

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokuslayer.data.game.ProtoHint
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintType
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintType.ProtoHintHouse
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintTypeKt.protoHintHouse
import io.github.mgrablo.sudokuslayer.data.game.protoHint
import io.github.mgrablo.sudokuslayer.data.game.protoHintType
import kotlinx.collections.immutable.toPersistentSet

fun ProtoHint.toHint(): Hint = when (hintType.type) {
	ProtoHintType.Type.NakedSingle -> {
		Hint.NakedSingle(
			row = row,
			col = column,
			number = value,
		)
	}

	ProtoHintType.Type.HiddenSingle -> {
		Hint.HiddenSingle(
			row = row,
			col = column,
			number = value,
			groupType = hintType.house.toGroupType(),
		)
	}

	ProtoHintType.Type.ClaimingCandidate -> {
		Hint.ClaimingCandidate(
			number = value,
			groupType = hintType.house.toGroupType(),
			affectedCells = affectedCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
			enforcingCells = enforcingCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
		)
	}

	ProtoHintType.Type.PointingCandidate -> {
		Hint.PointingCandidate(
			number = value,
			groupType = hintType.house.toGroupType(),
			affectedCells = affectedCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
			enforcingCells = enforcingCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
		)
	}

	else -> throw IllegalArgumentException("Unkown hint type: $hintType")
}

fun Hint.toProtoHint(): ProtoHint = protoHint {
	value = number
	hintType = toProtoHintType()

	when (val h = this@toProtoHint) {
		is Hint.ResolutionHint -> {
			row = h.row
			column = h.col
		}

		is Hint.EliminationHint -> {
			// New elimination hint data classes do not have row/col fields,
			// so using the first affected cell for backward compatibility
			val anchor = h.affectedCells.firstOrNull()
			row = anchor?.row ?: 0
			column = anchor?.col ?: 0

			affectedCells += h.affectedCells.map { it.toProtoCell() }
			enforcingCells += h.enforcingCells.map { it.toProtoCell() }
		}
	}
}

fun Hint.toProtoHintType(): ProtoHintType = protoHintType {
	type =
		when (this@toProtoHintType) {
			is Hint.NakedSingle -> ProtoHintType.Type.NakedSingle
			is Hint.HiddenSingle -> ProtoHintType.Type.HiddenSingle
			is Hint.ClaimingCandidate -> ProtoHintType.Type.ClaimingCandidate
			is Hint.PointingCandidate -> ProtoHintType.Type.PointingCandidate
		}

	if (this@toProtoHintType !is Hint.NakedSingle) {
		house = when (this@toProtoHintType) {
			is Hint.HiddenSingle -> this@toProtoHintType.groupType.toProtoHintHouse()
			is Hint.EliminationHint -> this@toProtoHintType.groupType.toProtoHintHouse()
			else -> throw IllegalArgumentException("Unknown hint type: $this")
		}
	}
}

fun Hint.GroupType.toProtoHintHouse() = protoHintHouse {
	// Proto defaults to 0, so we need to increment it by 1
	id = this@toProtoHintHouse.id + 1
	houseType =
		when (this@toProtoHintHouse) {
			is Hint.GroupType.Row -> ProtoHintHouse.Type.Row
			is Hint.GroupType.Column -> ProtoHintHouse.Type.Column
			is Hint.GroupType.Block -> ProtoHintHouse.Type.Block
		}
}

fun ProtoHintHouse.toGroupType(): Hint.GroupType = when (this.houseType) {
	ProtoHintHouse.Type.Row -> Hint.GroupType.Row(id - 1)

	ProtoHintHouse.Type.Column -> Hint.GroupType.Column(id - 1)

	ProtoHintHouse.Type.Block -> Hint.GroupType.Block(id - 1)

	ProtoHintHouse.Type.HouseType_UNSPECIFIED -> throw IllegalArgumentException(
		"House type is unspecified",
	)

	ProtoHintHouse.Type.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized house type")
}
