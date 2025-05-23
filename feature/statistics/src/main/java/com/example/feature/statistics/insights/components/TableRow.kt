package com.example.feature.statistics.insights.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.feature.statistics.model.InsightsTableColumn
import com.example.feature.statistics.model.getPreferredWidth
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
internal fun TableRow(
	gameResult: GameResult,
	visibleColumns: PersistentList<InsightsTableColumn>,
	scrollStateProvider: () -> ScrollState,
	onCopySeedClick: (Long) -> Unit,
	onPlayClick: (Long) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.horizontalScroll(scrollStateProvider())
			.padding(horizontal = LocalPadding.current.tiny),
		verticalAlignment = Alignment.CenterVertically,
	) {
		visibleColumns.forEach { column ->
			CreateTableCell(column, gameResult)
		}
		Row(
			modifier = Modifier.width(120.dp),
			horizontalArrangement = Arrangement.Center,
		) {
			gameResult.seed?.let {
				IconButton(
					onClick = { onPlayClick(it) },
				) {
					Icon(
						Icons.Default.PlayArrow,
						contentDescription = stringResource(
							R.string.play_again_content_description,
						),
					)
				}
				IconButton(
					onClick = { onCopySeedClick(it) },
				) {
					Icon(
						painter = painterResource(com.example.sudokuslayer.feature.uicore.R.drawable.content_copy),
						contentDescription = stringResource(R.string.copy_seed_content_description),
					)
				}
			}
		}
	}
}

@Composable
private fun CreateTableCell(column: InsightsTableColumn, gameResult: GameResult) = when (column) {
	InsightsTableColumn.Date -> TableCell(
		text = formatDate(gameResult.completionDate),
		column = column,
	)

	InsightsTableColumn.Difficulty -> TableCell(
		text = gameResult.difficulty.name,
		column = column,
	)

	InsightsTableColumn.GridSize -> TableCell(
		text = gameResult.gridSize.toText(),
		column = column,
	)

	InsightsTableColumn.SolvingTime -> TableCell(
		text = formatTime(gameResult.timeInSeconds),
		column = column,
	)

	InsightsTableColumn.HintsUsed -> TableCell(
		text = gameResult.hintsUsed.toString(),
		column = column,
	)
}

@Composable
private fun TableCell(text: String, column: InsightsTableColumn) {
	val cellModifier = column.getPreferredWidth()?.let {
		Modifier.width(it)
	} ?: Modifier.wrapContentWidth(unbounded = true)

	Box(
		modifier = cellModifier.heightIn(48.dp),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurface,
			textAlign = TextAlign.Center,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			modifier = cellModifier,
		)
	}
}

private fun formatTime(totalSeconds: Long): String {
	val minutes = totalSeconds / 60
	val seconds = totalSeconds % 60
	return "%d:%02d".format(minutes, seconds)
}

@OptIn(FormatStringsInDatetimeFormats::class)
private fun formatDate(date: LocalDateTime) = date.format(
	LocalDateTime.Format {
		byUnicodePattern("yyyy-MM-dd")
	},
)

@Composable
internal fun SudokuGridSize.toText(): String = when (this) {
	SudokuGridSize.FOUR -> stringResource(R.string.gridsize_4x4)
	SudokuGridSize.NINE -> stringResource(R.string.gridsize_9x9)
	SudokuGridSize.SIXTEEN -> stringResource(R.string.gridsize_16x16)
}

@PreviewLightDark
@Composable
private fun TableRowPreview() {
	val scrollState = rememberScrollState()
	SudokuSlayerTheme {
		Surface {
			TableRow(
				gameResult = GameResult(
					id = "1",
					timeInSeconds = 124,
					difficulty = GameDifficulty.Easy,
					gridSize = SudokuGridSize.NINE,
					hintsUsed = 4,
					completionDate = LocalDateTime.parse("2010-06-01T22:19:44"),
					seed = 1,
				),
				visibleColumns = InsightsTableColumn.ALL.toPersistentList(),
				scrollStateProvider = { scrollState },
				onCopySeedClick = { },
				onPlayClick = { },
			)
		}
	}
}
