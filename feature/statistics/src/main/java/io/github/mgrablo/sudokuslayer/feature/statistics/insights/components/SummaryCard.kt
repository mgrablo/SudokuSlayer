package io.github.mgrablo.sudokuslayer.feature.statistics.insights.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import io.github.mgrablo.sudokuslayer.feature.statistics.model.SummaryCardData
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.MorphPolygonShape
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.RotatingMorphShape
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SummaryCard(
	data: SummaryCardData,
	modifier: Modifier = Modifier,
	animationSpec: AnimationSpec<Color> = MotionScheme.expressive().fastEffectsSpec(),
	onClick: (() -> Unit)? = null,
	interactionSource: MutableInteractionSource? = null,
) = SummaryCard(
	label = data.label,
	value = data.value,
	style = data.style,
	modifier = modifier,
	animationSpec = animationSpec,
	onClick = onClick,
	interactionSource = interactionSource,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SummaryCard(
	label: String,
	value: String,
	modifier: Modifier = Modifier,
	animationSpec: AnimationSpec<Color> = MotionScheme.expressive().fastEffectsSpec(),
	style: SummaryCardStyle = SummaryCardStyleDefaults.defaults(),
	onClick: (() -> Unit)? = null,
	interactionSource: MutableInteractionSource? = null,
) {
	val animatedBackgroundColor by animateColorAsState(
		targetValue = style.backgroundColor,
		animationSpec = animationSpec,
		label = "SummaryCardBackgroundColorAnimation",
	)
	val animatedContentColor by animateColorAsState(
		targetValue = style.contentColor,
		animationSpec = animationSpec,
		label = "SummaryCardContentColorAnimation",
	)
	if (onClick != null) {
		Card(
			modifier = modifier
				.height(IntrinsicSize.Min),
			onClick = onClick,
			interactionSource = interactionSource,
			shape = style.shape,
			colors = CardDefaults.cardColors(
				containerColor = animatedBackgroundColor,
				contentColor = animatedContentColor,
			),
		) {
			SummaryCardContent(
				label = label,
				value = value,
				style = style,
			)
		}
	} else {
		Card(
			modifier = modifier
				.height(IntrinsicSize.Min),
			shape = style.shape,
			colors = CardDefaults.cardColors(
				containerColor = animatedBackgroundColor,
				contentColor = animatedContentColor,
			),
		) {
			SummaryCardContent(
				label = label,
				value = value,
				style = style,
			)
		}
	}
}

@Composable
private fun SummaryCardContent(label: String, value: String, style: SummaryCardStyle) {
	Column(
		modifier = Modifier
			.fillMaxSize() // Fill the card's bounds
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		Box(
			Modifier
				.weight(0.7f)
				.padding(horizontal = LocalPadding.current.tiny),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = value,
				style = style.valueTextStyle,
				maxLines = 1,
				autoSize = TextAutoSize.StepBased(),
				textAlign = TextAlign.Center,
				color = style.contentColor,
			)
		}
		Box(
			Modifier.weight(0.3f),
		) {
			Text(
				text = label,
				style = style.labelTextStyle,
				textAlign = TextAlign.Center,
				color = style.contentColor.copy(alpha = 0.8f),
			)
		}
	}
}

@Preview(showBackground = true, widthDp = 150, heightDp = 150)
@Composable
private fun SummaryCardPreview_Rounded() {
	SudokuSlayerTheme {
		SummaryCard(
			label = "Games Played",
			value = "127",
			modifier = Modifier.size(140.dp),
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun SummaryCardPreview_Cut() {
	SudokuSlayerTheme {
		SummaryCard(
			label = "Avg. Time",
			value = "5m3s",
			style = SummaryCardStyleDefaults.defaults(
				shape = CutCornerShape(
					topStart = 24.dp,
					bottomEnd = 24.dp,
					topEnd = 8.dp,
					bottomStart = 8.dp,
				),
				backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
				contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
			),
			modifier = Modifier.size(140.dp),
		)
	}
}

@Preview(showBackground = true, widthDp = 150, heightDp = 150)
@Composable
private fun SummaryCardPreview_Circle() {
	SudokuSlayerTheme {
		SummaryCard(
			label = "Win Rate",
			value = "88%",
			modifier = Modifier.size(140.dp),
			style = SummaryCardStyleDefaults.defaults(
				shape = CircleShape,
				backgroundColor = MaterialTheme.colorScheme.primaryContainer,
				contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
			),
		)
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun SummaryCardPreview_Morph() {
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
	val infiniteTransition = rememberInfiniteTransition()
	val animateRotation = infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 360f,
		animationSpec = infiniteRepeatable(
			animation = tween(600, delayMillis = 1000),
			repeatMode = RepeatMode.Restart,
		),
	)
	SudokuSlayerTheme {
		Column {
			Row {
				Card(
					modifier = Modifier
						.weight(1f)
						.aspectRatio(1f)
						.clip(MorphPolygonShape(morph, animatedProgress.value))
						.background(Color.Red)
						.clickable(interactionSource = interactionSource, indication = null) {
						},
				) {
					Text("Hello")
				}
				Card(
					colors = CardDefaults.cardColors(
						containerColor = Color.Red,
					),
					modifier = Modifier
						.weight(1f)
						.aspectRatio(1f)
						.clip(RotatingMorphShape(morph, animatedProgress.value, animateRotation.value))
						.clickable(interactionSource = interactionSource, indication = null) {
						},
				) {
					Text("Hello")
				}
			}
		}
	}
}
