package com.example.feature.uicore.modifiers

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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
