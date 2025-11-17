package com.example.sudokuslayer.feature.uicore.modifiers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.breathingBorder(isActive: Boolean, focusedColor: Color, shape: Shape): Modifier =
	composed {
		if (!isActive) return@composed this
		val infiniteTransition = rememberInfiniteTransition("breathingEffect")
		val borderWidth by infiniteTransition.animateFloat(
			initialValue = 1.8f,
			targetValue = 4f,
			animationSpec = infiniteRepeatable(
				animation = tween(750, easing = LinearEasing),
				repeatMode = RepeatMode.Reverse,
			),
			label = "borderWidth",
		)
		val borderAlpha by infiniteTransition.animateFloat(
			initialValue = 0.5f,
			targetValue = 1f,
			animationSpec = infiniteRepeatable(
				animation = tween(750, easing = LinearEasing),
				repeatMode = RepeatMode.Reverse,
			),
			label = "borderAlpha",
		)
		Modifier.border(
			width = borderWidth.dp,
			color = focusedColor.copy(alpha = borderAlpha),
			shape = shape,
		)
	}

@Composable
fun rememberShimmerBrush(
	targetColor: Color,
	baseColor: Color,
	showShimmer: Boolean = true,
	shimmerWidth: Float = 500f,
	animationDuration: Int = 100,
): Brush {
	val shimmerTranslate = remember { Animatable(0f) }

	val colorStops = remember(targetColor) {
		listOf(
			baseColor,
			targetColor.copy(alpha = 0.75f),
			targetColor,
			targetColor.copy(alpha = 0.75f),
			baseColor,
			targetColor.copy(alpha = 0.75f),
			targetColor,
		)
	}

	LaunchedEffect(showShimmer) {
		if (showShimmer) {
			shimmerTranslate.animateTo(
				targetValue = shimmerWidth,
				animationSpec = tween(
					durationMillis = animationDuration,
					easing = LinearEasing,
				),
			)
		}
	}

	return remember(shimmerTranslate.value) {
		Brush.linearGradient(
			colors = colorStops,
			start = Offset(x = shimmerTranslate.value - (shimmerWidth / 2), y = 0f),
			end = Offset(x = shimmerTranslate.value + (shimmerWidth / 2), y = 0f),
		)
	}
}
