package io.github.mgrablo.sudokuslayer.data.game.mappers

import io.github.mgrablo.sudokucore.hints.GroupType
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintExplanationFactory
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.hints.HintType.ClaimingCandidate
import io.github.mgrablo.sudokucore.hints.HintType.HiddenSingle
import io.github.mgrablo.sudokucore.hints.HintType.NakedSingle
import io.github.mgrablo.sudokucore.hints.HintType.PointingCandidate
import io.github.mgrablo.sudokuslayer.data.game.ProtoHint
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintType
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintType.ProtoHintHouse
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintTypeKt.protoHintHouse
import io.github.mgrablo.sudokuslayer.data.game.protoHint
import io.github.mgrablo.sudokuslayer.data.game.protoHintType
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
