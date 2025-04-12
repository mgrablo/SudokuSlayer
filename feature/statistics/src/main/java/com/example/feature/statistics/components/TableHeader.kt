package com.example.feature.statistics.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.feature.statistics.SortDirection
import com.example.feature.statistics.SortState
import com.example.feature.statistics.StatisticsColumn
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet


@Composable
internal fun TableHeader(
	visibleColumns: PersistentSet<StatisticsColumn>,
	sortState: SortState,
	onSortChange: (StatisticsColumn) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		for (column in visibleColumns) {
			HeaderCell(
				text = column.getDisplayText(),
				weight = 1f,
				isActive = sortState.column == column,
				sortDirection = if (sortState.column == column) sortState.direction else SortDirection.NONE,
				onClick = { onSortChange(column) },
			)
		}
	}
}

@Composable
private fun RowScope.HeaderCell(
	text: String,
	weight: Float,
	isActive: Boolean,
	sortDirection: SortDirection,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.weight(weight)
			.height(48.dp)
			.clickable(onClick = onClick)
			.padding(vertical = 12.dp, horizontal = 4.dp),
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
	) {
		if (sortDirection != SortDirection.NONE) {
			Icon(
				imageVector = if (sortDirection ==
					SortDirection.ASC
				) {
					Icons.Default.KeyboardArrowUp
				} else {
					Icons.Default.KeyboardArrowDown
				},
				contentDescription = if (sortDirection ==
					SortDirection.ASC
				) {
					"Sort ascending"
				} else {
					"Sort descending"
				},
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.padding(start = 2.dp),
			)
		}

		Text(
			text = text,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
			textAlign = TextAlign.Center,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
		)
	}
}

@PreviewLightDark
@Composable
private fun TableHeaderPreview() {
	SudokuSlayerTheme {
		Surface {
			Column {
				TableHeader(
					visibleColumns = StatisticsColumn.entries.toPersistentSet(),
					sortState = SortState(StatisticsColumn.Time, SortDirection.DESC),
					onSortChange = { },
					modifier = Modifier.fillMaxWidth(),
				)

				TableHeader(
					visibleColumns = persistentSetOf(
						StatisticsColumn.Difficulty,
						StatisticsColumn.Size,
					),
					sortState = SortState(StatisticsColumn.Time, SortDirection.DESC),
					onSortChange = { },
					modifier = Modifier.fillMaxWidth(),
				)
			}
		}
	}
}
