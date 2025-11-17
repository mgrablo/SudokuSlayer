package com.example.sudokuslayer.feature.game.components.board

import com.example.sudoku.model.CellAttributes
import kotlinx.collections.immutable.PersistentSet

internal sealed interface CellContentState {
	object Default : CellContentState
	object Generated : CellContentState
	object HintRevealed : CellContentState
	object NumberMatchHighlight : CellContentState
	object RuleBreaking : CellContentState
	object SolutionConflict : CellContentState
}

internal fun getCellContentState(attributes: PersistentSet<CellAttributes>): CellContentState =
	when {
		attributes.contains(CellAttributes.SOLUTION_CONFLICT) -> CellContentState.SolutionConflict
		attributes.contains(CellAttributes.RULE_BREAKING) -> CellContentState.RuleBreaking
		attributes.contains(
			CellAttributes.NUMBER_MATCH_HIGHLIGHTED,
		) -> CellContentState.NumberMatchHighlight
		attributes.contains(CellAttributes.HINT_REVEALED) -> CellContentState.HintRevealed
		attributes.contains(CellAttributes.GENERATED) -> CellContentState.Generated
		else -> CellContentState.Default
	}
