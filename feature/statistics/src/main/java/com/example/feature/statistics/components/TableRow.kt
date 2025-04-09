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
import com.example.domain.core.SudokuGridSize
import com.example.domain.statistics.FinishedGame
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.datetime.LocalDateTime

@Composable
internal fun TableRow(tableEntry: FinishedGame, modifier: Modifier = Modifier) {
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		// Format time (seconds -> minutes:seconds)
		val minutes = tableEntry.timeInSeconds / 60
		val seconds = tableEntry.timeInSeconds % 60
		val formattedTime = "%d:%02d".format(minutes, seconds)

		// Format date
		val date = tableEntry.completedAt
		val formattedDate = "${date.dayOfMonth}/${date.monthNumber}/${date.year}"

		TableCell(text = formattedDate, weight = 1f)
		TableCell(text = tableEntry.difficulty.name, weight = 1f)
		TableCell(
			text = tableEntry.gridSize.toText(),
			weight = 0.8f,
		)
		TableCell(text = formattedTime, weight = 0.8f)
		TableCell(text = tableEntry.hintsUsed.toString(), weight = 0.6f)
	}
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
				tableEntry =
				FinishedGame(
					id = "1",
					timeInSeconds = 124,
					difficulty = GameDifficulty.Easy,
					gridSize = SudokuGridSize.NINE,
					hintsUsed = 4,
					completedAt = LocalDateTime.parse("2010-06-01T22:19:44"),
				),
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}
