package com.example.feature.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.feature.statistics.StatisticsColumn
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
internal fun TableRow(
	gameResult: GameResult,
	visibleColumns: PersistentSet<StatisticsColumn>,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		visibleColumns.forEach { column ->
			CreateTableCell(column, gameResult)
		}
	}
}

@Composable
private fun RowScope.CreateTableCell(column: StatisticsColumn, gameResult: GameResult) =
	when (column) {
		StatisticsColumn.Date -> TableCell(text = formatDate(gameResult.completedAt), weight = 1f)
		StatisticsColumn.Difficulty -> TableCell(text = gameResult.difficulty.name, weight = 1f)
		StatisticsColumn.Size -> TableCell(
			text = gameResult.gridSize.toText(),
			weight = 1f,
		)
		StatisticsColumn.Time -> TableCell(text = formatTime(gameResult.timeInSeconds), weight = 1f)
		StatisticsColumn.HintsUsed -> TableCell(text = gameResult.hintsUsed.toString(), weight = 1f)
	}

@Composable
private fun RowScope.TableCell(text: String, weight: Float, modifier: Modifier = Modifier) {
	Text(
		text = text,
		style = MaterialTheme.typography.bodyMedium,
		color = MaterialTheme.colorScheme.onSurface,
		textAlign = TextAlign.Center,
		maxLines = 1,
		overflow = TextOverflow.Ellipsis,
		modifier = modifier
			.weight(weight)
			.padding(vertical = 12.dp, horizontal = 4.dp),
	)
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

private fun SudokuGridSize.toText(): String = when (this) {
	SudokuGridSize.FOUR -> "4x4"
	SudokuGridSize.NINE -> "9x9"
	SudokuGridSize.SIXTEEN -> "16x16"
}

@PreviewLightDark
@Composable
private fun TableRowPreview() {
	SudokuSlayerTheme {
		Surface {
			TableRow(
				gameResult = GameResult(
					id = "1",
					timeInSeconds = 124,
					difficulty = GameDifficulty.Easy,
					gridSize = SudokuGridSize.NINE,
					hintsUsed = 4,
					completedAt = LocalDateTime.parse("2010-06-01T22:19:44"),
				),
				visibleColumns = StatisticsColumn.entries.toPersistentSet(),
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}
