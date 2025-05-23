package com.example.feature.statistics.insights.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.theme.extendedColorScheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

data class SummaryCardData(
	val id: String,
	val value: String,
	val label: String,
	val style: SummaryCardStyle,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CompactSummaryLayout(
	totalGamesPlayed: String,
	totalHintsUsed: String,
	formattedTimeSpent: String,
	formattedSlowest: String,
	formattedFastest: String,
	formattedAvgTime: String,
	modifier: Modifier = Modifier,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
		modifier = modifier.padding(horizontal = LocalPadding.current.normal),
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(
				LocalPadding.current.tiny,
			),
			modifier = Modifier.fillMaxWidth(),
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
		SummaryCarousel(
			modifier = Modifier.height(150.dp),
			summaries = persistentListOf(
				SummaryCardData(
					id = "totalTimeSpent",
					value = formattedTimeSpent,
					label = "Total time spent",
					style = SummaryCardStyleDefaults.defaults(
						contentColor = MaterialTheme.colorScheme.onSecondary,
						backgroundColor = MaterialTheme.colorScheme.secondary,
					),
				),
				SummaryCardData(
					id = "avgTime",
					label = "Avg. Time",
					value = formattedAvgTime,
					style = SummaryCardStyleDefaults.defaults(
						shape = RoundedCornerShape(percent = 50),
						backgroundColor = MaterialTheme.extendedColorScheme.rosewater.color,
						contentColor = MaterialTheme.extendedColorScheme.rosewater.onColor,
					),
				),
			),
		)
		Row(
			horizontalArrangement = Arrangement.spacedBy(
				LocalPadding.current.tiny,
			),
			modifier = Modifier.fillMaxWidth().height(120.dp),
		) {
			SummaryCarousel(
				summaries = persistentListOf(
					SummaryCardData(
						id = "slowestGame",
						value = formattedSlowest,
						label = "Slowest game",
						style = SummaryCardStyleDefaults.defaults(
							backgroundColor = MaterialTheme.extendedColorScheme.lavender.colorContainer,
							contentColor = MaterialTheme.extendedColorScheme.lavender.onColorContainer,
							shape = CutCornerShape(16.dp),
						),
					),
					SummaryCardData(
						id = "fastestGame",
						value = formattedFastest,
						label = "Fastest game",
						style = SummaryCardStyleDefaults.defaults(
							backgroundColor = MaterialTheme.extendedColorScheme.peach.colorContainer,
							contentColor = MaterialTheme.extendedColorScheme.peach.onColorContainer,
							shape = CutCornerShape(16.dp),
						),
					),
				),
				modifier = Modifier.weight(1f),
			)
			SummaryCarousel(
				summaries = persistentListOf(
					SummaryCardData(
						id = "slowestGame",
						value = formattedSlowest,
						label = "Slowest game",
						style = SummaryCardStyleDefaults.defaults(
							backgroundColor = MaterialTheme.extendedColorScheme.lavender.colorContainer,
							contentColor = MaterialTheme.extendedColorScheme.lavender.onColorContainer,
							shape = CutCornerShape(16.dp),
						),
					),
					SummaryCardData(
						id = "fastestGame",
						value = formattedFastest,
						label = "Fastest game",
						style = SummaryCardStyleDefaults.defaults(
							backgroundColor = MaterialTheme.extendedColorScheme.peach.colorContainer,
							contentColor = MaterialTheme.extendedColorScheme.peach.onColorContainer,
							shape = CutCornerShape(16.dp),
						),
					),
				),
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Composable
private fun SummaryCarousel(
	summaries: PersistentList<SummaryCardData>,
	modifier: Modifier = Modifier,
) {
	val pagerState = rememberPagerState(initialPage = 0, pageCount = { summaries.size })
	val coroutineScope = rememberCoroutineScope()

	HorizontalPager(
		state = pagerState,
		key = { index -> summaries[index].id },
		userScrollEnabled = false,
		modifier = modifier.clickable(
			onClick = {
				coroutineScope.launch {
					pagerState.scrollToPage(
						(pagerState.currentPage + 1) % pagerState.pageCount,
					)
				}
			},
		),
	) { pageIndex ->
		val summary = summaries[pageIndex]

		SummaryCard(
			label = summary.label,
			value = summary.value,
			style = summary.style,
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@PreviewLightDark
@Composable
private fun CompactSummaryLayoutPreview() {
	SudokuSlayerTheme {
		Surface {
			CompactSummaryLayout(
				totalGamesPlayed = "10",
				totalHintsUsed = "12",
				formattedTimeSpent = rememberFormattedTime(2555f),
				formattedSlowest = rememberFormattedTime(1000f),
				formattedFastest = rememberFormattedTime(9f),
				formattedAvgTime = rememberFormattedTime(300f),
				modifier = Modifier,
			)
		}
	}
}
