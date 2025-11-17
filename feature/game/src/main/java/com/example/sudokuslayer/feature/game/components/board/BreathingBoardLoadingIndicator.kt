package com.example.sudokuslayer.feature.game.components.board

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.sudokuslayer.domain.core.SudokuGridSize
import com.example.sudokuslayer.feature.game.theme.LocalSudokuBoardAppearance
import com.example.sudokuslayer.feature.game.theme.SudokuBoardAppearance
import com.example.sudokuslayer.feature.game.theme.SudokuGameTheme
import kotlinx.coroutines.launch
import kotlin.math.sqrt

private const val MAX_SCALE = 1.05f
private const val MIN_SCALE = 0.95f
private const val ANIMATION_DURATION = 500
private const val EXPLOSION_THRESHOLD = 0.01f
private const val EXPLOSION_INITIAL_VALUE = 0.001f

@Composable
internal fun BreathingBoardLoadingIndicator(
	sudokuGridSize: SudokuGridSize,
	modifier: Modifier = Modifier,
	boardAppearance: SudokuBoardAppearance = LocalSudokuBoardAppearance.current,
) {
	val gridSize = remember(sudokuGridSize) { sudokuGridSize.toIntSize() }
	val numCellsInBlock = remember(gridSize) { sqrt(gridSize.toDouble()).toInt() }

	val infiniteTransition = rememberInfiniteTransition(label = "BreathingBoardScale")
	val scale by infiniteTransition.animateFloat(
		initialValue = MIN_SCALE,
		targetValue = MAX_SCALE,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = ANIMATION_DURATION),
			repeatMode = RepeatMode.Reverse,
		),
	)

	val showExplosion by remember { derivedStateOf { scale >= MAX_SCALE - EXPLOSION_THRESHOLD } }
	val explosion = remember { Animatable(EXPLOSION_INITIAL_VALUE) }

	LaunchedEffect(showExplosion) {
		if (showExplosion && !explosion.isRunning) {
			launch {
				explosion.animateTo(
					targetValue = 1f,
					animationSpec = tween(durationMillis = ANIMATION_DURATION),
				)
				explosion.snapTo(EXPLOSION_INITIAL_VALUE)
			}
		}
	}

	Box(
		modifier = modifier
			.aspectRatio(1f)
			.drawWithCache {
				val thinLineWidth = boardAppearance.thinLineWidth.toPx()
				val thickLineWidth = boardAppearance.thickLineWidth.toPx()
				val cornerRadius = boardAppearance.cornerRadius.toPx()
				val canvasSize = minOf(size.width, size.height)
				val numThickLines = gridSize / numCellsInBlock + 1
				val numThinLines = gridSize + 1 - numThickLines
				val totalLinesWidth =
					numThickLines * thickLineWidth + numThinLines * thinLineWidth
				val drawableCellArea = (canvasSize - totalLinesWidth) / gridSize

				val boardClipPath = Path().apply {
					addRoundRect(
						RoundRect(
							Rect(Offset.Zero, Size(canvasSize, canvasSize)),
							CornerRadius(cornerRadius),
						),
					)
				}

				onDrawBehind {
					scale(scale) {
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
					if (showExplosion) {
						val explosionScale = scale + explosion.value * (MAX_SCALE - MIN_SCALE)
						scale(explosionScale) {
							drawBoardFrame(
								color = boardAppearance.colors.blockBorder.copy(alpha = 1f - explosion.value),
								canvasWidth = canvasSize,
								strokeWidth = thickLineWidth,
								cornerRadius = cornerRadius,
							)
						}
					}
				}
			},

	)
}

@PreviewLightDark
@Composable
private fun BreathingBoardLoadingIndicatorPreview() {
	SudokuGameTheme {
		BreathingBoardLoadingIndicator(
			sudokuGridSize = SudokuGridSize.NINE,
			modifier = Modifier.aspectRatio(1f),
		)
	}
}
