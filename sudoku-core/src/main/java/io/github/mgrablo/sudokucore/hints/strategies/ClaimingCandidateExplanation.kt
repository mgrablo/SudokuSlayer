package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintExplanationPart
import io.github.mgrablo.sudokucore.hints.HintExplanationStep
import io.github.mgrablo.sudokucore.hints.HintExplanationStrategy
import io.github.mgrablo.sudokucore.hints.HintMessageFormatter
import io.github.mgrablo.sudokucore.hints.HintStringKey
import io.github.mgrablo.sudokucore.hints.HintStringProvider
import io.github.mgrablo.sudokucore.hints.ScopeType
import io.github.mgrablo.sudokucore.model.SudokuGrid

class ClaimingCandidateExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider,
	): List<HintExplanationStep> {
		require(hint is Hint.ClaimingCandidate)

		// Organize enforcing cells by row or column
		val groupType = hint.groupType
		val scopeType = if (groupType is Hint.GroupType.Row) ScopeType.ROW else ScopeType.COLUMN

		// Get affected cells coordinates (1-indexed)
		val affectedCells = hint.affectedCells
			.sortedBy { it.row }
			.map { Pair(it.row + 1, it.col + 1) }

		// Group enforcing cells by row/column
		val enforcingCellsByScope = hint.enforcingCells
			.groupBy { if (groupType is Hint.GroupType.Row) it.row else it.col }
			.map { (scopeIndex, cells) ->
				scopeIndex to cells.map { Pair(it.row + 1, it.col + 1) }
			}

		val steps = mutableListOf<HintExplanationStep>()

		// This should use group type and will be changed when refactoring Explanation classes
		// For now changed index to 0
		val blockPart = HintExplanationPart.ScopeReference(ScopeType.BLOCK, 0)
		val valuePart = HintExplanationPart.Value(hint.number)
		val lineScopePart = HintExplanationPart.Text(
			if (groupType is Hint.GroupType.Row) {
				stringProvider.getString(HintStringKey.ROW)
			} else {
				stringProvider.getString(HintStringKey.COLUMN)
			},
		)

		// Step 1: Check {0}!
		steps.add(
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.CLAIMING_CANDIDATE_STEP_1),
					blockPart,
				),
			),
		)

		// Step 2: In this {0}, the number {1} is forced to be in one of these specific cells: {2}.
		enforcingCellsByScope.forEach { (scopeIndex, cells) ->
			val scopePart = HintExplanationPart.ScopeReference(scopeType, scopeIndex + 1)
			val cellsPart = HintExplanationPart.CellCoordinatesGroup(cells)

			steps.add(
				HintExplanationStep(
					HintMessageFormatter.format(
						stringProvider.getString(HintStringKey.CLAIMING_CANDIDATE_STEP_2),
						scopePart,
						valuePart,
						cellsPart,
					),
				),
			)
		}

		// Step 3: This means {0} is "claimed" by this {1}. You can erase {0} from the notes in the rest of the block.
		steps.add(
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.CLAIMING_CANDIDATE_STEP_3),
					valuePart,
					lineScopePart,
				),
			),
		)

		// Step 4: Update notes in: {0}.
		if (affectedCells.isNotEmpty()) {
			val affectedPart = HintExplanationPart.CellCoordinatesGroup(affectedCells)
			steps.add(
				HintExplanationStep(
					HintMessageFormatter.format(
						stringProvider.getString(HintStringKey.CLAIMING_CANDIDATE_STEP_4),
						affectedPart,
					),
				),
			)
		}

		// Step 5: Technique name
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.TechniqueName(
						stringProvider.getString(HintStringKey.TECHNIQUE_CLAIMING_CANDIDATE),
					),
				),
			),
		)

		return steps
	}
}
