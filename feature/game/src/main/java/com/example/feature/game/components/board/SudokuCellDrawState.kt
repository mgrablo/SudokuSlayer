package com.example.feature.game.components.board

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.domain.core.SudokuGridSize
import com.example.feature.game.theme.SudokuBoardColors
import com.example.feature.uicore.darken
import com.example.feature.uicore.lighten
import com.example.sudoku.model.SudokuCellData
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet
import kotlin.math.floor
import kotlin.math.sqrt

const val NOTE_PADDING_FACTOR = 0.7f
const val NUMBER_PADDING_FACTOR = 0.7f

@Stable
internal data class SudokuCellDrawState(
	val backgroundColor: Color,
	val focusedColor: Color?,

	val numberText: String?,
	val numberTextColor: Color,
	val numberBackgroundColor: Color, // For circle behind numbers when highlighted matching or errors
	val numberTextSize: Float,

	val notes: PersistentSet<NoteDrawState>,

	val cellSize: Float,
)

@Stable
internal data class NoteDrawState(
	val text: String,
	val color: Color,
	val size: Float,
	val offset: Offset,
)

internal fun SudokuCellData.toDrawState(
	gridSize: SudokuGridSize,
	cellSize: Float,
	isFocused: Boolean,
	colors: SudokuBoardColors,
	isDarkTheme: Boolean,
): SudokuCellDrawState {
	val subgridSize = floor(sqrt(gridSize.toIntSize().toFloat())).toInt()

	// Background
	val cellBackgroundState = getCellBackgroundState(attributes)
	val backgroundColor = determineBackgroundColor(cellBackgroundState, colors, isDarkTheme)

	// Main number
	val cellContentState = getCellContentState(attributes)
	val numberTextSize = cellSize * NUMBER_PADDING_FACTOR
	val (numberTextColor, numberBackgroundColor) = determineNumberColors(cellContentState, colors)

	// Note
	val noteSlotSize = cellSize / subgridSize
	val noteTextSize = noteSlotSize * NOTE_PADDING_FACTOR

	val noteDrawStates = cornerNotes.map {
		val noteIndex = it - 1
		val row = noteIndex / subgridSize
		val col = noteIndex % subgridSize

		val x = col * noteSlotSize + (noteSlotSize / 2)
		val y = row * noteSlotSize + (noteSlotSize / 2)

		NoteDrawState(
			text = it.toString(),
			color = colors.onDefaultBackground,
			size = noteTextSize,
			offset = Offset(x, y),
		)
	}.toPersistentSet()

	return SudokuCellDrawState(
		backgroundColor = backgroundColor,
		focusedColor = colors.hintMarkBackground.takeIf { isFocused },
		numberText = number.takeIf { it != 0 }?.toString(),
		numberTextColor = numberTextColor,
		numberBackgroundColor = numberBackgroundColor,
		numberTextSize = numberTextSize,
		notes = noteDrawStates,
		cellSize = cellSize,
	)
}

private fun determineBackgroundColor(
	cellBackgroundState: CellBackgroundState,
	colors: SudokuBoardColors,
	isDarkTheme: Boolean,
): Color {
	val baseBackgroundColor = when (cellBackgroundState.underlyingStateIfSelected) {
		CellBackgroundState.Highlighted -> colors.highlightedBackground
		CellBackgroundState.SolutionConflict -> colors.invalidMarkBackground
		else -> colors.defaultBackground
	}

	val backgroundColor = if (cellBackgroundState.isSelected) {
		baseBackgroundColor.adjustForSelection(isDarkTheme)
	} else {
		baseBackgroundColor
	}
	return backgroundColor
}

private fun determineNumberColors(
	cellContentState: CellContentState,
	colors: SudokuBoardColors,
): Pair<Color, Color> {
	val numberTextColor = when (cellContentState) {
		CellContentState.Default -> colors.onDefaultBackground
		CellContentState.Generated -> colors.generatedNumber
		CellContentState.HintRevealed -> colors.onHintMarkBackground
		CellContentState.NumberMatchHighlight -> colors.onMatchingMarkBackground
		CellContentState.RuleBreaking -> colors.onInvalidMarkBackground
		CellContentState.SolutionConflict -> colors.onInvalidMarkBackground
	}
	val numberBackgroundColor = when (cellContentState) {
		CellContentState.HintRevealed -> colors.hintMarkBackground
		CellContentState.NumberMatchHighlight -> colors.matchingMarkBackground
		CellContentState.RuleBreaking -> colors.invalidMarkBackground
		CellContentState.Generated -> Color.Transparent
		CellContentState.Default -> Color.Transparent
		CellContentState.SolutionConflict -> Color.Transparent
	}
	return numberTextColor to numberBackgroundColor
}

private fun Color.adjustForSelection(isDarkTheme: Boolean, factor: Float = 0.1f): Color =
	if (isDarkTheme) {
		this.lighten(factor)
	} else {
		this.darken(factor)
	}
