package com.example.feature.statistics.insights.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.theme.extendedColorScheme
import com.example.feature.uicore.theme.rememberAnimatedShape
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Stable
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
			modifier = Modifier
				.fillMaxWidth()
				.height(120.dp),
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
							shape = CutCornerShape(8.dp, 32.dp, 8.dp, 32.dp),
						),
					),
					SummaryCardData(
						id = "fastestGame",
						value = formattedFastest,
						label = "Fastest game",
						style = SummaryCardStyleDefaults.defaults(
							backgroundColor = MaterialTheme.extendedColorScheme.peach.colorContainer,
							contentColor = MaterialTheme.extendedColorScheme.peach.onColorContainer,
							shape = RoundedCornerShape(32.dp, 8.dp, 32.dp, 8.dp),
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
	var currentIndex by remember { mutableIntStateOf(0) }
	val summary = remember(currentIndex, summaries) { summaries[currentIndex] }
	if (summaries.all { it.style.shape is CornerBasedShape }) {
		val animatedShape = rememberAnimatedShape(
			currentShape = summary.style.shape.let {
				it as? CornerBasedShape ?: RoundedCornerShape(32.dp)
			},
			animationSpec = tween(300),
		)
		SummaryCard(
			label = summary.label,
			value = summary.value,
			style = summary.style.copy(
				shape = animatedShape,
			),
			modifier = modifier
				.fillMaxSize()
				.clickable(
					onClick = {
						currentIndex = (currentIndex + 1) % summaries.size
					},
				),
		)
	} else {
		AnimatedContent(
			targetState = summary,
			modifier = modifier,
			contentKey = { it.id },
		) { cardData ->
			SummaryCard(
				label = cardData.label,
				value = cardData.value,
				style = cardData.style,
				modifier = Modifier
					.fillMaxSize()
					.clickable(
						onClick = {
							currentIndex = (currentIndex + 1) % summaries.size
						},
					),
			)
		}
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
