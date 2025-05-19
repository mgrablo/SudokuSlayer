package com.example.feature.statistics.insights.components

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
import com.example.feature.statistics.model.InsightsTableColumn
import com.example.feature.statistics.model.SortDirection
import com.example.feature.statistics.model.SortState
import com.example.feature.statistics.model.getDisplayText
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun TableHeader(
	visibleColumns: PersistentList<InsightsTableColumn>,
	sortState: SortState,
	onSortChange: (InsightsTableColumn) -> Unit,
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
					visibleColumns = InsightsTableColumn.ALL.toPersistentList(),
					sortState = SortState(InsightsTableColumn.SolvingTime, SortDirection.DESC),
					onSortChange = { },
					modifier = Modifier.fillMaxWidth(),
				)

				TableHeader(
					visibleColumns = persistentListOf(
						InsightsTableColumn.Difficulty,
						InsightsTableColumn.GridSize,
					),
					sortState = SortState(InsightsTableColumn.SolvingTime, SortDirection.DESC),
					onSortChange = { },
					modifier = Modifier.fillMaxWidth(),
				)
			}
		}
	}
}
