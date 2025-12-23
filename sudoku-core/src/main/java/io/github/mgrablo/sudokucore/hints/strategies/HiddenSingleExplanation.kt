package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.GroupType
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintExplanationPart
import io.github.mgrablo.sudokucore.hints.HintExplanationStep
import io.github.mgrablo.sudokucore.hints.HintExplanationStrategy
import io.github.mgrablo.sudokucore.hints.HintMessageFormatter
import io.github.mgrablo.sudokucore.hints.HintStringKey
import io.github.mgrablo.sudokucore.hints.HintStringProvider
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.hints.ScopeType
import io.github.mgrablo.sudokucore.model.SudokuGrid

class HiddenSingleExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider,
	): List<HintExplanationStep> {
		val hintType = hint.type as HintType.HiddenSingle

		// Determine the scope type (row, column, or block)
		val (scopeType, scopeIndex) = when (hintType.groupType) {
			is GroupType.Row -> ScopeType.ROW to hint.row + 1

			is GroupType.Column -> ScopeType.COLUMN to hint.col + 1

			is GroupType.Block -> {
				val blockId =
					(hint.row / grid.subgridSize) * grid.subgridSize + (hint.col / grid.subgridSize) + 1
				ScopeType.BLOCK to blockId
			}
		}

		val cellPart = HintExplanationPart.CellCoordinate(hint.row + 1, hint.col + 1)
		val scopePart = HintExplanationPart.ScopeReference(scopeType, scopeIndex)
		val valuePart = HintExplanationPart.Value(hint.value)

		return listOf(
			// Step 1: Focus on the cell
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.HIDDEN_SINGLE_STEP_1),
					cellPart,
				),
			),

			// Step 2: Explain hidden single logic - the value can only go in this cell
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.HIDDEN_SINGLE_STEP_2),
					scopePart,
					valuePart,
					cellPart,
				),
			),
			// Step 4: Conclusion
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.HIDDEN_SINGLE_STEP_3),
					cellPart,
					valuePart,
				),
			),

			// Step 5: Technique name
			HintExplanationStep(
				listOf(
					HintExplanationPart.TechniqueName(
						stringProvider.getString(HintStringKey.TECHNIQUE_HIDDEN_SINGLE),
					),
				),
			),
		)
	}
}
