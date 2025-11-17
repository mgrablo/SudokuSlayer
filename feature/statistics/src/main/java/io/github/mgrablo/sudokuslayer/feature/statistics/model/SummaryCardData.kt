package io.github.mgrablo.sudokuslayer.feature.statistics.model

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokuslayer.feature.statistics.R
import io.github.mgrablo.sudokuslayer.feature.statistics.insights.components.SummaryCardStyle
import io.github.mgrablo.sudokuslayer.feature.statistics.insights.components.SummaryCardStyleDefaults
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalSudokuTypography
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.extendedColorScheme

@Stable
data class SummaryCardData(
	val id: String,
	val value: String,
	val label: String,
	val style: SummaryCardStyle,
)

internal object SummaryCardDataProvider {

	@OptIn(ExperimentalMaterial3ExpressiveApi::class)
	@Composable
	fun totalGamesPlayed(value: String) = SummaryCardData(
		id = "totalGamesPlayed",
		value = value,
		label = stringResource(R.string.card_games_played),
		style = SummaryCardStyleDefaults.defaults(
			shape = MaterialShapes.Cookie12Sided.toShape(),
			contentColor = MaterialTheme.colorScheme.onPrimary,
			backgroundColor = MaterialTheme.colorScheme.primary,
		),
	)

	@OptIn(ExperimentalMaterial3ExpressiveApi::class)
	@Composable
	fun totalHintsUsed(value: String) = SummaryCardData(
		id = "totalHintsUsed",
		label = stringResource(R.string.card_hints_used),
		value = value,
		style = SummaryCardStyleDefaults.defaults(
			shape = MaterialShapes.Slanted.toShape(),
			contentColor = MaterialTheme.extendedColorScheme.maroon.onColorContainer,
			backgroundColor = MaterialTheme.extendedColorScheme.maroon.colorContainer,
		),
	)

	@OptIn(ExperimentalMaterial3ExpressiveApi::class)
	@Composable
	fun totalTimeSpent(value: String) = SummaryCardData(
		id = "totalTimeSpent",
		value = value,
		label = stringResource(R.string.card_total_time_spent),
		style = SummaryCardStyleDefaults.defaults(
			contentColor = MaterialTheme.extendedColorScheme.teal.onColorContainer,
			backgroundColor = MaterialTheme.extendedColorScheme.teal.colorContainer,
			valueTextStyle = LocalSudokuTypography.current.displayLargeEmphasized,
		),
	)

	@OptIn(ExperimentalMaterial3ExpressiveApi::class)
	@Composable
	fun avgPlayTime(value: String) = SummaryCardData(
		id = "avgTime",
		label = stringResource(R.string.card_avg_time),
		value = value,
		style = SummaryCardStyleDefaults.defaults(
			shape = RoundedCornerShape(percent = 50),
			backgroundColor = MaterialTheme.extendedColorScheme.rosewater.color,
			contentColor = MaterialTheme.extendedColorScheme.rosewater.onColor,
			valueTextStyle = LocalSudokuTypography.current.displayLargeEmphasized,
		),
	)

	@Composable
	fun slowestGame(value: String) = SummaryCardData(
		id = "slowestGame",
		value = value,
		label = stringResource(R.string.card_slowest_game),
		style = SummaryCardStyleDefaults.defaults(
			backgroundColor = MaterialTheme.extendedColorScheme.lavender.colorContainer,
			contentColor = MaterialTheme.extendedColorScheme.lavender.onColorContainer,
			shape = CutCornerShape(8.dp, 32.dp, 8.dp, 32.dp),
			valueTextStyle = LocalSudokuTypography.current.displayLarge,
		),
	)

	@Composable
	fun fastestGame(value: String) = SummaryCardData(
		id = "fastestGame",
		value = value,
		label = stringResource(R.string.card_fastest_game),
		style = SummaryCardStyleDefaults.defaults(
			backgroundColor = MaterialTheme.extendedColorScheme.peach.colorContainer,
			contentColor = MaterialTheme.extendedColorScheme.peach.onColorContainer,
			shape = CutCornerShape(32.dp, 8.dp, 32.dp, 8.dp),
			valueTextStyle = LocalSudokuTypography.current.displayLarge,
		),
	)

	@OptIn(ExperimentalMaterial3ExpressiveApi::class)
	@Composable
	fun mostPlayedDifficulty(value: String) = SummaryCardData(
		id = "mostPlayedDifficulty",
		value = value,
		label = stringResource(R.string.card_most_played),
		style = SummaryCardStyleDefaults.defaults(
			backgroundColor = MaterialTheme.extendedColorScheme.lavender.colorContainer,
			contentColor = MaterialTheme.extendedColorScheme.lavender.onColorContainer,
			shape = RoundedCornerShape(8.dp, 32.dp, 8.dp, 32.dp),
			valueTextStyle = LocalSudokuTypography.current.displayMediumEmphasized,
		),
	)

	@OptIn(ExperimentalMaterial3ExpressiveApi::class)
	@Composable
	fun mostPlayedGridSize(value: String) = SummaryCardData(
		id = "mostPlayedGridSize",
		value = value,
		label = stringResource(R.string.card_most_played),
		style = SummaryCardStyleDefaults.defaults(
			backgroundColor = MaterialTheme.extendedColorScheme.peach.colorContainer,
			contentColor = MaterialTheme.extendedColorScheme.peach.onColorContainer,
			shape = RoundedCornerShape(32.dp, 8.dp, 32.dp, 8.dp),
			valueTextStyle = LocalSudokuTypography.current.displayMediumEmphasized,
		),
	)
}
