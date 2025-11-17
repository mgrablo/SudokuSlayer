package com.example.sudokuslayer.feature.game.components.board

import com.example.sudoku.model.CellAttributes
import kotlinx.collections.immutable.PersistentSet

internal sealed interface CellBackgroundState {
	object Default : CellBackgroundState
	object Highlighted : CellBackgroundState
	object SolutionConflict : CellBackgroundState
	data class Selected(val underlyingState: CellBackgroundState) : CellBackgroundState
}

internal fun getCellBackgroundState(
	attributes: PersistentSet<CellAttributes>,
): CellBackgroundState {
	val isSelected = attributes.contains(CellAttributes.SELECTED)
	val isHighlighted = attributes.contains(CellAttributes.ROW_COLUMN_HIGHLIGHTED)
	val isSolutionConflict = attributes.contains(CellAttributes.SOLUTION_CONFLICT)

	val baseState = when {
		isSolutionConflict -> CellBackgroundState.SolutionConflict
		isHighlighted -> CellBackgroundState.Highlighted
		else -> CellBackgroundState.Default
	}

	return if (isSelected) {
		CellBackgroundState.Selected(baseState)
	} else {
		baseState
	}
}

internal val CellBackgroundState.isSelected: Boolean
	get() = this is CellBackgroundState.Selected

internal val CellBackgroundState.underlyingStateIfSelected: CellBackgroundState
	get() = if (this is CellBackgroundState.Selected) this.underlyingState else this
