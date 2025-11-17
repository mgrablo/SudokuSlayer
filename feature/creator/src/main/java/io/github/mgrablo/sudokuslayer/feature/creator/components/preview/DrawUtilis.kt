package io.github.mgrablo.sudokuslayer.feature.creator.components.preview

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.abs

internal fun DrawScope.drawGridLines(
	gridSize: Int,
	blockSize: Int,
	canvasWidth: Float,
	cellSize: Float,
	thinLineWidth: Float,
	thickLineWidth: Float,
	thickLineColor: Color,
	thinLineColor: Color,
) {
	if (gridSize <= 1) return

	for (i in 1 until gridSize) {
		val isThickLine = i % blockSize == 0
		drawLine(
			color = if (isThickLine) thickLineColor else thinLineColor,
			start = Offset(cellSize * i.toFloat(), 0f),
			end = Offset(cellSize * i.toFloat(), canvasWidth),
			strokeWidth = if (isThickLine) thickLineWidth else thinLineWidth,
		)
	}
	for (i in 1 until gridSize) {
		val isThickLine = i % blockSize == 0
		if (canvasWidth >= cellSize * i) {
			drawLine(
				color = if (isThickLine) thickLineColor else thinLineColor,
				start = Offset(0f, cellSize * i.toFloat()),
				end = Offset(canvasWidth, cellSize * i.toFloat()),
				strokeWidth = if (isThickLine) thickLineWidth else thinLineWidth,
			)
		}
	}
}

internal fun DrawScope.drawBoardFrame(
	color: Color,
	canvasWidth: Float,
	strokeWidth: Float,
	cornerRadius: Float,
) {
	drawRoundRect(
		color = color,
		topLeft = Offset.Zero,
		size = Size(canvasWidth, canvasWidth),
		cornerRadius = CornerRadius(cornerRadius, cornerRadius),
		style = Stroke(width = strokeWidth),
	)
}

internal fun DrawScope.drawTransitionSweepEffect(
	progress: Float,
	isRunning: Boolean,
	color: Color,
	canvasWidth: Float,
) {
	if (isRunning) {
		val glowWidth = 20.dp.toPx()

		val sweepBrush = Brush.horizontalGradient(
			colorStops = arrayOf(
				0.0f to Color.Transparent,
				0.3f to color.copy(alpha = 0.05f),
				0.6f to color.copy(alpha = 0.2f),
				0.8f to color.copy(alpha = 0.4f),
				0.9f to color.copy(alpha = 0.2f),
				1.0f to Color.Transparent,
			),
			startX = (canvasWidth * progress) - glowWidth,
			endX = (canvasWidth * progress) + (glowWidth * 0.6f),
		)

		drawRect(
			brush = sweepBrush,
			topLeft = Offset(x = 0f, y = 0f),
			size = Size(width = canvasWidth, height = size.height),
		)

		drawLine(
			color = color.copy(alpha = 0.7f),
			start = Offset(x = canvasWidth * progress, y = 0f),
			end = Offset(x = canvasWidth * progress, y = size.height),
			strokeWidth = 1.dp.toPx(),
			alpha = 0.5f,
		)
	}
}

internal fun DrawScope.drawCellIllumination(
	gridSize: Int,
	cellSize: Float,
	color: Color,
	progress: Float,
	waveWidth: Float,
) {
	// Loop through all cells to calculate and draw their illumination
	for (row in 0 until gridSize) {
		for (col in 0 until gridSize) {
			// The "distance" of the cell from the top-left corner (row + col)
			val cellDistance = (row + col).toFloat()

			// How far this cell is from the center of the wave
			val distanceFromWave = abs(cellDistance - progress)

			// If the cell is within the wave's width, calculate its alpha
			if (distanceFromWave < waveWidth / 2f) {
				// Normalize the distance to a value between 0.0 (edge of wave) and 1.0 (center)
				val normalizedDistance = 1f - (distanceFromWave / (waveWidth / 2f))

				// Use a non-linear easing to make the fade look nicer (fade in fast, fade out slow)
				val alpha = CubicBezierEasing(0.2f, 0f, 0f, 1f).transform(normalizedDistance) * 0.4f

				if (alpha > 0f) {
					drawRect(
						color = color,
						alpha = alpha,
						topLeft = Offset(x = col * cellSize, y = row * cellSize),
						size = Size(cellSize, cellSize),
					)
				}
			}
		}
	}
}

internal fun DrawScope.drawPlaceholderContent(
	placeholderPositions: List<Pair<Int, Int>>,
	cellSize: Float,
	color: Color,
) {
	val iconSizeRatio = 0.6f
	val iconSize = (cellSize * iconSizeRatio).toInt()

	val offset = (cellSize - iconSize) / 2

	for ((row, col) in placeholderPositions) {
		val cellTopLeftX = col * cellSize
		val cellTopLeftY = row * cellSize

		val iconTopLeftX = (cellTopLeftX + offset)
		val iconTopLeftY = (cellTopLeftY + offset)

		drawRect(
			color = color,
			topLeft = Offset(iconTopLeftX, iconTopLeftY),
			size = Size(iconSize.toFloat(), iconSize.toFloat()),
		)
	}
}
