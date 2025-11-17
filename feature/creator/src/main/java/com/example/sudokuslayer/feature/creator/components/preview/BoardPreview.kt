package com.example.sudokuslayer.feature.creator.components.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.domain.core.GameDifficulty
import com.example.sudokuslayer.domain.core.SudokuGridSize
import com.example.sudokuslayer.feature.creator.theme.BoardPreviewColors
import com.example.sudokuslayer.feature.creator.theme.LocalBoardPreviewColors
import com.example.sudokuslayer.feature.creator.theme.SudokuCreatorTheme
import kotlin.math.floor
import kotlin.math.sqrt

@Composable
internal fun BoardPreview(
	state: BoardPreviewState,
	modifier: Modifier = Modifier,
	colors: BoardPreviewColors = LocalBoardPreviewColors.current,
) {
// 	val image = ImageBitmap.imageResource(R.drawable.question_mark)
	Box(
		modifier = modifier.drawWithContent {
			val maxWidth = this.size.width
			val previousSize = state.previousSize.toIntSize()
			val currentSize = state.currentSize.toIntSize()
			val progress = state.progress

			val previousCellSize = maxWidth / previousSize
			val currentCellSize = maxWidth / currentSize
			val previousBlockSize = floor(sqrt(previousSize.toFloat())).toInt()
			val currentBlockSize = floor(sqrt(currentSize.toFloat())).toInt()

			val thinLineWidth = 1.dp.toPx()
			val thickLineWidth = 2.dp.toPx()

			drawRoundRect(
				color = colors.background,
				topLeft = Offset.Zero,
				size = Size(maxWidth, maxWidth),
				cornerRadius = CornerRadius(16f, 16f),
			)

			clipRect(left = maxWidth * progress) {
				drawGridLines(
					gridSize = previousSize,
					cellSize = previousCellSize,
					blockSize = previousBlockSize,
					canvasWidth = maxWidth,
					thinLineWidth = thinLineWidth,
					thickLineWidth = thickLineWidth,
					thickLineColor = colors.thickLine,
					thinLineColor = colors.thinLine,
				)
				drawPlaceholderContent(
					placeholderPositions = state.previousPositions,
					cellSize = previousCellSize,
					color = colors.placeholder,
				)
			}

			clipRect(right = maxWidth * progress) {
				drawGridLines(
					gridSize = currentSize,
					cellSize = currentCellSize,
					blockSize = currentBlockSize,
					canvasWidth = maxWidth,
					thinLineWidth = thinLineWidth,
					thickLineWidth = thickLineWidth,
					thickLineColor = colors.thickLine,
					thinLineColor = colors.thinLine,
				)
				drawPlaceholderContent(
					placeholderPositions = state.currentPositions,
					cellSize = currentCellSize,
					color = colors.placeholder,
				)
			}

			drawBoardFrame(
				color = colors.frame,
				canvasWidth = maxWidth,
				strokeWidth = thickLineWidth,
				cornerRadius = 16f,
			)
			drawTransitionSweepEffect(
				progress = progress,
				isRunning = state.isRunning,
				color = colors.frame,
				canvasWidth = maxWidth,
			)
		},
	)
}

@PreviewLightDark
@Composable
private fun BoardPreviewNinePreview() {
	SudokuCreatorTheme {
		BoardPreview(
			state = rememberBoardPreviewState(SudokuGridSize.NINE, GameDifficulty.Medium),
			modifier = Modifier.size(200.dp),
		)
	}
}

@PreviewLightDark
@Composable
private fun BoardPreviewFourPreview() {
	SudokuCreatorTheme {
		BoardPreview(
			state = rememberBoardPreviewState(SudokuGridSize.FOUR, GameDifficulty.Expert),
			modifier = Modifier.size(200.dp),
		)
	}
}

@PreviewLightDark
@Composable
private fun BoardPreviewSixteenPreview() {
	SudokuCreatorTheme {
		BoardPreview(
			state = rememberBoardPreviewState(SudokuGridSize.SIXTEEN, GameDifficulty.Easy),
			modifier = Modifier.size(200.dp),
		)
	}
}
