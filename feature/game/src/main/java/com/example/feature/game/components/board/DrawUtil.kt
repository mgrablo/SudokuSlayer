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
import com.example.feature.game.theme.NOTE_PADDING_FACTOR
import com.example.feature.game.theme.NUMBER_PADDING_FACTOR
import kotlin.math.floor
import kotlin.math.sqrt

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

internal fun DrawScope.drawCell(
	drawState: SudokuCellDrawState,
	textMeasurer: TextMeasurer,
	cellSize: Float,
	gridSize: Int,
) {
	drawRect(
		color = drawState.backgroundColor,
		size = Size(cellSize, cellSize),
	)

	if (drawState.numberText != null) {
		drawNumber(drawState = drawState, textMeasurer = textMeasurer, cellSize = cellSize)
	} else if (drawState.notes.isNotEmpty()) {
		drawNotes(
			drawState = drawState,
			textMeasurer = textMeasurer,
			cellSize = cellSize,
			gridSize = gridSize,
		)
	}
}
