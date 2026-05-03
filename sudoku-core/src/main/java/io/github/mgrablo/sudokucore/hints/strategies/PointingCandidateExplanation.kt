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

class PointingCandidateExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider,
	): List<HintExplanationStep> {
		val hintType = hint.type as HintType.PointingCandidate
		val isRow = hintType.groupType is GroupType.Row
		val subgridSize = grid.subgridSize

		// 1. Identify the Line (Affected Scope - Row or Column)
		// We prefer using affectedCells to determine the line index because that's where the elimination happens.
		val scopeIndex = if (hint.affectedCells.isNotEmpty()) {
			if (isRow) hint.affectedCells.first().row else hint.affectedCells.first().col
		} else {
			hint.enforcingCells.firstOrNull()?.let {
				if (isRow) it.row else it.col
			} ?: 0
		}

		// 2. Filter Enforcing Cells
		// Enforcing cells must be on the same line (Row/Col) as the scopeIndex.
		val cellsOnLine = hint.enforcingCells.filter {
			(if (isRow) it.row else it.col) == scopeIndex
		}

		// Identify the Block from the first valid enforcing cell
		val firstCell = cellsOnLine.firstOrNull()
		val enforcingBlockId = if (firstCell != null) {
			(firstCell.row / subgridSize) * subgridSize + (firstCell.col / subgridSize) + 1
		} else {
			1
		}

		// Further filter enforcing cells to ensure they are in the identified block
		val finalEnforcingCells = cellsOnLine.filter { cell ->
			val blockId = (cell.row / subgridSize) * subgridSize + (cell.col / subgridSize) + 1
			blockId == enforcingBlockId
		}

		// 3. Prepare Parts
		val blockPart = HintExplanationPart.ScopeReference(ScopeType.BLOCK, enforcingBlockId)
		val valuePart = HintExplanationPart.Value(hint.value)
		val enforcingCellsPart = HintExplanationPart.CellCoordinatesGroup(
			finalEnforcingCells.map { Pair(it.row + 1, it.col + 1) },
		)
		val lineScopePart = HintExplanationPart.Text(
			if (isRow) {
				stringProvider.getString(HintStringKey.ROW)
			} else {
				stringProvider.getString(HintStringKey.COLUMN)
			},
		)
		val affectedCellsPart = HintExplanationPart.CellCoordinatesGroup(
			hint.affectedCells.map { Pair(it.row + 1, it.col + 1) },
		)

		val steps = mutableListOf<HintExplanationStep>()

		// Step 1: Focus on {0}! (Block)
		steps.add(
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.POINTING_CANDIDATE_STEP_1),
					blockPart,
				),
			),
		)

		// Step 2: In {0}, the number {1} points in a straight line. It must be in {2}.
		steps.add(
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.POINTING_CANDIDATE_STEP_2),
					blockPart,
					valuePart,
					enforcingCellsPart,
				),
			),
		)

		// Step 3: Since {0} must be in this line, you can remove it from the notes in the rest of the {1}.
		steps.add(
			HintExplanationStep(
				HintMessageFormatter.format(
					stringProvider.getString(HintStringKey.POINTING_CANDIDATE_STEP_3),
					valuePart,
					lineScopePart,
				),
			),
		)

		// Step 4: Update notes in: {0}.
		if (hint.affectedCells.isNotEmpty()) {
			steps.add(
				HintExplanationStep(
					HintMessageFormatter.format(
						stringProvider.getString(HintStringKey.POINTING_CANDIDATE_STEP_4),
						affectedCellsPart,
					),
				),
			)
		}

		// Step 5: Technique Name
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.TechniqueName(
						stringProvider.getString(HintStringKey.TECHNIQUE_POINTING_CANDIDATE),
					),
				),
			),
		)

		return steps
	}
}
