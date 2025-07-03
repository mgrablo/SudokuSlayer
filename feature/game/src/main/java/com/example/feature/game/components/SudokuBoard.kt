package com.example.feature.game.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.LocalSudokuBoardColors
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentSetOf

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun SudokuBoard(
	sudoku: SudokuGrid,
	onCellClick: (Int, Int) -> Unit,
	onCellLongClick: (Int, Int) -> Unit,
	modifier: Modifier = Modifier,
	textStyle: TextStyle = TextStyle(),
) {
	val itemsInRow = remember { sudoku.gridSize }
	val itemsInBlock = remember { sudoku.subgridSize }
	BoxWithConstraints(
		contentAlignment = Alignment.Center,
		modifier = modifier,
	) {
		val requiredRatio = itemsInRow.toFloat() / itemsInRow
		val currentRatio = maxWidth / maxHeight
		val blockPadding = LocalPadding.current.tiny
		val totalBlockPadding = blockPadding * (itemsInRow / itemsInBlock - 1)
		val cellSize =
			if (requiredRatio > currentRatio) {
				(maxWidth - totalBlockPadding) / itemsInRow
			} else {
				(maxHeight - totalBlockPadding) / itemsInRow
			}
		val sizeAdjustedTextStyle =
			textStyle.copy(
				fontSize = (cellSize.value * 0.6f).sp,
			)

		val lineColor = LocalSudokuBoardColors.current.blockBorder

		Column {
			repeat(itemsInRow) { row ->
				if (row > 0 && row % itemsInBlock == 0) {
					Spacer(modifier = Modifier.height(blockPadding))
				}

				Row {
					repeat(itemsInRow) { col ->
						if (col > 0 && col % itemsInBlock == 0) {
							Spacer(modifier = Modifier.width(blockPadding))
						}

						SudokuCell(
							data = sudoku.getCellAt(row, col),
							gridSize = itemsInRow,
							onCellClick = { row, col -> onCellClick(row, col) },
							onCellLongClick = { row, col -> onCellLongClick(row, col) },
							modifier = Modifier.size(cellSize),
							textStyle = sizeAdjustedTextStyle,
						)
					}
				}
			}
		}

		Canvas(modifier = Modifier.size(cellSize * itemsInRow + totalBlockPadding)) {
			val lineThicknessPx = 2.dp.toPx()
			val numBlocks = itemsInRow / itemsInBlock

			fun dividerPosition(index: Int): Float = index * (itemsInBlock * cellSize.toPx()) +
				(index - 1) * blockPadding.toPx() +
				blockPadding.toPx() / 2f

			// Draw horizontal dividers
			for (i in 1 until numBlocks) {
				val y = dividerPosition(i)
				drawLine(
					color = lineColor,
					start = Offset(0f, y),
					end = Offset(size.width, y),
					strokeWidth = lineThicknessPx,
					cap = StrokeCap.Round,
				)
			}

			// Draw vertical dividers
			for (j in 1 until numBlocks) {
				val x = dividerPosition(j)
				drawLine(
					color = lineColor,
					start = Offset(x, 0f),
					end = Offset(x, size.height),
					strokeWidth = lineThicknessPx,
					cap = StrokeCap.Round,
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun SudokuBoardNormalPreview() {
	val grid = createFilledSudokuGrid(9)
	SudokuSlayerTheme {
		SudokuBoard(
			sudoku = grid,
			onCellClick = { _, _ -> },
			onCellLongClick = { _, _ -> },
		)
	}
}

@PreviewLightDark
@Composable
private fun SudokuBoardBigPreview() {
	val grid = createFilledSudokuGrid(16)
	SudokuSlayerTheme {
		SudokuBoard(
			sudoku = grid,
			onCellClick = { _, _ -> },
			onCellLongClick = { _, _ -> },
		)
	}
}

@PreviewLightDark
@Composable
private fun SudokuBoardSmallPreview() {
	val grid = createFilledSudokuGrid(4)
	SudokuSlayerTheme {
		SudokuBoard(
			sudoku = grid,
			onCellClick = { _, _ -> },
			onCellLongClick = { _, _ -> },
		)
	}
}

private fun createFilledSudokuGrid(gridSize: Int): SudokuGrid {
	val list = mutableListOf<IntArray>()
	repeat(gridSize) {
		list += (1..gridSize).toList().toIntArray()
	}
	return SudokuGrid.fromIntArray(list, gridSize)
}

private fun createSudokuCellData(gridSize: Int): List<SudokuCellData> {
	val list =
		mutableListOf<SudokuCellData>().apply {
			repeat(gridSize + 1) {
				// 0 to 9
				add(SudokuCellData(0, 0, it))
			}
			repeat(gridSize + 1) {
				add(
					SudokuCellData(
						0,
						0,
						it,
						cornerNotes = persistentSetOf<Int>().mutate { it.addAll(1..gridSize) },
					),
				)
			}
			repeat(8) {
				add(
					SudokuCellData(
						0,
						0,
						3,
						attributes = persistentSetOf(CellAttributes.entries[it]),
					),
				)
			}
		}
	return list
}
