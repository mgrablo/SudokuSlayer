package com.example.feature.statistics.insights.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.mirrorVertically
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.theme.extendedColorScheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ExpandedSummaryLayout(
	totalGamesPlayed: String,
	totalHintsUsed: String,
	formattedTimeSpent: String,
	formattedSlowest: String,
	formattedFastest: String,
	formattedAvgTime: String,
	mostPlayedDifficulty: String,
	mostPlayedGridSize: String,
	modifier: Modifier = Modifier,
) {
	Column {
		Row(
			horizontalArrangement = Arrangement.spacedBy(
				LocalPadding.current.tiny,
			),
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					horizontal = LocalPadding.current.normal,
				)
				.padding(bottom = LocalPadding.current.tiny),
		) {
			SummaryCard(
				label = "Games played",
				value = totalGamesPlayed,
				modifier = Modifier
					.weight(1f)
					.aspectRatio(1f),
				style = SummaryCardStyleDefaults.defaults(
					shape = MaterialShapes.Cookie12Sided.toShape(),
					contentColor = MaterialTheme.colorScheme.onPrimary,
					backgroundColor = MaterialTheme.colorScheme.primary,
				),
			)
			SummaryCard(
				label = "Hints used",
				value = totalHintsUsed,
				modifier = Modifier
					.weight(1f)
					.aspectRatio(1f),
				style = SummaryCardStyleDefaults.defaults(
					shape = MaterialShapes.Slanted.toShape(),
					contentColor = MaterialTheme.extendedColorScheme.maroon.onColorContainer,
					backgroundColor = MaterialTheme.extendedColorScheme.maroon.colorContainer,
				),
			)
		}
		SummaryCard(
			label = "Total time spent",
			value = formattedTimeSpent,
			modifier = Modifier
				.padding(horizontal = LocalPadding.current.normal)
				.height(150.dp),
			style = SummaryCardStyleDefaults.defaults(
				contentColor = MaterialTheme.colorScheme.onSecondary,
				backgroundColor = MaterialTheme.colorScheme.secondary,
			),
		)
		Row(
			horizontalArrangement = Arrangement.spacedBy(
				LocalPadding.current.tiny,
			),
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					horizontal = LocalPadding.current.normal,
					vertical = LocalPadding.current.tiny,
				),
		) {
			SummaryCard(
				label = "Fastest game",
				value = formattedFastest,
				modifier = Modifier
					.height(120.dp)
					.weight(1f),
				style = SummaryCardStyleDefaults.defaults(
					shape = CutCornerShape(32.dp, 8.dp, 32.dp, 8.dp),
					contentColor = MaterialTheme.extendedColorScheme.peach.onColorContainer,
					backgroundColor = MaterialTheme.extendedColorScheme.peach.colorContainer,
				),
			)
			SummaryCard(
				label = "Slowest game",
				value = formattedSlowest,
				modifier = Modifier
					.height(120.dp)
					.weight(1f),
				style = SummaryCardStyleDefaults.defaults(
					shape = CutCornerShape(8.dp, 32.dp, 8.dp, 32.dp),
					contentColor = MaterialTheme.extendedColorScheme.lavender.onColorContainer,
					backgroundColor = MaterialTheme.extendedColorScheme.lavender.colorContainer,
				),
			)
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(
				LocalPadding.current.tiny,
			),
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					horizontal = LocalPadding.current.normal,
				),
		) {
			SummaryCard(
				label = "Favourite Difficulty",
				value = mostPlayedDifficulty,
				modifier = Modifier
					.height(120.dp)
					.weight(1f),
				style = SummaryCardStyleDefaults.defaults(
					shape = RoundedCornerShape(8.dp, 32.dp, 8.dp, 32.dp),
					contentColor = MaterialTheme.extendedColorScheme.peach.onColorContainer,
					backgroundColor = MaterialTheme.extendedColorScheme.peach.colorContainer,
				),
			)
			SummaryCard(
				label = "Favourite Grid Size",
				value = mostPlayedGridSize,
				modifier = Modifier
					.height(120.dp)
					.weight(1f),
				style = SummaryCardStyleDefaults.defaults(
					shape = RoundedCornerShape(32.dp, 8.dp, 32.dp, 8.dp),
					contentColor = MaterialTheme.extendedColorScheme.lavender.onColorContainer,
					backgroundColor = MaterialTheme.extendedColorScheme.lavender.colorContainer,
				),
			)
		}
		SummaryCard(
			label = "Avg. Time",
			value = formattedAvgTime,
			modifier = Modifier
				.padding(horizontal = LocalPadding.current.normal)
				.padding(vertical = LocalPadding.current.small)
				.height(150.dp),
			style = SummaryCardStyleDefaults.defaults(
				shape = MaterialShapes.Arch.mirrorVertically().toShape(),
				backgroundColor = MaterialTheme.extendedColorScheme.rosewater.color,
				contentColor = MaterialTheme.extendedColorScheme.rosewater.onColor,
			),
		)
	}
}

@PreviewLightDark
@Composable
private fun ExpandedSummaryLayoutPreview() {
	SudokuSlayerTheme {
		Surface {
			ExpandedSummaryLayout(
				totalGamesPlayed = "10",
				totalHintsUsed = "12",
				formattedTimeSpent = rememberFormattedTime(2555f),
				formattedSlowest = rememberFormattedTime(1000f),
				formattedFastest = rememberFormattedTime(9f),
				formattedAvgTime = rememberFormattedTime(300f),
				mostPlayedDifficulty = "Easy",
				mostPlayedGridSize = "4x4",
				modifier = Modifier,
			)
		}
	}
}
