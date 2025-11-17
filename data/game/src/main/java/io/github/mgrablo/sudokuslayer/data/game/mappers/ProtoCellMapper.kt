package io.github.mgrablo.sudokuslayer.data.game.mappers

import data.game.ProtoCell
import data.game.protoCell
import io.github.mgrablo.sudokucore.model.CellAttributes
import io.github.mgrablo.sudokucore.model.SudokuCellData
import kotlinx.collections.immutable.toPersistentSet

fun ProtoCell.toSudokuCellData(): io.github.mgrablo.sudokucore.model.SudokuCellData =
	SudokuCellData(
		row = row,
		col = col,
		number = number,
		cornerNotes = cornerNotesList.toPersistentSet(),
		candidates = candidatesList.toPersistentSet(),
		attributes = attributesList.map { it.toCellAttributes() }.toPersistentSet(),
	)

fun ProtoCell.Attributes.toCellAttributes(): CellAttributes = when (this) {
	ProtoCell.Attributes.GENERATED -> CellAttributes.GENERATED
	ProtoCell.Attributes.HINT_REVEALED -> CellAttributes.HINT_REVEALED
	ProtoCell.Attributes.BREAKING_RULE -> CellAttributes.RULE_BREAKING
	ProtoCell.Attributes.SELECTED -> CellAttributes.SELECTED
	ProtoCell.Attributes.NUMBER_MATCH_HIGHLIGHTED -> CellAttributes.NUMBER_MATCH_HIGHLIGHTED
	ProtoCell.Attributes.ROW_COLUMN_HIGHLIGHTED -> CellAttributes.ROW_COLUMN_HIGHLIGHTED
	ProtoCell.Attributes.SOLUTION_CONFLICT -> CellAttributes.SOLUTION_CONFLICT
	ProtoCell.Attributes.UNSPECIFIED -> CellAttributes.UNSPECIFIED
	ProtoCell.Attributes.UNRECOGNIZED -> CellAttributes.UNSPECIFIED
}

fun SudokuCellData.toProtoCell(): ProtoCell = protoCell {
	row = this@toProtoCell.row
	col = this@toProtoCell.col
	number = this@toProtoCell.number
	candidates += this@toProtoCell.candidates
	cornerNotes += this@toProtoCell.cornerNotes
	attributes += this@toProtoCell.attributes.map { it.toProtoCellAttribute() }
}

fun CellAttributes.toProtoCellAttribute(): ProtoCell.Attributes = when (this) {
	CellAttributes.GENERATED -> ProtoCell.Attributes.GENERATED
	CellAttributes.HINT_REVEALED -> ProtoCell.Attributes.HINT_REVEALED
	CellAttributes.RULE_BREAKING -> ProtoCell.Attributes.BREAKING_RULE
	CellAttributes.SELECTED -> ProtoCell.Attributes.SELECTED
	CellAttributes.NUMBER_MATCH_HIGHLIGHTED -> ProtoCell.Attributes.NUMBER_MATCH_HIGHLIGHTED
	CellAttributes.ROW_COLUMN_HIGHLIGHTED -> ProtoCell.Attributes.ROW_COLUMN_HIGHLIGHTED
	CellAttributes.SOLUTION_CONFLICT -> ProtoCell.Attributes.SOLUTION_CONFLICT
	CellAttributes.UNSPECIFIED -> ProtoCell.Attributes.UNSPECIFIED
}
