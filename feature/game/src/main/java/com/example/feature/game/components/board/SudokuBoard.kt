package com.example.feature.game.components.board

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.domain.core.SudokuGridSize
import com.example.feature.game.theme.LocalSudokuBoardAppearance
import com.example.feature.game.theme.SudokuBoardAppearance
import com.example.feature.game.theme.SudokuGameTheme
import com.example.feature.uicore.theme.LocalAppColorScheme
import com.example.sudoku.model.SudokuGrid
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

@Composable
internal fun SudokuBoard(
	sudoku: SudokuGrid,
	focusedCells: PersistentSet<Pair<Int, Int>>,
	onCellClick: (Int, Int) -> Unit,
	onCellLongClick: (Int, Int) -> Unit,
	modifier: Modifier = Modifier,
	appearence: SudokuBoardAppearance = LocalSudokuBoardAppearance.current,
) {
	val isDarkTheme = LocalAppColorScheme.current.isDark
	val colors = appearence.colors
	val textMeasurer = rememberTextMeasurer()
	val sudokuGridSize = remember(sudoku.gridSize) { SudokuGridSize.fromIntSize(sudoku.gridSize) }

	var canvasSize by remember(sudokuGridSize) { mutableFloatStateOf(0f) }
	var cellSize by remember(sudokuGridSize) { mutableFloatStateOf(0f) }
	var drawableCellArea by remember { mutableFloatStateOf(0f) }

	val thickLineWidth = with(LocalDensity.current) { appearence.thickLineWidth.toPx() }
	val thinLineWidth = with(LocalDensity.current) { appearence.thinLineWidth.toPx() }
	val cornerRadius = with(LocalDensity.current) { appearence.cornerRadius.toPx() }
	val numCellsInBlock = floor(sqrt(sudoku.gridSize.toFloat())).toInt()

	val infiniteTransition = rememberInfiniteTransition(label = "FocusedCellBorderAnimation")
	val rotationAngle by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 360f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = 2000, easing = LinearEasing),
			repeatMode = RepeatMode.Restart,
		),
		label = "FocusedBorderRotation",
	)

	fun processTap(offset: Offset, lambda: (Int, Int) -> Unit) {
		if (cellSize == 0f) return

		val col = (offset.x / cellSize).toInt()
		val row = (offset.y / cellSize).toInt()

		if (row in 0 until sudoku.gridSize && col in 0 until sudoku.gridSize) {
			lambda(row, col)
		}
	}

	Box(
		modifier
			.onSizeChanged { newSize ->
				val newCanvasSize = min(newSize.width, newSize.height).toFloat()
				if (canvasSize != newCanvasSize) {
					canvasSize = newCanvasSize
					cellSize = canvasSize / sudoku.gridSize
					val numThickLines = sudoku.gridSize / numCellsInBlock + 1
					val numThinLines = sudoku.gridSize + 1 - numThickLines
					val totalLinesWidth =
						numThickLines * thickLineWidth + numThinLines * thinLineWidth
					drawableCellArea = (canvasSize - totalLinesWidth) / sudoku.gridSize
					cellSize = drawableCellArea + totalLinesWidth / sudoku.gridSize
				}
			}
			.pointerInput(sudoku.gridSize) {
				detectTapGestures(
					onTap = { processTap(it, onCellClick) },
					onLongPress = { processTap(it, onCellLongClick) },
				)
			}
			.drawWithContent {
				if (canvasSize == 0f) return@drawWithContent

				val clipPath = Path().apply {
					addRoundRect(
						RoundRect(
							Rect(Offset.Zero, Size(canvasSize, canvasSize)),
							CornerRadius(cornerRadius),
						),
					)
				}
				clipPath(clipPath) {
					sudoku.getArray().forEach { cellData ->
						val cellTopLeft = getCellTopLeft(
							row = cellData.row,
							column = cellData.col,
							numCellsInBlock = numCellsInBlock,
							drawableCellArea = drawableCellArea,
							thinLineWidth = thinLineWidth,
							thickLineWidth = thickLineWidth,
						)
						val drawState = cellData.toDrawState(
							isFocused = focusedCells.contains(cellData.row to cellData.col),
							colors = colors,
							isDarkTheme = isDarkTheme,
						)
						translate(left = cellTopLeft.x, top = cellTopLeft.y) {
							drawCell(
								drawState = drawState,
								textMeasurer = textMeasurer,
								cellSize = drawableCellArea,
								gridSize = sudokuGridSize.toIntSize(),
								focusRotationAngle = rotationAngle,
								focusGradientColors = colors.focusedGradient,
							)
						}
					}
					drawGridLines(
						gridSize = sudoku.gridSize,
						blockSize = numCellsInBlock,
						canvasWidth = canvasSize,
						cellSize = drawableCellArea,
						thinLineWidth = thinLineWidth,
						thickLineWidth = thickLineWidth,
						thinLineColor = colors.cellBorder,
						thickLineColor = colors.blockBorder,
					)
				}
				drawBoardFrame(
					color = colors.blockBorder,
					canvasWidth = canvasSize,
					strokeWidth = thickLineWidth,
					cornerRadius = cornerRadius,
				)
			},
	)
}

private fun getCellTopLeft(
	row: Int,
	column: Int,
	numCellsInBlock: Int,
	drawableCellArea: Float,
	thinLineWidth: Float,
	thickLineWidth: Float,
): Offset {
	val numThickLinesBeforecolumn = (column / numCellsInBlock) + 1
	val numThinLinesBeforecolumn = column - (column / numCellsInBlock)
	val x =
		numThickLinesBeforecolumn * thickLineWidth + numThinLinesBeforecolumn * thinLineWidth +
			column * drawableCellArea

	val numThickLinesBeforeRow = (row / numCellsInBlock) + 1
	val numThinLinesBeforeRow = row - (row / numCellsInBlock)
	val y =
		numThickLinesBeforeRow * thickLineWidth + numThinLinesBeforeRow * thinLineWidth +
			row * drawableCellArea

	return Offset(x, y)
}

@PreviewLightDark
@Preview(name = "Cell States")
@Composable
private fun SudokuBoardStatesPreview(
	@PreviewParameter(SudokuBoardPreviewParameterProvider::class) sudokuState:
	Pair<SudokuGrid, String>,
) {
	SudokuGameTheme {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.size(400.dp)
				.background(
					MaterialTheme.colorScheme.background,
				),
		) {
			Text(text = sudokuState.second, color = MaterialTheme.colorScheme.onBackground)
			SudokuBoard(
				sudoku = sudokuState.first,
				focusedCells = persistentSetOf(),
				onCellClick = { _, _ -> },
				onCellLongClick = { _, _ -> },
				modifier = Modifier
					.weight(1f)
					.aspectRatio(1f),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun SudokuBoardFocusedCellPreview() {
	SudokuGameTheme {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.size(400.dp)
				.background(
					MaterialTheme.colorScheme.background,
				),
		) {
			SudokuBoard(
				sudoku = SudokuGrid(gridSize = 9),
				focusedCells = persistentSetOf(
					Pair(0, 0),
					Pair(0, 1),
					Pair(0, 2),
				),
				onCellClick = { _, _ -> },
				onCellLongClick = { _, _ -> },
				modifier = Modifier
					.weight(1f)
					.aspectRatio(1f),
			)
		}
	}
}
