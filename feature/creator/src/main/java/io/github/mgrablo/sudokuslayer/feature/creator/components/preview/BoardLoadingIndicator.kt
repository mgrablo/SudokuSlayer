package io.github.mgrablo.sudokuslayer.feature.creator.components.preview

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.feature.creator.theme.BoardPreviewColors
import io.github.mgrablo.sudokuslayer.feature.creator.theme.LocalBoardPreviewColors
import io.github.mgrablo.sudokuslayer.feature.creator.theme.SudokuCreatorTheme
import kotlin.math.floor
import kotlin.math.sqrt

@Composable
internal fun BoardLoadingIndicator(
	gridSize: SudokuGridSize,
	modifier: Modifier = Modifier,
	colors: BoardPreviewColors = LocalBoardPreviewColors.current,
	animationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
		animation = tween(1800, easing = LinearEasing),
		repeatMode = RepeatMode.Restart,
	),
) {
	val infiniteTransition = rememberInfiniteTransition(label = "game_creation_transition")
	val waveWidth = 4f
	val maxDistance = (gridSize.toIntSize() - 1) * 2f
	val animationStartValue = -(waveWidth / 2f)
	val animationEndValue = maxDistance + (waveWidth / 2f)

	val waveAnimationValue by infiniteTransition.animateFloat(
		initialValue = animationStartValue,
		targetValue = animationEndValue,
		animationSpec = animationSpec,
		label = "game_creation_wave_animation",
	)

	Box(
		modifier.drawWithContent {
			val maxWidth = this.size.width
			val gridSize = gridSize.toIntSize()
			val cellSize = maxWidth / gridSize
			val blockSize = floor(sqrt(gridSize.toFloat())).toInt()

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
			drawGridLines(
				gridSize = gridSize,
				cellSize = cellSize,
				blockSize = blockSize,
				canvasWidth = maxWidth,
				thinLineWidth = thinLineWidth,
				thickLineWidth = thickLineWidth,
				thickLineColor = colors.thickLine,
				thinLineColor = colors.thinLine,
			)
			drawCellIllumination(
				gridSize = gridSize,
				cellSize = cellSize,
				color = colors.frame,
				progress = waveAnimationValue,
				waveWidth = waveWidth,
			)
		},
	)
}

@PreviewLightDark
@Composable
private fun BoardLoadingIndicatorPreview() {
	SudokuCreatorTheme {
		BoardLoadingIndicator(
			gridSize = SudokuGridSize.NINE,
			modifier = Modifier.size(200.dp),
		)
	}
}
