package com.example.feature.game.components.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.domain.core.SudokuGridSize
import com.example.feature.game.theme.LocalSudokuBoardColors
import com.example.feature.game.theme.SudokuBoardColors
import com.example.feature.game.theme.SudokuGameTheme
import com.example.feature.uicore.theme.LocalAppColorScheme
import com.example.sudoku.model.SudokuGrid
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

@Composable
internal fun CanvasBoard(
	sudoku: SudokuGrid,
	focusedCells: PersistentSet<Pair<Int, Int>>,
	onCellClick: (Int, Int) -> Unit,
	onCellLongClick: (Int, Int) -> Unit,
	modifier: Modifier = Modifier,
	colors: SudokuBoardColors = LocalSudokuBoardColors.current,
) {
	val isDarkTheme = LocalAppColorScheme.current.isDark
	val textMeasurer = rememberTextMeasurer()
	val sudokuGridSize = remember(sudoku) { SudokuGridSize.fromIntSize(sudoku.gridSize) }
	Box(
		modifier.drawWithContent {
			val canvasSize = min(size.width, size.height)
			val cellSize = canvasSize / sudoku.gridSize
			val thinLineWidth = 1.dp.toPx()
			val thickLineWidth = 2.dp.toPx()
			val blockSize = floor(sqrt(sudoku.gridSize.toFloat())).toInt()
			val cornerRadius = 8.dp.toPx()

			val clipPath = Path().apply {
				addRoundRect(
					RoundRect(Rect(Offset.Zero, Size(canvasSize, canvasSize)), CornerRadius(cornerRadius)),
				)
			}
			clipPath(clipPath) {
				drawRect(
					color = colors.defaultBackground,
				)
				sudoku.getArray().forEach { cellData ->
					val cellTopLeft = Offset(cellData.col * cellSize, cellData.row * cellSize)
					val drawState = cellData.toDrawState(
						gridSize = sudokuGridSize,
						cellSize = cellSize,
						isFocused = focusedCells.contains(cellData.row to cellData.col),
						colors = colors,
						isDarkTheme = isDarkTheme,
					)
					translate(left = cellTopLeft.x, top = cellTopLeft.y) {
						drawCell(drawState, textMeasurer)
					}
				}
				drawGridLines(
					gridSize = sudoku.gridSize,
					blockSize = blockSize,
					canvasWidth = canvasSize,
					cellSize = cellSize,
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

@PreviewLightDark
@Preview(name = "Cell States")
@Composable
private fun CanvasBoardStatesPreview(
	@PreviewParameter(CanvasBoardPreviewParameterProvider::class) sudokuState:
	Pair<SudokuGrid, String>,
) {
	SudokuGameTheme {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.background(
				MaterialTheme.colorScheme.background,
			),
		) {
			Text(text = sudokuState.second, color = MaterialTheme.colorScheme.onBackground)
			CanvasBoard(
				sudoku = sudokuState.first,
				focusedCells = persistentSetOf(),
				onCellClick = { _, _ -> },
				onCellLongClick = { _, _ -> },
				modifier = Modifier.size(400.dp),
			)
		}
	}
}
