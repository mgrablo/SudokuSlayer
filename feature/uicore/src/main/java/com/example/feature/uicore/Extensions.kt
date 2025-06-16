package com.example.feature.uicore

import androidx.collection.FloatFloatPair
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.graphics.shapes.RoundedPolygon
import com.example.domain.core.GameDifficulty
import com.example.sudokuslayer.feature.uicore.R
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@Composable
fun GameDifficulty.toLocalizedString(): String {
	return when(this) {
		GameDifficulty.Easy -> stringResource(R.string.difficulty_easy)
		GameDifficulty.Medium -> stringResource(R.string.difficulty_medium)
		GameDifficulty.Hard -> stringResource(R.string.difficulty_hard)
		GameDifficulty.Expert -> stringResource(R.string.difficulty_expert)
	}
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
	// It's assumed that R.string.time_less_than_a_second is defined in your resources.
	// If not, you should define it, e.g., <string name="time_less_than_a_second">less than 1s</string>
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
		// Original logic for non-compact minutes: show if hours are present or minutes > 0
		if (minutes > 0 || hours > 0) {
			parts.add("${minutes}m")
		}
	}

	if (compact) {
		if (seconds > 0) {
			parts.add("${seconds}s")
		}
	} else {
		// Original logic for non-compact seconds: show if seconds > 0, or if time is < 1 minute
		if (seconds > 0 || (hours == 0L && minutes == 0L)) {
			parts.add("${seconds}s")
		}
	}

	return if (parts.isEmpty()) {
		// This case should be covered by the initial checks if totalSeconds >= 1f,
		// as at least one part (h, m, or s) would be non-zero.
		// Kept for consistency with original structure or as a fallback.
		stringResource(R.string.time_less_than_a_second)
	} else {
		parts.joinToString(" ")
	}
}

fun RoundedPolygon.mirrorVertically(): RoundedPolygon = this.transformed { x, y ->
	FloatFloatPair(x, 1f - y)
}
