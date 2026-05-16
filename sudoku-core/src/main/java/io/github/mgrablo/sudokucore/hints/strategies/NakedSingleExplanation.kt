package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintExplanationPart
import io.github.mgrablo.sudokucore.hints.HintExplanationStep
import io.github.mgrablo.sudokucore.hints.HintExplanationStrategy
import io.github.mgrablo.sudokucore.hints.HintMessageFormatter
import io.github.mgrablo.sudokucore.hints.HintStringKey
import io.github.mgrablo.sudokucore.hints.HintStringProvider
import io.github.mgrablo.sudokucore.model.SudokuGrid

class NakedSingleExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider,
	): List<HintExplanationStep> {
		require(hint is Hint.NakedSingle)
		val (row, column, number) = hint
		return listOf(
			// Step 1: Focus on the cell
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.NAKED_SINGLE_STEP_1),
					HintExplanationPart.CellCoordinate(row + 1, column + 1),
				),
			),
			// Step 2: Explain the naked single logic
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.NAKED_SINGLE_STEP_2),
					HintExplanationPart.CellCoordinate(row + 1, column + 1),
				),
			),
			// Step 3: Explain the conclusion
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.NAKED_SINGLE_STEP_3),
					HintExplanationPart.Value(number),
					HintExplanationPart.CellCoordinate(row + 1, column + 1),
					HintExplanationPart.Value(number),
				),
			),
			// Step 4: Name the technique
			HintExplanationStep(
				listOf(
					HintExplanationPart.TechniqueName(
						stringProvider.getString(HintStringKey.TECHNIQUE_NAKED_SINGLE),
					),
				),
			),
		)
	}
}
