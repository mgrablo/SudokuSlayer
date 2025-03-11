package com.example.data.game.mappers

import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuCellData
import data.game.ProtoCell
import kotlinx.collections.immutable.toPersistentSet

class ProtoCellMapper : Mapper<ProtoCell, SudokuCellData> {
	override operator fun invoke(input: ProtoCell): SudokuCellData =
		SudokuCellData(
			row = input.row,
			col = input.col,
			number = input.number,
			cornerNotes = input.cornerNotesList.toPersistentSet(),
			attributes = input.attributesList.map { mapToDomainAttribute(it) }.toPersistentSet(),
		)

	fun toProtoCell(input: SudokuCellData): ProtoCell =
		ProtoCell
			.newBuilder()
			.setRow(input.row)
			.setCol(input.col)
			.setNumber(input.number)
			.addAllCornerNotes(input.cornerNotes.toList())
			.addAllAttributes(input.attributes.map { mapToProtoAttribute(it) })
			.build()

	private fun mapToDomainAttribute(input: ProtoCell.Attributes): CellAttributes =
		when (input) {
			ProtoCell.Attributes.GENERATED -> CellAttributes.GENERATED
			ProtoCell.Attributes.HINT_REVEALED -> CellAttributes.HINT_REVEALED
			ProtoCell.Attributes.BREAKING_RULE -> CellAttributes.RULE_BREAKING
			ProtoCell.Attributes.UNSPECIFIED -> CellAttributes.UNSPECIFIED
			ProtoCell.Attributes.UNRECOGNIZED -> CellAttributes.UNSPECIFIED
		}

	private fun mapToProtoAttribute(input: CellAttributes): ProtoCell.Attributes =
		when (input) {
			CellAttributes.GENERATED -> ProtoCell.Attributes.GENERATED
			CellAttributes.HINT_REVEALED -> ProtoCell.Attributes.HINT_REVEALED
			CellAttributes.RULE_BREAKING -> ProtoCell.Attributes.BREAKING_RULE
			else -> ProtoCell.Attributes.UNSPECIFIED
		}
}
