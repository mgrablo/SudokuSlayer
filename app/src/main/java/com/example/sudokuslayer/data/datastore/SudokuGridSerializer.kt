package com.example.sudokuslayer.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import sudoku.SudokuGrid
import java.io.InputStream
import java.io.OutputStream

object SudokuGridSerializer : Serializer<SudokuGrid> {
	override val defaultValue: SudokuGrid = SudokuGrid.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): SudokuGrid {
		try {
			return SudokuGrid.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto.", exception)
		}
	}

	override suspend fun writeTo(
		t: SudokuGrid,
		output: OutputStream,
	) {
		t.writeTo(output)
	}
}

val Context.sudokuGridDataStore: DataStore<SudokuGrid> by dataStore(
	fileName = "sudokugrid.db",
	serializer = SudokuGridSerializer,
)
