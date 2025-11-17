package com.example.sudokuslayer.feature.statistics.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet

enum class InsightsTableColumn {
	Date,
	Difficulty,
	GridSize,
	SolvingTime,
	HintsUsed,
	;

	companion object {
		val ALL = entries.toPersistentSet()
	}
}

@Composable
fun InsightsTableColumn.getDisplayText(): String = when (this) {
	InsightsTableColumn.Date -> stringResource(R.string.table_header_date)
	InsightsTableColumn.Difficulty -> stringResource(R.string.table_header_difficulty)
	InsightsTableColumn.GridSize -> stringResource(R.string.table_header_size)
	InsightsTableColumn.SolvingTime -> stringResource(R.string.table_header_time)
	InsightsTableColumn.HintsUsed -> stringResource(R.string.table_header_hints)
}

@Composable
fun InsightsTableColumn.getPreferredWidth(): Dp? = when (this) {
	InsightsTableColumn.Date -> 140.dp
	InsightsTableColumn.Difficulty -> 120.dp
	InsightsTableColumn.GridSize -> 100.dp
	InsightsTableColumn.SolvingTime -> 120.dp
	InsightsTableColumn.HintsUsed -> 100.dp
}

data class ColumnDisplayState(val column: InsightsTableColumn, val visible: Boolean) {
	companion object {
		fun getAll() = InsightsTableColumn.ALL.map {
			ColumnDisplayState(
				column = it,
				visible = true,
			)
		}.toPersistentList()
	}
}

internal data class SortState(
	val column: InsightsTableColumn? = null,
	val direction: SortDirection = SortDirection.NONE,
)

internal enum class SortDirection { NONE, ASC, DESC }
