package com.example.sudokuslayer.presentation.screen.game.components

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme

@Composable
fun SudokuBoard(
	sudoku: SudokuGrid,
	onCellClick: (Int, Int) -> Unit,
	modifier: Modifier = Modifier
) {
	val cellBorderColor = MaterialTheme.colorScheme.outlineVariant
	val blockBorderColor = MaterialTheme.colorScheme.outline

	var scale by remember { mutableFloatStateOf(1f) }
	val colors = remember {
		SudokuBoardColors(
			cellBorder = cellBorderColor,
			blockBorder = blockBorderColor
		)
	}

	val transformableState = rememberTransformableState { zoomChange, _, _ ->
		scale = (scale * zoomChange).coerceIn(0.5f, 2f)
	}

	Box(
		modifier = modifier
			.aspectRatio(1f)
			.drawWithContent {
				drawGridLines(
					gridSize = sudoku.gridSize,
					blockSize = sudoku.subgridSize,
					colors = colors
				)
			}
			.border(
				width = 1.dp,
				color = MaterialTheme.colorScheme.outlineVariant,
				shape = RoundedCornerShape(8.dp)
			)
			.graphicsLayer(
				scaleX = scale,
				scaleY = scale,
			)
			.transformable(state = transformableState)
			.clipToBounds()
	) {
		LazyVerticalGrid(
			columns = GridCells.Fixed(sudoku.gridSize),
			modifier = Modifier.fillMaxSize(),
			userScrollEnabled = false,
		) {
			items(sudoku.getArray(), key = { "r${it.row}c${it.col}" }) { cell ->
				SudokuCell(
					cellData = cell,
					onClick = { onCellClick(cell.row, cell.col) },
					attributeStates = CellAttributeStates(
						isGenerated = cell.attributes.contains(CellAttributes.GENERATED),
						isNumberHighlighted = cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
						isRowColumnHighlighted = cell.attributes.contains(CellAttributes.ROW_COLUMN_HIGHLIGHTED),
						isSelected = cell.attributes.contains(CellAttributes.SELECTED),
						isBreakingRules = cell.attributes.contains(CellAttributes.RULE_BREAKING),
						isHintFocus = cell.attributes.contains(CellAttributes.HINT_FOCUS),
						isHintRevealed = cell.attributes.contains(CellAttributes.HINT_REVEALED)
					),
				)
			}
		}
	}
}

fun ContentDrawScope.drawGridLines(
	gridSize: Int,
	blockSize: Int,
	colors: SudokuBoardColors,
	modifier: Modifier = Modifier
) {
	drawContent()
	repeat(gridSize - 1) { index ->
		val color = if ((index + 1) % blockSize == 0) colors.blockBorder else colors.cellBorder
		val width = if ((index + 1) % blockSize == 0) 2.dp.toPx() else 1.dp.toPx()

		drawLine(
			color = color,
			strokeWidth = width,
			start = Offset(size.width / gridSize * (index + 1), 0f),
			end = Offset(size.width / gridSize * (index + 1), size.height)
		)
		drawLine(
			color = color,
			strokeWidth = width,
			start = Offset(0f, size.height / gridSize * (index + 1)),
			end = Offset(size.width, size.height / gridSize * (index + 1))
		)
	}
}

// Add this data class to avoid recreating colors
data class SudokuBoardColors(
	val cellBorder: Color,
	val blockBorder: Color
)

@PreviewLightDark
@Composable
private fun SudokuBoardPreview() {
	SudokuSlayerTheme {
		var grid = SudokuGrid(9)
		SudokuBoard(
			grid,
			onCellClick = { row, col -> },
		)
	}
}

@Preview
@Composable
private fun SudokuBoardFourByFourPreview() {
	SudokuSlayerTheme {
		var grid = SudokuGrid(4)
		SudokuBoard(
			grid,
			onCellClick = { row, col -> },
		)
	}
}

@Preview
@Composable
private fun SudokuBoardSixteenBySixteenPreview() {
	SudokuSlayerTheme {
		var grid = SudokuGrid(16)
		SudokuBoard(
			grid,
			onCellClick = { row, col -> },
		)
	}
}
