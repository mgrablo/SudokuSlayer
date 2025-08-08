package com.example.sudoku.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

@Stable
data class SudokuCellData(
	val row: Int,
	val col: Int,
	val number: Int = 0,
	val cornerNotes: PersistentSet<Int> = persistentSetOf<Int>(),
	val candidates: PersistentSet<Int> = persistentSetOf<Int>(),
	val attributes: PersistentSet<CellAttributes> = persistentSetOf<CellAttributes>(),
)

enum class CellAttributes {
	UNSPECIFIED,
	SELECTED,
	GENERATED,
	NUMBER_MATCH_HIGHLIGHTED,
	ROW_COLUMN_HIGHLIGHTED,
	RULE_BREAKING,
	HINT_REVEALED,
}
