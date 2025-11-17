package com.example.sudokuslayer.data.game.mappers

import com.example.sudoku.solver.GroupType
import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintExplanationFactory
import com.example.sudoku.solver.HintType
import com.example.sudoku.solver.HintType.ClaimingCandidate
import com.example.sudoku.solver.HintType.HiddenSingle
import com.example.sudoku.solver.HintType.NakedSingle
import com.example.sudoku.solver.HintType.PointingCandidate
import data.game.ProtoHint
import data.game.ProtoHintType
import data.game.ProtoHintType.ProtoHintHouse
import data.game.ProtoHintTypeKt.protoHintHouse
import data.game.protoHint
import data.game.protoHintType
import kotlinx.collections.immutable.toPersistentSet

fun ProtoHint.toHint(): Hint {
	val hintType = this.hintType.toHintType()
	return Hint(
		row = row,
		col = column,
		value = value,
		type = hintType,
		explanationStrategy = HintExplanationFactory.createStrategyFor(hintType),
		additionalInfo = additionalInfo,
		affectedCells = affectedCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
		enforcingCells = enforcingCellsList.map { it.toSudokuCellData() }.toPersistentSet(),
	)
}

fun Hint.toProtoHint(): ProtoHint = protoHint {
	row = this@toProtoHint.row
	column = this@toProtoHint.col
	value = this@toProtoHint.value
	hintType = this@toProtoHint.type.toProtoHintType()
	enforcingCells += this@toProtoHint.enforcingCells.map { it.toProtoCell() }
	affectedCells += this@toProtoHint.affectedCells.map { it.toProtoCell() }
}

fun ProtoHintType.toHintType(): HintType = when (this.type) {
	ProtoHintType.Type.NakedSingle -> NakedSingle
	ProtoHintType.Type.HiddenSingle -> HiddenSingle(this.house.toGroupType())
	ProtoHintType.Type.ClaimingCandidate -> ClaimingCandidate(this.house.toGroupType())
	ProtoHintType.Type.PointingCandidate -> PointingCandidate(this.house.toGroupType())
	ProtoHintType.Type.HintType_UNSPECIFIED -> throw IllegalArgumentException(
		"Hint type is unspecified",
	)
	ProtoHintType.Type.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized hint type")
	else -> throw IllegalArgumentException("Unknown hint type: $this")
}

fun HintType.toProtoHintType(): ProtoHintType = protoHintType {
	type =
		when (this@toProtoHintType) {
			is NakedSingle -> ProtoHintType.Type.NakedSingle
			is HiddenSingle -> ProtoHintType.Type.HiddenSingle
			is ClaimingCandidate -> ProtoHintType.Type.ClaimingCandidate
			is PointingCandidate -> ProtoHintType.Type.PointingCandidate
		}
	if (this@toProtoHintType !is NakedSingle) {
		house =
			when (this@toProtoHintType) {
				is HiddenSingle -> this@toProtoHintType.groupType.toProtoHintHouse()
				is ClaimingCandidate -> this@toProtoHintType.groupType.toProtoHintHouse()
				is PointingCandidate -> this@toProtoHintType.groupType.toProtoHintHouse()
				else -> throw IllegalArgumentException("Unknown hint type: $this")
			}
	} else {
		clearHouse()
	}
}

fun GroupType.toProtoHintHouse() = protoHintHouse {
	// Proto defaults to 0, so we need to increment it by 1
	id = this@toProtoHintHouse.id + 1
	houseType =
		when (this@toProtoHintHouse) {
			is GroupType.Row -> ProtoHintHouse.Type.Row
			is GroupType.Column -> ProtoHintHouse.Type.Column
			is GroupType.Block -> ProtoHintHouse.Type.Block
		}
}

fun ProtoHintHouse.toGroupType(): GroupType = when (this.houseType) {
	ProtoHintHouse.Type.Row -> GroupType.Row(id - 1)
	ProtoHintHouse.Type.Column -> GroupType.Column(id - 1)
	ProtoHintHouse.Type.Block -> GroupType.Block(id - 1)
	ProtoHintHouse.Type.HouseType_UNSPECIFIED -> throw IllegalArgumentException(
		"House type is unspecified",
	)
	ProtoHintHouse.Type.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized house type")
}
