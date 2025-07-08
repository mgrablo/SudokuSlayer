package com.example.feature.game.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.game.theme.LocalSudokuBoardColors
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuCellData
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentSetOf
import kotlin.math.sqrt

@Composable
internal fun SudokuCell(
	data: SudokuCellData,
	gridSize: Int,
	onCellClick: (Int, Int) -> Unit,
	onCellLongClick: (Int, Int) -> Unit,
	modifier: Modifier = Modifier,
	isFocused: Boolean = false,
	textStyle: TextStyle = TextStyle(),
) {
	val subgridSize by remember { derivedStateOf { sqrt(gridSize.toDouble()).toInt() } }
	val isSelected = remember(data.attributes) { data.attributes.contains(CellAttributes.SELECTED) }
	val isHighlighted =
		remember(data.attributes) { data.attributes.contains(CellAttributes.ROW_COLUMN_HIGHLIGHTED) }

	val backgroundColor =
		when {
			isSelected -> LocalSudokuBoardColors.current.selectedBackground
			isHighlighted -> LocalSudokuBoardColors.current.highlightedBackground
			else -> LocalSudokuBoardColors.current.defaultBackground
		}

	val shape = getRoundedBlockShape(gridSize, data.row, data.col)

	val innerBorderModifier =
		if (isFocused) {
			val infiniteTransition = rememberInfiniteTransition("breathingEffect")
			val borderWidth by infiniteTransition.animateFloat(
				initialValue = 1.8f,
				targetValue = 4f,
				animationSpec = infiniteRepeatable(
					animation = tween(750, easing = LinearEasing),
					repeatMode = RepeatMode.Reverse,
				),
				label = "borderWidth",
			)
			val borderAlpha by infiniteTransition.animateFloat(
				initialValue = 0.5f,
				targetValue = 1f,
				animationSpec = infiniteRepeatable(
					animation = tween(750, easing = LinearEasing),
					repeatMode = RepeatMode.Reverse,
				),
				label = "borderAlpha",
			)
			Modifier.border(
				width = borderWidth.dp,
				color = LocalSudokuBoardColors.current.hintMarkBackground.copy(alpha = borderAlpha),
				shape = shape,
			)
		} else {
			Modifier
		}

	Box(
		contentAlignment = Alignment.Center,
		modifier =
		modifier
			.clip(shape)
			.border(1.dp, LocalSudokuBoardColors.current.cellBorder, shape)
			.then(innerBorderModifier)
			.background(backgroundColor)
			.combinedClickable(
				onClick = { onCellClick(data.row, data.col) },
				onLongClick = { onCellLongClick(data.row, data.col) },
			),
	) {
		when (data.number) {
			0 -> {
				// Empty cell
				if (data.cornerNotes.isNotEmpty()) {
					LazyVerticalGrid(
						columns = GridCells.Fixed(subgridSize),
						modifier = Modifier.fillMaxSize(),
						contentPadding = PaddingValues(1.dp),
					) {
						items(
							count = data.cornerNotes.size,
							key = { index -> data.cornerNotes.elementAt(index) },
						) { index ->
							val cornerNote = data.cornerNotes.elementAt(index)
							Text(
								text = cornerNote.toString(),
								color = LocalSudokuBoardColors.current.onDefaultBackground,
								style = textStyle.copy(fontSize = textStyle.fontSize / subgridSize),
								textAlign = TextAlign.Center,
								modifier =
								Modifier
									.fillMaxSize()
									.wrapContentHeight(align = Alignment.CenterVertically),
							)
						}
					}
				}
			}

			else -> {
				// Filled cell
				val circleColor =
					when {
						data.attributes.contains(
							CellAttributes.RULE_BREAKING,
						) -> LocalSudokuBoardColors.current.invalidMarkBackground

						data.attributes.contains(
							CellAttributes.NUMBER_MATCH_HIGHLIGHTED,
						) -> LocalSudokuBoardColors.current.matchingMarkBackground

						data.attributes.contains(
							CellAttributes.HINT_REVEALED,
						) -> LocalSudokuBoardColors.current.hintMarkBackground

						else -> Color.Transparent
					}
				val textColor =
					when {
						data.attributes.contains(
							CellAttributes.RULE_BREAKING,
						) -> LocalSudokuBoardColors.current.onInvalidMarkBackground

						data.attributes.contains(
							CellAttributes.NUMBER_MATCH_HIGHLIGHTED,
						) -> LocalSudokuBoardColors.current.onMatchingMarkBackground

						data.attributes.contains(
							CellAttributes.HINT_REVEALED,
						) -> LocalSudokuBoardColors.current.onHintMarkBackground

						else -> LocalSudokuBoardColors.current.onDefaultBackground
					}
				Box(
					contentAlignment = Alignment.Center,
					modifier =
					Modifier
						.fillMaxSize(0.9f)
						.clip(CircleShape)
						.background(circleColor),
				) {
					Text(
						text = data.number.toString(),
						color = textColor,
						style = textStyle,
						textAlign = TextAlign.Center,
						modifier =
						Modifier
							.fillMaxSize()
							.wrapContentHeight(
								align = Alignment.CenterVertically,
							),
					)
				}
			}
		}
	}
}

