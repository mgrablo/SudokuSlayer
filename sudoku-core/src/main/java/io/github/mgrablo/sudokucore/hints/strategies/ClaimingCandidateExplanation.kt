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

class ClaimingCandidateExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider,
	): List<HintExplanationStep> {
		val hintType = hint.type as HintType.ClaimingCandidate

		// Calculate block id
		val blockId =
			(hint.row / grid.subgridSize) * grid.subgridSize + (hint.col / grid.subgridSize) + 1

		// Organize enforcing cells by row or column
		val groupType = hintType.groupType
		val scopeType = if (groupType is GroupType.Row) ScopeType.ROW else ScopeType.COLUMN

		// Get affected cells coordinates (1-indexed)
		val affectedCells = hint.affectedCells
			.sortedBy { it.row }
			.map { Pair(it.row + 1, it.col + 1) }

		// Group enforcing cells by row/column
		val enforcingCellsByScope = hint.enforcingCells
			.groupBy { if (groupType is GroupType.Row) it.row else it.col }
			.map { (scopeIndex, cells) ->
				scopeIndex to cells.map { Pair(it.row + 1, it.col + 1) }
			}

		val steps = mutableListOf<HintExplanationStep>()
		val blockPart = HintExplanationPart.ScopeReference(ScopeType.BLOCK, blockId)
		val valuePart = HintExplanationPart.Value(hint.value)

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

		// Step 3: This means {0} is "claimed" by this row or column. You can erase {0} from the notes in the rest of the block.
		steps.add(
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.CLAIMING_CANDIDATE_STEP_3),
					valuePart,
					blockPart,
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
