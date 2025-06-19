package com.example.feature.statistics.insights.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import com.example.feature.statistics.model.SummaryCardData
import com.example.feature.statistics.model.SummaryCardDataProvider
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.MorphPolygonShape
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.theme.rememberAnimatedShape
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CompactSummaryLayout(
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
			val shapeA = remember { MaterialShapes.Cookie12Sided }
			val shapeB = remember { MaterialShapes.SoftBurst }
			val morph = remember { Morph(shapeA, shapeB) }
			val interactionSource = remember {
				MutableInteractionSource()
			}
			val isPressed by interactionSource.collectIsPressedAsState()
			val animatedProgress = animateFloatAsState(
				targetValue = if (isPressed) 1f else 0f,
				label = "progress",
				animationSpec = MotionScheme.expressive().fastSpatialSpec(),
			)

			val totalGamesPlayedData =
				SummaryCardDataProvider.totalGamesPlayed(totalGamesPlayed)
			SummaryCard(
				data = totalGamesPlayedData.copy(
					style = totalGamesPlayedData.style.copy(
						shape = RoundedCornerShape(0.dp),
					),
				),
				modifier = Modifier
					.weight(1f)
					.aspectRatio(1f)
					.clip(MorphPolygonShape(morph, animatedProgress.value)),
				onClick = {
				},
				interactionSource = interactionSource,
			)

			SummaryCard(
				data = SummaryCardDataProvider.totalHintsUsed(totalHintsUsed),
				modifier = Modifier
					.weight(1f)
					.aspectRatio(1f),
			)
		}
		SummaryCarousel(
			modifier = Modifier.height(150.dp),
			shapeAnimationSpec = MotionScheme.expressive().defaultSpatialSpec(),
			colorAnimationSpec = MotionScheme.expressive().defaultEffectsSpec(),
			summaries = persistentListOf(
				SummaryCardDataProvider.totalTimeSpent(formattedTimeSpent),
				SummaryCardDataProvider.avgPlayTime(formattedAvgTime),
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
					SummaryCardDataProvider.fastestGame(formattedFastest),
					SummaryCardDataProvider.slowestGame(formattedSlowest),
				),
				modifier = Modifier.weight(1f),
			)
			SummaryCarousel(
				summaries = persistentListOf(
					SummaryCardDataProvider.mostPlayedDifficulty(mostPlayedDifficulty),
					SummaryCardDataProvider.mostPlayedGridSize(mostPlayedGridSize),
				),
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SummaryCarousel(
	summaries: PersistentList<SummaryCardData>,
	modifier: Modifier = Modifier,
	shapeAnimationSpec: FiniteAnimationSpec<Float> = MotionScheme.expressive().fastSpatialSpec(),
	colorAnimationSpec: AnimationSpec<Color> = MotionScheme.expressive().fastEffectsSpec(),
) {
	var currentIndex by remember { mutableIntStateOf(0) }
	val summary = remember(currentIndex, summaries) { summaries[currentIndex] }

	Box(modifier) {
		if (summaries.all { it.style.shape is CornerBasedShape }) {
			val animatedShape = rememberAnimatedShape(
				currentShape = summary.style.shape.let {
					it as? CornerBasedShape ?: RoundedCornerShape(32.dp)
				},
				animationSpec = shapeAnimationSpec,
			)
			SummaryCard(
				label = summary.label,
				value = summary.value,
				animationSpec = colorAnimationSpec,
				style = summary.style.copy(
					shape = animatedShape,
				),
				onClick = {
					currentIndex = (currentIndex + 1) % summaries.size
				},
				modifier = Modifier
					.fillMaxSize(),
			)
		} else {
			AnimatedContent(
				targetState = summary,
				modifier = Modifier,
				contentKey = { it.id },
			) { cardData ->
				SummaryCard(
					label = cardData.label,
					value = cardData.value,
					animationSpec = colorAnimationSpec,
					style = cardData.style,
					onClick = {
						currentIndex = (currentIndex + 1) % summaries.size
					},
					modifier = Modifier
						.fillMaxSize(),
				)
			}
		}
		PageIndicator(
			currentPageIndex = currentIndex,
			pageCount = summaries.size,
			modifier = Modifier.align(Alignment.BottomCenter),
			activeColor = summary.style.contentColor.copy(alpha = 0.8f),
			inactiveColor = summary.style.contentColor.copy(alpha = 0.5f),
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
				mostPlayedDifficulty = "Easy",
				mostPlayedGridSize = "16x16",
				modifier = Modifier,
			)
		}
	}
}
