package com.example.feature.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.game.getRoundedBlockShape
import com.example.feature.game.theme.LocalSudokuBoardColors
import com.example.feature.game.theme.SudokuBoardColors
import com.example.feature.game.theme.SudokuGameTheme
import com.example.feature.uicore.darken
import com.example.feature.uicore.lighten
import com.example.feature.uicore.modifiers.breathingBorder
import com.example.feature.uicore.theme.LocalAppColorScheme
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuCellData
import kotlinx.collections.immutable.PersistentSet
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
) {
	val subgridSize by remember { derivedStateOf { sqrt(gridSize.toDouble()).toInt() } }
	val isSelected = remember(data.attributes) { data.attributes.contains(CellAttributes.SELECTED) }
	val isHighlighted =
		remember(data.attributes) { data.attributes.contains(CellAttributes.ROW_COLUMN_HIGHLIGHTED) }
	val isSolutionConflict =
		remember(data.attributes) { data.attributes.contains(CellAttributes.SOLUTION_CONFLICT) }

	val backgroundColor = rememberCellBackgroundColor(isSelected, isHighlighted, isSolutionConflict)
	val shape = getRoundedBlockShape(subgridSize, data.row, data.col)

	Box(
		contentAlignment = Alignment.Center,
		modifier =
		modifier
			.clip(shape)
			.border(1.dp, LocalSudokuBoardColors.current.cellBorder, shape)
			.breathingBorder(
				isActive = isFocused,
				focusedColor = LocalSudokuBoardColors.current.hintMarkBackground,
				shape = shape,
			)
			.background(backgroundColor)
			.combinedClickable(
				onClick = { onCellClick(data.row, data.col) },
				onLongClick = { onCellLongClick(data.row, data.col) },
			),
	) {
		if (data.number == 0) {
			NotesCellContent(
				cornerNotes = data.cornerNotes,
				subgridSize = subgridSize,
			)
		} else {
			FilledCellContent(
				number = data.number,
				attributes = data.attributes,
			)
		}
	}
}

@Composable
private fun FilledCellContent(
	number: Int,
	attributes: PersistentSet<CellAttributes>,
	modifier: Modifier = Modifier,
) {
	val colors = rememberFilledCellColors(attributes)

	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
			.padding(2.dp)
			.fillMaxSize()
			.clip(CircleShape)
			.background(colors.circleColor),
	) {
		Text(
			text = number.toString(),
			color = colors.textColor,
			autoSize = TextAutoSize.StepBased(),
			textAlign = TextAlign.Center,
			style = TextStyle.Default.copy(
				platformStyle = PlatformTextStyle(
					includeFontPadding = false,
				),
			),
			modifier = Modifier
				.fillMaxSize()
				.wrapContentHeight(
					align = Alignment.CenterVertically,
				),
		)
	}
}

@Composable
private fun NotesCellContent(
	cornerNotes: PersistentSet<Int>,
	subgridSize: Int,
	modifier: Modifier = Modifier,
) {
	if (cornerNotes.isNotEmpty()) {
		LazyVerticalGrid(
			columns = GridCells.Fixed(subgridSize),
			modifier = modifier.fillMaxSize(),
		) {
			items(
				count = cornerNotes.size,
				key = { index -> cornerNotes.elementAt(index) },
			) { index ->
				val cornerNote = cornerNotes.elementAt(index)
				Text(
					text = cornerNote.toString(),
					color = LocalSudokuBoardColors.current.onDefaultBackground,
					autoSize = TextAutoSize.StepBased(minFontSize = 4.sp),
					style = TextStyle.Default.copy(
						platformStyle = PlatformTextStyle(includeFontPadding = false),
					),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.aspectRatio(1f)
						.wrapContentHeight(align = Alignment.CenterVertically),
				)
			}
		}
	}
}

@Composable
private fun rememberCellBackgroundColor(
	isSelected: Boolean,
	isHighlighted: Boolean,
	isSolutionConflict: Boolean,
	colors: SudokuBoardColors = LocalSudokuBoardColors.current,
	isDarkTheme: Boolean = LocalAppColorScheme.current.isDark,
): Color {
	val backgroundColor = remember(colors, isSelected, isHighlighted, isSolutionConflict) {
		when {
			isSolutionConflict -> colors.invalidMarkBackground
			isHighlighted -> colors.highlightedBackground
			else -> colors.defaultBackground
		}
	}
	val finalColor = remember(isSelected, isDarkTheme) {
		if (isSelected) {
			if (isDarkTheme) {
				backgroundColor.lighten(0.10f)
			} else {
				backgroundColor.darken(0.10f)
			}
		} else {
			backgroundColor
		}
	}
	return finalColor
}

@Stable
private data class FilledCellColors(val textColor: Color, val circleColor: Color)

@Composable
private fun rememberFilledCellColors(attributes: PersistentSet<CellAttributes>): FilledCellColors {
	val colors = LocalSudokuBoardColors.current

	return remember(attributes) {
		when {
			attributes.contains(CellAttributes.SOLUTION_CONFLICT) ->
				FilledCellColors(
					textColor = colors.onInvalidMarkBackground,
					circleColor = Color.Transparent,
				)

			attributes.contains(CellAttributes.RULE_BREAKING) ->
				FilledCellColors(
					textColor = colors.onInvalidMarkBackground,
					circleColor = colors.invalidMarkBackground,
				)

			attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED) ->
				FilledCellColors(
					textColor = colors.onMatchingMarkBackground,
					circleColor = colors.matchingMarkBackground,
				)

			attributes.contains(CellAttributes.HINT_REVEALED) -> FilledCellColors(
				textColor = colors.onHintMarkBackground,
				circleColor = colors.hintMarkBackground,
			)

			attributes.contains(CellAttributes.GENERATED) -> FilledCellColors(
				textColor = colors.generatedNumber,
				circleColor = Color.Transparent,
			)

			else -> FilledCellColors(
				textColor = colors.onDefaultBackground,
				circleColor = Color.Transparent,
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@PreviewLightDark
@Composable
private fun SudokuCellPreview() {
	val gridSize = 16
	val list = createSudokuCellData(gridSize)

	SudokuGameTheme {
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			for (cellData in list) {
				SudokuCell(
					data = cellData,
					onCellClick = { _, _ -> },
					onCellLongClick = { _, _ -> },
					gridSize = gridSize,
					isFocused = false,
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
