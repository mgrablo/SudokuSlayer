package com.example.feature.game.components.board

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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import com.example.feature.game.theme.NOTE_PADDING_FACTOR
import com.example.feature.game.theme.NUMBER_PADDING_FACTOR
import kotlin.math.floor
import kotlin.math.sqrt

private const val FOCUS_SCALE = 0.92f
private const val FOCUS_CORNER_RADIUS_FACTOR = 0.15f
private const val FOCUS_STROKE_WIDTH_FACTOR = 0.1f

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
	textMeasurer: TextMeasurer,
	cellSize: Float,
) {
	val textLayoutResult = textMeasurer.measure(
		text = drawState.numberText!!,
		style = TextStyle(
			color = drawState.numberTextColor,
			fontSize = (cellSize * NUMBER_PADDING_FACTOR).toSp(),
			textAlign = TextAlign.Center,
		),
	)

	val center = Offset(cellSize / 2, cellSize / 2)

	if (drawState.numberBackgroundColor != Color.Transparent) {
		drawCircle(
			color = drawState.numberBackgroundColor,
			radius = cellSize / 2f * 0.8f,
			center = center,
		)
	}

	drawText(
		textLayoutResult = textLayoutResult,
		topLeft = Offset(
			x = center.x - textLayoutResult.size.width / 2,
			y = center.y - textLayoutResult.size.height / 2,
		),
	)
}

internal fun DrawScope.drawNotes(
	drawState: SudokuCellDrawState,
	textMeasurer: TextMeasurer,
	cellSize: Float,
	gridSize: Int,
) {
	val subgridSize = floor(sqrt(gridSize.toFloat())).toInt()
	val noteSlotSize = cellSize / subgridSize
	val noteFontSizeInSp = (noteSlotSize * NOTE_PADDING_FACTOR).toSp()

	drawState.notes.forEach { note ->
		val row = note.index / subgridSize
		val column = note.index % subgridSize

		val center = Offset(
			x = column * noteSlotSize + (noteSlotSize / 2),
			y = row * noteSlotSize + (noteSlotSize / 2),
		)

		val textLayoutResult = textMeasurer.measure(
			text = note.text,
			style = TextStyle(
				color = note.color,
				fontSize = noteFontSizeInSp,
				textAlign = TextAlign.Center,
			),
		)

		val topLeft = Offset(
			x = center.x - textLayoutResult.size.width / 2,
			y = center.y - textLayoutResult.size.height / 2,
		)

		drawText(textLayoutResult, topLeft = topLeft)
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
	textMeasurer: TextMeasurer,
	cellSize: Float,
	gridSize: Int,
	focusRotationAngle: Float,
	focusGradientColors: List<Color>,
) {
	drawRect(
		color = drawState.backgroundColor,
		size = Size(cellSize, cellSize),
	)

	if (drawState.numberText != null) {
		drawNumber(
			drawState = drawState,
			textMeasurer = textMeasurer,
			cellSize = cellSize,
		)
	} else if (drawState.notes.isNotEmpty()) {
		drawNotes(
			drawState = drawState,
			textMeasurer = textMeasurer,
			cellSize = cellSize,
			gridSize = gridSize,
		)
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
