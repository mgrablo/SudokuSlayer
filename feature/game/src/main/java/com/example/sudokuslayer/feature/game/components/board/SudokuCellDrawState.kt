package com.example.sudokuslayer.feature.game.components.board

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.example.sudoku.model.SudokuCellData
import com.example.sudokuslayer.feature.game.theme.SudokuBoardColors
import com.example.sudokuslayer.feature.uicore.darken
import com.example.sudokuslayer.feature.uicore.lighten
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet

@Stable
internal data class SudokuCellDrawState(
	val backgroundColor: Color,
	val focusedColor: Color?,
	val numberText: String?,
	val numberTextColor: Color,
	val numberBackgroundColor: Color, // For circle behind numbers when highlighted matching or errors
	val notes: PersistentSet<NoteDrawState>,
)

@Stable
internal data class NoteDrawState(val index: Int, val value: Int, val color: Color)

internal fun SudokuCellData.toDrawState(
	isFocused: Boolean,
	colors: SudokuBoardColors,
	isDarkTheme: Boolean,
): SudokuCellDrawState {
	// Background
	val cellBackgroundState = getCellBackgroundState(attributes)
	val backgroundColor = determineBackgroundColor(cellBackgroundState, colors, isDarkTheme)

	// Main number
	val cellContentState = getCellContentState(attributes)
	val (numberTextColor, numberBackgroundColor) = determineNumberColors(cellContentState, colors)

	// Note
	val noteDrawStates = cornerNotes.map {
		NoteDrawState(
			index = it - 1,
			value = it,
			color = colors.onDefaultBackground,
		)
	}.toPersistentSet()

	return SudokuCellDrawState(
		backgroundColor = backgroundColor,
		focusedColor = colors.hintMarkBackground.takeIf { isFocused },
		numberText = number.takeIf { it != 0 }?.toString(),
		numberTextColor = numberTextColor,
		numberBackgroundColor = numberBackgroundColor,
		notes = noteDrawStates,
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
