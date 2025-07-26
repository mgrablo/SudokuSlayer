package com.example.feature.creator.components.preview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
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
	size: SudokuGridSize,
	difficulty: GameDifficulty,
	modifier: Modifier = Modifier,
	colors: BoardPreviewColors = LocalBoardPreviewColors.current,
) {
	val size = when (size) {
		SudokuGridSize.FOUR -> 4
		SudokuGridSize.NINE -> 9
		SudokuGridSize.SIXTEEN -> 16
	}
	val blockSize by remember(size) { mutableIntStateOf(floor(sqrt(size.toFloat())).toInt()) }

	BoxWithConstraints(
		modifier.aspectRatio(1f),
	) {
		val maxWidth = constraints.maxWidth.toFloat()
		val cellSize by remember(size) { mutableFloatStateOf(maxWidth / size) }

		val thinLineWidth = with(LocalDensity.current) { 1.dp.toPx() }
		val thickLineWidth = with(LocalDensity.current) { 2.dp.toPx() }

		Canvas(
			modifier = Modifier.fillMaxSize(),
		) {
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
			drawGridLines(
				gridSize = size,
				cellSize = cellSize,
				blockSize = blockSize,
				canvasWidth = maxWidth,
				thinLineWidth = thinLineWidth,
				thickLineWidth = thickLineWidth,
				thickLineColor = colors.thickLine,
				thinLineColor = colors.thinLine,
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun BoardPreviewNinePreview() {
	SudokuCreatorTheme {
		BoardPreview(
			size = SudokuGridSize.NINE,
			difficulty = GameDifficulty.Medium,
		)
	}
}

@PreviewLightDark
@Composable
private fun BoardPreviewFourPreview() {
	SudokuCreatorTheme {
		BoardPreview(
			size = SudokuGridSize.FOUR,
			difficulty = GameDifficulty.Medium,
		)
	}
}

@PreviewLightDark
@Composable
private fun BoardPreviewSixteenPreview() {
	SudokuCreatorTheme {
		BoardPreview(
			size = SudokuGridSize.SIXTEEN,
			difficulty = GameDifficulty.Medium,
		)
	}
}
