package com.example.sudokuslayer.feature.statistics.insights.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.feature.statistics.model.SummaryCardDataProvider
import com.example.sudokuslayer.feature.uicore.rememberFormattedTime
import com.example.sudokuslayer.feature.uicore.theme.LocalPadding
import com.example.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme

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
	Column(modifier) {
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
				data = SummaryCardDataProvider.totalGamesPlayed(totalGamesPlayed),
				modifier = Modifier
					.weight(1f)
					.aspectRatio(1f),
			)
			SummaryCard(
				data = SummaryCardDataProvider.totalHintsUsed(totalHintsUsed),
				modifier = Modifier
					.weight(1f)
					.aspectRatio(1f),
			)
		}
		SummaryCard(
			data = SummaryCardDataProvider.totalTimeSpent(formattedTimeSpent),
			modifier = Modifier
				.padding(horizontal = LocalPadding.current.normal)
				.height(150.dp),
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
				data = SummaryCardDataProvider.fastestGame(formattedFastest),
				modifier = Modifier
					.height(120.dp)
					.weight(1f),
			)
			SummaryCard(
				data = SummaryCardDataProvider.slowestGame(formattedSlowest),
				modifier = Modifier
					.height(120.dp)
					.weight(1f),
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
				data = SummaryCardDataProvider.mostPlayedDifficulty(mostPlayedDifficulty),
				modifier = Modifier
					.height(120.dp)
					.weight(1f),
			)
			SummaryCard(
				data = SummaryCardDataProvider.mostPlayedGridSize(mostPlayedGridSize),
				modifier = Modifier
					.height(120.dp)
					.weight(1f),
			)
		}
		SummaryCard(
			data = SummaryCardDataProvider.avgPlayTime(formattedAvgTime),
			modifier = Modifier
				.padding(horizontal = LocalPadding.current.normal)
				.padding(vertical = LocalPadding.current.small)
				.height(150.dp),
		)
	}
}

@PreviewLightDark
@Composable
private fun ExpandedSummaryLayoutPreview() {
	SudokuSlayerTheme {
		Surface {
			ExpandedSummaryLayout(
				totalGamesPlayed = "1000",
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
