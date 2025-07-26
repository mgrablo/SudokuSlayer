package com.example.feature.creator.components.preview

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
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.feature.creator.theme.BoardPreviewColors
import com.example.feature.creator.theme.LocalBoardPreviewColors
import com.example.feature.creator.theme.SudokuCreatorTheme
import kotlin.math.floor
import kotlin.math.sqrt

@Composable
internal fun BoardPreview(
	state: BoardPreviewState,
	difficulty: GameDifficulty,
	modifier: Modifier = Modifier,
	colors: BoardPreviewColors = LocalBoardPreviewColors.current,
) {
	Box(
		modifier = modifier.drawWithContent {
			val maxWidth = this.size.width
			val progress = state.progress
			val previousSize = state.previousSize.toIntSize()
			val currentSize = state.currentSize.toIntSize()

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
			drawBoardFrame(
				color = colors.frame,
				canvasWidth = maxWidth,
				strokeWidth = thickLineWidth,
				cornerRadius = 16f,
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
			}

			drawTransitionSweepEffect(
				progress = progress,
				isRunning = state.isRunning,
				color = colors.frame,
				maxWidth = maxWidth,
			)
		},
	)
}

@PreviewLightDark
@Composable
private fun BoardPreviewNinePreview() {
	SudokuCreatorTheme {
		BoardPreview(
			difficulty = GameDifficulty.Medium,
			state = rememberBoardPreviewState(SudokuGridSize.NINE),
			modifier = Modifier.size(200.dp),
		)
	}
}

@PreviewLightDark
@Composable
private fun BoardPreviewFourPreview() {
	SudokuCreatorTheme {
		BoardPreview(
			state = rememberBoardPreviewState(SudokuGridSize.FOUR),
			difficulty = GameDifficulty.Medium,
			modifier = Modifier.size(200.dp),
		)
	}
}

@PreviewLightDark
@Composable
private fun BoardPreviewSixteenPreview() {
	SudokuCreatorTheme {
		BoardPreview(
			state = rememberBoardPreviewState(SudokuGridSize.SIXTEEN),
			difficulty = GameDifficulty.Medium,
			modifier = Modifier.size(200.dp),
		)
	}
}
