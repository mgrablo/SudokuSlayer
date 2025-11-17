package io.github.mgrablo.sudokuslayer.feature.statistics.insights.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokuslayer.feature.statistics.R
import io.github.mgrablo.sudokuslayer.feature.statistics.model.InsightsTableColumn
import io.github.mgrablo.sudokuslayer.feature.statistics.model.SortDirection
import io.github.mgrablo.sudokuslayer.feature.statistics.model.SortState
import io.github.mgrablo.sudokuslayer.feature.statistics.model.getDisplayText
import io.github.mgrablo.sudokuslayer.feature.statistics.model.getPreferredWidth
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun TableHeader(
	columns: PersistentList<InsightsTableColumn>,
	sortState: SortState,
	onSortChange: (InsightsTableColumn) -> Unit,
	modifier: Modifier = Modifier,
	scrollStateProvider: () -> ScrollState,
) {
	Row(
		modifier = modifier
			.horizontalScroll(scrollStateProvider())
			.padding(horizontal = LocalPadding.current.small)
			.clip(MaterialTheme.shapes.medium)
			.background(MaterialTheme.colorScheme.surfaceVariant),
		verticalAlignment = Alignment.CenterVertically,
	) {
		columns.forEach { column ->
			HeaderCell(
				column = column,
				sortDirection = if (sortState.column ==
					column
				) {
					sortState.direction
				} else {
					SortDirection.NONE
				},
				onClick = { onSortChange(column) },
			)
		}
		Spacer(Modifier.width(120.dp))
	}
}

@Composable
private fun HeaderCell(
	column: InsightsTableColumn,
	sortDirection: SortDirection,
	onClick: () -> Unit,
) {
	val cellModifier = column.getPreferredWidth()?.let {
		Modifier.width(it)
	} ?: Modifier.wrapContentWidth(unbounded = true)

	Box(
		modifier = cellModifier
			.height(48.dp)
			.clickable(onClick = onClick),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = column.getDisplayText(),
			style = MaterialTheme.typography.titleSmall,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
			textAlign = TextAlign.Center,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
		)
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
					stringResource(R.string.sort_ascending)
				} else {
					stringResource(R.string.sort_descending)
				},
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier
					.align(Alignment.CenterStart)
					.padding(horizontal = LocalPadding.current.tiny),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun TableHeaderPreview() {
	val scrollState = rememberScrollState()
	SudokuSlayerTheme {
		Surface {
			Column {
				TableHeader(
					columns = InsightsTableColumn.ALL.toPersistentList(),
					sortState = SortState(InsightsTableColumn.SolvingTime, SortDirection.DESC),
					onSortChange = { },
					scrollStateProvider = { scrollState },
				)

				TableHeader(
					columns = InsightsTableColumn.ALL.toPersistentList(),
					sortState = SortState(InsightsTableColumn.SolvingTime, SortDirection.DESC),
					onSortChange = { },
					scrollStateProvider = { scrollState },
				)
			}
		}
	}
}
