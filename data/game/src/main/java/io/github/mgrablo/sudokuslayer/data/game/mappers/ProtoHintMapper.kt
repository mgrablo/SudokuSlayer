package io.github.mgrablo.sudokuslayer.data.game.mappers

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.Hint.HouseType
import io.github.mgrablo.sudokuslayer.data.game.ProtoHint
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintType
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintType.ProtoHintHouse
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintTypeKt.protoHintHouse
import io.github.mgrablo.sudokuslayer.data.game.protoHint
import io.github.mgrablo.sudokuslayer.data.game.protoHintType
import kotlinx.collections.immutable.toPersistentSet

fun ProtoHint.toHint(): Result<Hint> = when (hintType.type) {
	ProtoHintType.Type.NakedSingle -> {
		Result.success(
			Hint.NakedSingle(
				row = row,
				col = column,
				number = value,
			),
		)
	}

	ProtoHintType.Type.HiddenSingle -> {
		Result.success(
			Hint.HiddenSingle(
				row = row,
				col = column,
				number = value,
				houseType = hintType.house.toHouseType(),
			),
		)
	}

	ProtoHintType.Type.ClaimingCandidate -> {
		val line = if (hasLineHouse()) lineHouse.toHouseType() else hintType.house.toHouseType()
		val block = if (hasBlockHouse()) {
			blockHouse.toHouseType() as HouseType.Block
		} else {
			affectedCellsList.firstOrNull()?.let { cell ->
				// Get block from the first affected cell for backward compatibility
				val blockRow = cell.row / 3
				val blockCol = cell.col / 3
				HouseType.Block(blockRow * 3 + blockCol)
			} ?: HouseType.Block(line.id - 1)
		}

		Result.success(
			Hint.ClaimingCandidate(
				number = value,
				affectedCells = affectedCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
				enforcingCells = enforcingCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
				lineHouseType = line,
				blockHouseType = block,
			),
		)
	}

	ProtoHintType.Type.PointingCandidate -> {
		val line = if (hasLineHouse()) lineHouse.toHouseType() else hintType.house.toHouseType()
		val block = if (hasBlockHouse()) {
			blockHouse.toHouseType() as HouseType.Block
		} else {
			enforcingCellsList.firstOrNull()?.let { cell ->
				// Get block from the first enforcing cell for backward compatibility
				val blockRow = cell.row / 3
				val blockCol = cell.col / 3
				HouseType.Block(blockRow * 3 + blockCol)
			} ?: HouseType.Block(line.id - 1)
		}

		Result.success(
			Hint.PointingCandidate(
				number = value,
				affectedCells = affectedCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
				enforcingCells = enforcingCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
				lineHouseType = line,
				blockHouseType = block,
			),
		)
	}

	ProtoHintType.Type.UNRECOGNIZED,
	ProtoHintType.Type.HintType_UNSPECIFIED,
	->
		Result.failure(
			IllegalArgumentException("Unrecognized or unspecified hint type: ${hintType.type}"),
		)
}

fun Hint.toProtoHint(): ProtoHint = protoHint {
	value = number
	hintType = toProtoHintType()

	when (val h = this@toProtoHint) {
		is Hint.ResolutionHint -> {
			row = h.row
			column = h.col
		}

		is Hint.ClaimingCandidate -> {
			// New elimination hint data classes do not have row/col fields,
			// so using the first affected cell for backward compatibility
			val anchor = h.affectedCells.firstOrNull()
			row = anchor?.row ?: 0
			column = anchor?.col ?: 0

			affectedCells += h.affectedCells.map { it.toProtoCell() }
			enforcingCells += h.enforcingCells.map { it.toProtoCell() }
			lineHouse = h.lineHouseType.toProtoHintHouse()
			blockHouse = h.blockHouseType.toProtoHintHouse()
		}

		is Hint.PointingCandidate -> {
			// New elimination hint data classes do not have row/col fields,
			// so using the first affected cell for backward compatibility
			val anchor = h.affectedCells.firstOrNull()
			row = anchor?.row ?: 0
			column = anchor?.col ?: 0

			affectedCells += h.affectedCells.map { it.toProtoCell() }
			enforcingCells += h.enforcingCells.map { it.toProtoCell() }
			lineHouse = h.lineHouseType.toProtoHintHouse()
			blockHouse = h.blockHouseType.toProtoHintHouse()
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
			is Hint.HiddenSingle -> this@toProtoHintType.houseType.toProtoHintHouse()
			is Hint.EliminationHint -> this@toProtoHintType.houseType.toProtoHintHouse()
			else -> throw IllegalArgumentException("Unknown hint type: $this")
		}
	}
}

fun HouseType.toProtoHintHouse() = protoHintHouse {
	// Proto defaults to 0, so we need to increment it by 1
	id = this@toProtoHintHouse.id + 1
	houseType =
		when (this@toProtoHintHouse) {
			is HouseType.Row -> ProtoHintHouse.Type.Row
			is HouseType.Column -> ProtoHintHouse.Type.Column
			is HouseType.Block -> ProtoHintHouse.Type.Block
		}
}

fun ProtoHintHouse.toHouseType(): HouseType = when (this.houseType) {
	ProtoHintHouse.Type.Row -> HouseType.Row(id - 1)

	ProtoHintHouse.Type.Column -> HouseType.Column(id - 1)

	ProtoHintHouse.Type.Block -> HouseType.Block(id - 1)

	ProtoHintHouse.Type.HouseType_UNSPECIFIED -> throw IllegalArgumentException(
		"House type is unspecified",
	)

	ProtoHintHouse.Type.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized house type")
}
