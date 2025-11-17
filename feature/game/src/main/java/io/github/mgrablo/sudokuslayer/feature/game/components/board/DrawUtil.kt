package io.github.mgrablo.sudokuslayer.feature.game.components.board

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import kotlin.math.floor
import kotlin.math.sqrt

private const val FOCUS_SCALE = 0.92f
private const val FOCUS_CORNER_RADIUS_FACTOR = 0.15f
private const val FOCUS_STROKE_WIDTH_FACTOR = 0.1f
private const val NUMBER_BACKGROUND_CIRCLE_SIZE_FACTOR = 0.8f

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

	for (i in 0..gridSize) {
		val isThickLine = i % blockSize == 0
		val lineWidth = if (isThickLine) thickLineWidth else thinLineWidth
		val lineColor = if (isThickLine) thickLineColor else thinLineColor
		val lineOffset = getLineOffset(i, blockSize, cellSize, thinLineWidth, thickLineWidth)

		drawLine(
			color = lineColor,
			start = Offset(lineOffset, 0f),
			end = Offset(lineOffset, canvasWidth),
			strokeWidth = lineWidth,
		)
		drawLine(
			color = lineColor,
			start = Offset(0f, lineOffset),
			end = Offset(canvasWidth, lineOffset),
			strokeWidth = lineWidth,
		)
	}
}

private fun getLineOffset(
	index: Int,
	blockSize: Int,
	drawableCellArea: Float,
	thinLineWidth: Float,
	thickLineWidth: Float,
): Float {
	val numThickLines = index / blockSize
	val numThinLines = index - numThickLines
	return numThickLines * thickLineWidth + numThinLines * thinLineWidth + index * drawableCellArea +
		thickLineWidth / 2
}

internal fun DrawScope.drawNumber(
	drawState: SudokuCellDrawState,
	textLayoutResult: TextLayoutResult,
	cellSize: Float,
) {
	val center = Offset(cellSize / 2, cellSize / 2)

	if (drawState.numberBackgroundColor != Color.Transparent) {
		drawCircle(
			color = drawState.numberBackgroundColor,
			radius = cellSize / 2f * NUMBER_BACKGROUND_CIRCLE_SIZE_FACTOR,
			center = center,
		)
	}

	drawText(
		textLayoutResult = textLayoutResult,
		color = drawState.numberTextColor,
		topLeft = Offset(
			x = center.x - textLayoutResult.size.width / 2,
			y = center.y - textLayoutResult.size.height / 2,
		),
	)
}

internal fun DrawScope.drawNotes(
	drawState: SudokuCellDrawState,
	textLayoutResults: Map<Int, TextLayoutResult>,
	cellSize: Float,
	gridSize: Int,
) {
	val subgridSize = floor(sqrt(gridSize.toFloat())).toInt()
	val noteSlotSize = cellSize / subgridSize

	drawState.notes.forEach { note ->
		val row = note.index / subgridSize
		val column = note.index % subgridSize
		val textLayoutResult = textLayoutResults[note.value] ?: return@forEach

		val center = Offset(
			x = column * noteSlotSize + (noteSlotSize / 2),
			y = row * noteSlotSize + (noteSlotSize / 2),
		)

		val topLeft = Offset(
			x = center.x - textLayoutResult.size.width / 2,
			y = center.y - textLayoutResult.size.height / 2,
		)

		drawText(
			textLayoutResult = textLayoutResult,
			color = note.color,
			topLeft = topLeft,
		)
	}
}

internal fun DrawScope.drawFocusedBorderAnimation(
	cellSize: Float,
	strokeWidth: Float,
	cornerRadius: Float,
	scale: Float = 1f,
	focusRotationAngle: Float,
	focusGradientColors: List<Color>,
) {
	val brushSize = cellSize * 1.5f
	val borderSize = cellSize * scale
	val offsetValue = (cellSize - borderSize) / 2 + (strokeWidth / 2)
	val offset = Offset(offsetValue, offsetValue)

	val brush = Brush.sweepGradient(
		colors = focusGradientColors,
		center = Offset(cellSize / 2, cellSize / 2),
	)
	val outerPath = Path().apply {
		addRoundRect(
			RoundRect(
				rect = Rect(
					offset = offset,
					size = Size(borderSize - strokeWidth, borderSize - strokeWidth),
				),
				cornerRadius = CornerRadius(cornerRadius, cornerRadius),
			),
		)
	}
	val innerPath = Path().apply {
		addRoundRect(
			RoundRect(
				rect = Rect(
					offset = Offset(
						x = offsetValue + strokeWidth / 2,
						y = offsetValue + strokeWidth / 2,
					),
					size = Size(
						width = borderSize - strokeWidth * 2,
						height = borderSize - strokeWidth * 2,
					),
				),
				cornerRadius = CornerRadius(cornerRadius, cornerRadius),
			),
		)
	}
	val clipPath = outerPath.minus(innerPath)

	clipPath(path = clipPath) {
		rotate(focusRotationAngle, pivot = Offset(cellSize / 2, cellSize / 2)) {
			drawRect(
				brush = brush,
				size = Size(brushSize, brushSize),
				topLeft = Offset(cellSize / 2 - brushSize / 2, cellSize / 2 - brushSize / 2),
			)
		}
	}
}

internal fun DrawScope.drawCell(
	drawState: SudokuCellDrawState,
	numberTextLayoutResult: TextLayoutResult?,
	noteTextLayoutResults: Map<Int, TextLayoutResult>,
	cellSize: Float,
	gridSize: Int,
	focusRotationAngle: Float,
	focusGradientColors: List<Color>,
	shouldDrawContent: Boolean = true,
) {
	drawRect(
		color = drawState.backgroundColor,
		size = Size(cellSize, cellSize),
	)

	if (shouldDrawContent) {
		if (drawState.numberText != null && numberTextLayoutResult != null) {
			drawNumber(
				drawState = drawState,
				textLayoutResult = numberTextLayoutResult,
				cellSize = cellSize,
			)
		} else if (drawState.notes.isNotEmpty()) {
			drawNotes(
				drawState = drawState,
				textLayoutResults = noteTextLayoutResults,
				cellSize = cellSize,
				gridSize = gridSize,
			)
		}
	}

	if (drawState.focusedColor != null) {
		drawFocusedBorderAnimation(
			cellSize = cellSize,
			scale = FOCUS_SCALE,
			strokeWidth = cellSize * FOCUS_STROKE_WIDTH_FACTOR,
			cornerRadius = cellSize * FOCUS_CORNER_RADIUS_FACTOR,
			focusRotationAngle = focusRotationAngle,
			focusGradientColors = focusGradientColors,
		)
	}
}
