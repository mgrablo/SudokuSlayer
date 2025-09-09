package com.example.feature.game.components.board

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

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

internal fun DrawScope.drawNumber(drawState: SudokuCellDrawState, textMeasurer: TextMeasurer) {
	val textLayoutResult = textMeasurer.measure(
		text = drawState.numberText!!,
		style = TextStyle(
			color = drawState.numberTextColor,
			fontSize = drawState.numberTextSize.sp,
			textAlign = TextAlign.Center,
		),
	)

	val center = Offset(drawState.cellSize / 2, drawState.cellSize / 2)

	if (drawState.numberBackgroundColor != Color.Transparent) {
		drawCircle(
			color = drawState.numberBackgroundColor,
			radius = drawState.cellSize / 2f * 0.8f,
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

internal fun DrawScope.drawNotes(drawState: SudokuCellDrawState, textMeasurer: TextMeasurer) {
	drawState.notes.forEach { note ->
		val textLayoutResult = textMeasurer.measure(
			text = note.text,
			style = TextStyle(color = note.color, fontSize = note.size.sp),
		)

		val topLeft = Offset(
			x = note.offset.x - textLayoutResult.size.width / 2,
			y = note.offset.y - textLayoutResult.size.height / 2,
		)

		drawText(textLayoutResult, topLeft = topLeft)
	}
}

internal fun DrawScope.drawCell(drawState: SudokuCellDrawState, textMeasurer: TextMeasurer) {
	drawRect(
		color = drawState.backgroundColor,
		size = Size(drawState.cellSize, drawState.cellSize),
	)

	if (drawState.numberText != null) {
		drawNumber(drawState, textMeasurer)
	} else if (drawState.notes.isNotEmpty()) {
		drawNotes(drawState, textMeasurer)
	}
}
