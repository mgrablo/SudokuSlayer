package io.github.mgrablo.sudokuslayer.data.core.database

import app.cash.sqldelight.ColumnAdapter
import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import kotlinx.datetime.LocalDateTime

// GameDifficulty adapter (converts between enum and TEXT)
val gameDifficultyAdapter = object : ColumnAdapter<GameDifficulty, String> {
	override fun decode(databaseValue: String): GameDifficulty = GameDifficulty.valueOf(databaseValue)

	override fun encode(value: GameDifficulty): String = value.name
}

// SudokuGridSize adapter (converts between enum and TEXT)
val sudokuGridSizeAdapter = object : ColumnAdapter<SudokuGridSize, String> {
	override fun decode(databaseValue: String): SudokuGridSize = SudokuGridSize.valueOf(databaseValue)

	override fun encode(value: SudokuGridSize): String = value.name
}

// LocalDateTime adapter (converts between LocalDateTime and TEXT)
val localDateTimeAdapter = object : ColumnAdapter<LocalDateTime, String> {
	override fun decode(databaseValue: String): LocalDateTime = LocalDateTime.parse(databaseValue)

	override fun encode(value: LocalDateTime): String = value.toString()
}
