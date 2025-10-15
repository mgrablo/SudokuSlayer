package com.example.feature.game.components.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.feature.game.theme.LocalSudokuBoardAppearance
import com.example.feature.game.theme.SudokuBoardAppearance
import com.example.feature.game.theme.SudokuGameTheme
import kotlin.math.sqrt

@Composable
internal fun BoardLoadingIndicator(
	gridSize: Int,
	modifier: Modifier = Modifier,
	boardAppearance: SudokuBoardAppearance = LocalSudokuBoardAppearance.current,
) {
	val numCellsInBlock = remember(gridSize) { sqrt(gridSize.toDouble()).toInt() }
	Box(
		modifier = modifier.drawWithCache {
			val thinLineWidth = boardAppearance.thinLineWidth.toPx()
			val thickLineWidth = boardAppearance.thickLineWidth.toPx()
			val cornerRadius = boardAppearance.cornerRadius.toPx()
			val canvasSize = minOf(size.width, size.height)
			var cellSize = canvasSize / gridSize
			val numThickLines = gridSize / numCellsInBlock + 1
			val numThinLines = gridSize + 1 - numThickLines
			val totalLinesWidth =
				numThickLines * thickLineWidth + numThinLines * thinLineWidth
			val drawableCellArea = (canvasSize - totalLinesWidth) / gridSize
			cellSize = drawableCellArea + totalLinesWidth / gridSize

			val boardClipPath = Path().apply {
				addRoundRect(
					RoundRect(
						Rect(Offset.Zero, Size(canvasSize, canvasSize)),
						CornerRadius(cornerRadius),
					),
				)
			}

			onDrawBehind {
				clipPath(boardClipPath) {
					drawRoundRect(
						color = boardAppearance.colors.defaultBackground,
						cornerRadius = CornerRadius(boardAppearance.cornerRadius.toPx()),
					)
					drawGridLines(
						gridSize = gridSize,
						blockSize = numCellsInBlock,
						canvasWidth = canvasSize,
						cellSize = drawableCellArea,
						thinLineWidth = thinLineWidth,
						thickLineWidth = thickLineWidth,
						thinLineColor = boardAppearance.colors.cellBorder,
						thickLineColor = boardAppearance.colors.blockBorder,
					)
				}
				drawBoardFrame(
					color = boardAppearance.colors.blockBorder,
					canvasWidth = canvasSize,
					strokeWidth = thickLineWidth,
					cornerRadius = cornerRadius,
				)
			}
		},
	)
}

@PreviewLightDark
@Composable
private fun BoardLoadingIndicatorPreview() {
	SudokuGameTheme {
		BoardLoadingIndicator(gridSize = 9, modifier = Modifier.aspectRatio(1f))
	}
}
