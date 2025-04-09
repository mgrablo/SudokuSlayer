package com.example.feature.statistics.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.example.feature.uicore.theme.SudokuSlayerTheme

enum class SortDirection { NONE, ASC, DESC }

data class SortState(val column: String = "", val direction: SortDirection = SortDirection.NONE)

@Composable
internal fun TableHeader(
	sortState: SortState,
	onSortChange: (SortState) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		HeaderCell(
			text = "Date",
			weight = 1f,
			isActive = sortState.column == "Date",
			sortDirection = if (sortState.column == "Date") sortState.direction else SortDirection.NONE,
			onClick = {
				onSortChange(
					when {
						sortState.column != "Date" -> SortState("Date", SortDirection.ASC)
						sortState.direction == SortDirection.ASC -> SortState(
							"Date",
							SortDirection.DESC,
						)

						else -> SortState("", SortDirection.NONE)
					},
				)
			},
		)
		HeaderCell(
			text = "Difficulty",
			weight = 1f,
			isActive = sortState.column == "Difficulty",
			sortDirection = if (sortState.column ==
				"Difficulty"
			) {
				sortState.direction
			} else {
				SortDirection.NONE
			},
			onClick = {
				onSortChange(
					when {
						sortState.column != "Difficulty" -> SortState(
							"Difficulty",
							SortDirection.ASC,
						)

						sortState.direction == SortDirection.ASC -> SortState(
							"Difficulty",
							SortDirection.DESC,
						)

						else -> SortState("", SortDirection.NONE)
					},
				)
			},
		)
		HeaderCell(
			text = "Size",
			weight = 0.8f,
			isActive = sortState.column == "Size",
			sortDirection = if (sortState.column == "Size") sortState.direction else SortDirection.NONE,
			onClick = {
				onSortChange(
					when {
						sortState.column != "Size" -> SortState("Size", SortDirection.ASC)
						sortState.direction == SortDirection.ASC -> SortState(
							"Size",
							SortDirection.DESC,
						)

						else -> SortState("", SortDirection.NONE)
					},
				)
			},
		)
		HeaderCell(
			text = "Time",
			weight = 0.8f,
			isActive = sortState.column == "Time",
			sortDirection = if (sortState.column == "Time") sortState.direction else SortDirection.NONE,
			onClick = {
				onSortChange(
					when {
						sortState.column != "Time" -> SortState("Time", SortDirection.ASC)
						sortState.direction == SortDirection.ASC -> SortState(
							"Time",
							SortDirection.DESC,
						)

						else -> SortState("", SortDirection.NONE)
					},
				)
			},
		)
		HeaderCell(
			text = "Hints",
			weight = 0.6f,
			isActive = sortState.column == "Hints",
			sortDirection = if (sortState.column == "Hints") sortState.direction else SortDirection.NONE,
			onClick = {
				onSortChange(
					when {
						sortState.column != "Hints" -> SortState("Hints", SortDirection.ASC)
						sortState.direction == SortDirection.ASC -> SortState(
							"Hints",
							SortDirection.DESC,
						)

						else -> SortState("", SortDirection.NONE)
					},
				)
			},
		)
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
			TableHeader(
				sortState = SortState("Time", SortDirection.DESC),
				onSortChange = {},
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}
