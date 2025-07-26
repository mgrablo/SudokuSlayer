package com.example.feature.creator.components.preview

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

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
