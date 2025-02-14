package com.example.sudokuslayer.data.datastore

import androidx.datastore.core.Serializer
import sudoku.SudokuCell
import java.io.InputStream
import java.io.OutputStream

object SudokuCellSerializer : Serializer<SudokuCell> {
	override val defaultValue: SudokuCell = SudokuCell.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): SudokuCell {
		TODO("Not yet implemented")
	}

	override suspend fun writeTo(
		t: SudokuCell,
		output: OutputStream,
	) {
		TODO("Not yet implemented")
	}
}
