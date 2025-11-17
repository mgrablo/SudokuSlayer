package com.example.sudokuslayer.feature.uicore

import androidx.collection.FloatFloatPair
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.graphics.shapes.RoundedPolygon
import com.composables.core.DialogScope
import com.composeunstyled.LocalModalWindow
import com.example.sudokuslayer.domain.core.GameDifficulty
import com.example.sudokuslayer.domain.core.SudokuGridSize
import com.example.sudokuslayer.feature.uicore.R
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@Composable
fun GameDifficulty.toLocalizedString(): String = when (this) {
	GameDifficulty.Easy -> stringResource(R.string.difficulty_easy)
	GameDifficulty.Medium -> stringResource(R.string.difficulty_medium)
	GameDifficulty.Hard -> stringResource(R.string.difficulty_hard)
	GameDifficulty.Expert -> stringResource(R.string.difficulty_expert)
}

/**
 * Extension function for `Modifier` to consume horizontal drag gestures.
 * This prevents the drag gesture from propagating further if a horizontal drag is detected.
 * It can be useful to fix issues like a navigation drawer opening when dragging other elements (e.g., a slider).
 *
 * @return A `Modifier` that consumes horizontal drag gestures.
 */
fun Modifier.consumeHorizontalDrag(minimalVerticalDragThreshold: Float = 1.0f): Modifier =
	this.pointerInput(Unit) {
		detectDragGestures { change, dragAmount ->
			if (dragAmount.x != 0f && abs(dragAmount.y) < minimalVerticalDragThreshold) {
				change.consume()
			}
		}
	}

/**
 * Formats a Float value representing total seconds into a human-readable time string.
 * Example: 90f -> "1m 30s", 3665f -> "1h 1m 5s"
 *
 * @param totalSeconds The total time in seconds.
 * @param compact If true, tries to omit zero values where it makes sense (e.g., "1h 5s" instead of "1h 0m 5s").
 *                However, for simplicity, this basic version will show "0m" if hours and seconds are present.
 *                A more advanced version could be smarter.
 * @return A formatted time string.
 */
@Composable
fun rememberFormattedTime(totalSeconds: Float, compact: Boolean = false): String {
	if (totalSeconds < 0) return stringResource(R.string.time_less_than_a_second)
	if (totalSeconds < 1f && totalSeconds >= 0) return stringResource(R.string.time_less_than_a_second)

	val secondsLong = totalSeconds.toLong()

	val hours = TimeUnit.SECONDS.toHours(secondsLong)
	val minutes = TimeUnit.SECONDS.toMinutes(secondsLong) % 60
	val seconds = secondsLong % 60

	val parts = mutableListOf<String>()

	if (hours > 0) {
		parts.add("${hours}h")
	}

	if (compact) {
		if (minutes > 0) {
			parts.add("${minutes}m")
		}
	} else {
		if (minutes > 0 || hours > 0) {
			parts.add("${minutes}m")
		}
	}

	if (compact) {
		if (seconds > 0) {
			parts.add("${seconds}s")
		}
	} else {
		if (seconds > 0 || (hours == 0L && minutes == 0L)) {
			parts.add("${seconds}s")
		}
	}

	return if (parts.isEmpty()) {
		stringResource(R.string.time_less_than_a_second)
	} else {
		parts.joinToString(" ")
	}
}

fun RoundedPolygon.mirrorVertically(): RoundedPolygon = this.transformed { x, y ->
	FloatFloatPair(x, 1f - y)
}

@Composable
fun SudokuGridSize.toLocalizedString(): String = when (this) {
	SudokuGridSize.FOUR -> stringResource(R.string.gridsize_4x4)
	SudokuGridSize.NINE -> stringResource(R.string.gridsize_9x9)
	SudokuGridSize.SIXTEEN -> stringResource(R.string.gridsize_16x16)
}

@Composable
fun DialogScope.HideSystemBars() {
	val view = LocalView.current
	val window = LocalModalWindow.current
	val insertsController = WindowCompat.getInsetsController(window, view)
	if (!view.isInEditMode) {
		insertsController.apply {
			hide(WindowInsetsCompat.Type.systemBars())
			systemBarsBehavior =
				WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
		}
	}
}

fun Color.darken(factor: Float = 0.1f): Color {
	val hsl = FloatArray(3)
	ColorUtils.colorToHSL(this.toArgb(), hsl)
	hsl[2] -= factor // Decrease Luminance
	hsl[2] = hsl[2].coerceIn(0f, 1f) // Clamp between 0 and 1
	return Color(ColorUtils.HSLToColor(hsl))
}

fun Color.lighten(factor: Float = 0.1f): Color {
	val hsl = FloatArray(3)
	ColorUtils.colorToHSL(this.toArgb(), hsl)
	hsl[2] += factor // Increase Luminance
	hsl[2] = hsl[2].coerceIn(0f, 1f) // Clamp between 0 and 1
	return Color(ColorUtils.HSLToColor(hsl))
}