fun getRoundedBlockShape(gridSize: Int, row: Int, column: Int): Shape {
	val blockSize = sqrt(gridSize.toDouble()).toInt()
	val shape =
		when {
			row % blockSize == 0 && column % blockSize == 0 -> TopLeftRoundedCornerShape
			row % blockSize == 0 && column % blockSize == blockSize - 1 -> TopRightRoundedCornerShape
			row % blockSize == blockSize - 1 && column % blockSize == 0 -> BottomLeftRoundedCornerShape
			row % blockSize == blockSize - 1 && column % blockSize == blockSize - 1 -> {
				BottomRightRoundedCornerShape
			}

			else -> RoundedCornerShape(0.dp)
		}
	return shape
}

val TopLeftRoundedCornerShape =
	RoundedCornerShape(
		topStart = 8.dp,
		topEnd = 0.dp,
		bottomStart = 0.dp,
		bottomEnd = 0.dp,
	)

val TopRightRoundedCornerShape =
	RoundedCornerShape(
		topStart = 0.dp,
		topEnd = 8.dp,
		bottomStart = 0.dp,
		bottomEnd = 0.dp,
	)

val BottomLeftRoundedCornerShape =
	RoundedCornerShape(
		topStart = 0.dp,
		topEnd = 0.dp,
		bottomStart = 8.dp,
		bottomEnd = 0.dp,
	)

val BottomRightRoundedCornerShape =
	RoundedCornerShape(
		topStart = 0.dp,
		topEnd = 0.dp,
		bottomStart = 0.dp,
		bottomEnd = 8.dp,
	)

@OptIn(ExperimentalLayoutApi::class)
@PreviewLightDark
@Composable
private fun SudokuCellPreview() {
	val gridSize = 16
	val list = createSudokuCellData(gridSize)

	val fontSize = (50.sp * 0.6f)

	SudokuSlayerTheme {
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			for (cellData in list) {
				SudokuCell(
					data = cellData,
					onCellClick = { _, _ -> },
					onCellLongClick = { _, _ -> },
					textStyle = TextStyle(fontSize = fontSize),
					gridSize = gridSize,
					isFocused = true,
					modifier = Modifier.size(50.dp),
				)
			}
		}
	}
}

private fun createSudokuCellData(gridSize: Int): List<SudokuCellData> {
	val list =
		mutableListOf<SudokuCellData>().apply {
			repeat(gridSize + 1) {
				add(SudokuCellData(0, 0, it))
			}
			repeat(gridSize + 1) { index ->
				add(
					SudokuCellData(
						0,
						0,
						0,
						cornerNotes = persistentSetOf<Int>().mutate { it.addAll(1..index) },
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
