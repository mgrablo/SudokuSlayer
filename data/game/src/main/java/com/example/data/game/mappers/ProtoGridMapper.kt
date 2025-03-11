package com.example.data.game.mappers

import com.example.sudoku.model.SudokuGrid
import data.game.ProtoGrid

class ProtoGridMapper(
	private val protoCellMapper: ProtoCellMapper = ProtoCellMapper(),
) : Mapper<ProtoGrid, SudokuGrid> {
	override fun invoke(input: ProtoGrid): SudokuGrid =
		SudokuGrid
			.fromCellData(input.cellList.map { protoCellMapper(it) })
			.withSeed(input.seed)

	fun toProtoGrid(input: SudokuGrid): ProtoGrid =
		ProtoGrid
			.newBuilder()
			.addAllCell(input.getArray().map { protoCellMapper.toProtoCell(it) })
			.setSeed(input.seed ?: 0)
			.build()
}
