package com.example.sudoku.solver

import androidx.compose.runtime.Stable
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid

@Stable
interface HintExplanationStrategy {
	fun generateStructuredHintExplanation(grid: SudokuGrid, hint: Hint): List<HintExplanationStep>
}

class NakedSingleExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
	): List<HintExplanationStep> {
		val (row, column, value) = hint
		return listOf(
			// Step 1: Focus on the cell
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("Focus on the cell at "),
					HintExplanationPart.CellCoordinate(row + 1, column + 1),
					HintExplanationPart.Text("!"),
				),
			),
			// Step 2: Explain the naked single logic
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("The cell at "),
					HintExplanationPart.CellCoordinate(row + 1, column + 1),
					HintExplanationPart.Text(
						" has only one possible candidate remaining after considering the numbers already present in its row, column, and block.",
					),
				),
			),
			// Step 3: Explain the conclusion
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("Since the only possible candidate for the cell is "),
					HintExplanationPart.Value(value),
					HintExplanationPart.Text(", this cell must contain "),
					HintExplanationPart.Value(value),
					HintExplanationPart.Text("."),
				),
			),
			// Step 4: Name the technique
			HintExplanationStep(
				listOf(
					HintExplanationPart.TechniqueName("Naked Single"),
				),
			),
		)
	}
}

class HiddenSingleExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
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

		// Get the affected cells (cells that can't have the value)
		val affectedCells = when (hintType.groupType) {
			is GroupType.Row -> {
				grid.getRow(hint.row)
					.filter { it.number == 0 && it.col != hint.col }
					.map { Pair(it.row + 1, it.col + 1) }
			}

			is GroupType.Column -> {
				grid.getColumn(hint.col)
					.filter { it.number == 0 && it.row != hint.row }
					.map { Pair(it.row + 1, it.col + 1) }
			}

			is GroupType.Block -> {
				val blockRow = (hint.row / grid.subgridSize) * grid.subgridSize
				val blockCol = (hint.col / grid.subgridSize) * grid.subgridSize

				val cells = mutableListOf<Pair<Int, Int>>()
				for (r in blockRow until blockRow + grid.subgridSize) {
					for (c in blockCol until blockCol + grid.subgridSize) {
						if ((r != hint.row || c != hint.col) && grid.getCellAt(r, c).number == 0) {
							cells.add(Pair(r + 1, c + 1))
						}
					}
				}
				cells
			}
		}

		// Build explanation steps with proper structured parts
		return listOf(
			// Step 1: Focus on the cell
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("Focus on the cell "),
					HintExplanationPart.CellCoordinate(hint.row + 1, hint.col + 1),
					HintExplanationPart.Text("!"),
				),
			),

			// Step 2: Explain hidden single logic - the value can only go in this cell
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("In "),
					HintExplanationPart.ScopeReference(scopeType, scopeIndex),
					HintExplanationPart.Text(", "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text(" cannot be placed in any other empty cells because "),
					HintExplanationPart.Text("they are blocked by "),
					HintExplanationPart.Value(hint.value),
					when (hintType.groupType) {
						is GroupType.Row ->
							HintExplanationPart.Text(" in the same column or block.")

						is GroupType.Column ->
							HintExplanationPart.Text(" in the same row or block.")

						is GroupType.Block ->
							HintExplanationPart.Text(" in the same row or column.")
					},
				),
			),

			// Step 3: Show affected cells if there are any
			HintExplanationStep(
				if (affectedCells.isNotEmpty()) {
					listOf(
						HintExplanationPart.Text("The affected cells are: "),
						HintExplanationPart.CellCoordinatesGroup(affectedCells),
						HintExplanationPart.Text("."),
					)
				} else {
					listOf(
						HintExplanationPart.Text(
							"There are no other empty cells in this ",
						),
						HintExplanationPart.ScopeReference(scopeType, null),
						HintExplanationPart.Text("."),
					)
				},
			),

			// Step 4: Conclusion
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("Therefore, the cell "),
					HintExplanationPart.CellCoordinate(hint.row + 1, hint.col + 1),
					HintExplanationPart.Text(" must contain "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text("."),
				),
			),

			// Step 5: Technique name
			HintExplanationStep(
				listOf(
					HintExplanationPart.TechniqueName("Hidden Single"),
				),
			),
		)
	}
}

class ClaimingCandidateExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
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

		// Build the explanation steps
		val steps = mutableListOf<HintExplanationStep>()

		// Step 1: Focus on block
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("Focus on "),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, blockId),
					HintExplanationPart.Text("!"),
				),
			),
		)

		// Step 2: Explain for each row/column where the value can be placed
		enforcingCellsByScope.forEach { (scopeIndex, cells) ->
			steps.add(
				HintExplanationStep(
					listOf(
						HintExplanationPart.Text("In "),
						HintExplanationPart.ScopeReference(scopeType, scopeIndex + 1),
						HintExplanationPart.Text(", "),
						HintExplanationPart.Value(hint.value),
						HintExplanationPart.Text(" can only be placed in the following cells: "),
						HintExplanationPart.CellCoordinatesGroup(cells),
						HintExplanationPart.Text("."),
					),
				),
			)
		}

		// Step 3: Conclusion - removing candidates from other cells in the block
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("Thus, you can remove "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text(" from candidates for the other cells in "),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, blockId),
					HintExplanationPart.Text("."),
				),
			),
		)

		// Step 4: Show affected cells
		if (affectedCells.isNotEmpty()) {
			steps.add(
				HintExplanationStep(
					listOf(
						HintExplanationPart.Text("The affected cells are: "),
						HintExplanationPart.CellCoordinatesGroup(affectedCells),
						HintExplanationPart.Text("."),
					),
				),
			)
		}

		// Step 5: Technique name
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.TechniqueName("Claiming Candidate"),
				),
			),
		)

		return steps
	}
}

class PointingCandidateExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
	): List<HintExplanationStep> {
		val hintType = hint.type as HintType.PointingCandidate

		// Get block ID and scope type
		val affectedBlockId =
			(hint.row / grid.subgridSize) * grid.subgridSize + (hint.col / grid.subgridSize) + 1
		val scopeType = if (hintType.groupType is GroupType.Row) ScopeType.ROW else ScopeType.COLUMN
		val scopeName = if (hintType.groupType is GroupType.Row) "row" else "column"

		// Get enforcing cells
		val enforcingCells = hint.enforcingCells

		// Group cells by row/column and get their block IDs
		val scopeAndBlockInfo = enforcingCells
			.groupBy { if (hintType.groupType is GroupType.Row) it.row else it.col }
			.map { (index, cells) ->
				val blockId = cells.firstOrNull()?.let {
					(it.row / grid.subgridSize) * grid.subgridSize + (it.col / grid.subgridSize) + 1
				} ?: 0
				Triple(index, blockId, cells.map { Pair(it.row + 1, it.col + 1) })
			}

		// Get the enforcing block ID
		val enforcingBlockId = enforcingCells.firstOrNull()?.let {
			(it.row / grid.subgridSize) * grid.subgridSize + (it.col / grid.subgridSize) + 1
		} ?: 0

		// Get the scope part description (top/middle/bottom row or left/center/right column)
		val enforcingScopePart =
			getScopePartString(enforcingCells.firstOrNull(), hintType.groupType)

		// Get the affected cells
		val affectedCells = hint.affectedCells.map { Pair(it.row + 1, it.col + 1) }

		// Build the explanation steps
		val steps = mutableListOf<HintExplanationStep>()

		// Step 1: Focus on the affected block
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("Focus on "),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, affectedBlockId),
					HintExplanationPart.Text("!"),
				),
			),
		)

		// Step 2: Explain for each block where the value is confined to a single row/column
		scopeAndBlockInfo.forEach { (scopeIndex, blockId, cells) ->
			steps.add(
				HintExplanationStep(
					listOf(
						HintExplanationPart.Text("In "),
						HintExplanationPart.ScopeReference(ScopeType.BLOCK, blockId),
						HintExplanationPart.Text(", "),
						HintExplanationPart.Value(hint.value),
						HintExplanationPart.Text(" can only appear in the "),
						HintExplanationPart.Text(
							getScopePartString(
								scopeIndex,
								hintType.groupType,
							),
						),
						HintExplanationPart.Text(" $scopeName."),
					),
				),
			)
		}

		// Step 3: Explain the pointing pattern and its implication
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text("In "),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, affectedBlockId),
					HintExplanationPart.Text(", "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text(
						" cannot appear in the $enforcingScopePart $scopeName since it is confined to the $enforcingScopePart $scopeName in ",
					),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, enforcingBlockId),
					HintExplanationPart.Text("."),
				),
			),
		)

		// Step 4: Show affected cells if any
		if (affectedCells.isNotEmpty()) {
			steps.add(
				HintExplanationStep(
					listOf(
						HintExplanationPart.Text("The affected cells are: "),
						HintExplanationPart.CellCoordinatesGroup(affectedCells),
						HintExplanationPart.Text("."),
					),
				),
			)
		}

		// Step 5: Technique name
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.TechniqueName("Pointing Candidate"),
				),
			),
		)

		return steps
	}
}

private fun getScopePartString(cell: SudokuCellData?, groupType: GroupType): String {
	if (cell == null) return "null"
	return when (groupType) {
		is GroupType.Row ->
			when (cell.row % 3) {
				0 -> "top"
				1 -> "middle"
				2 -> "bottom"
				else -> "null"
			}

		is GroupType.Column ->
			when (cell.col % 3) {
				0 -> "left"
				1 -> "center"
				2 -> "right"
				else -> "null"
			}

		else -> "null"
	}
}

private fun getScopePartString(number: Int, groupType: GroupType): String = when (groupType) {
	is GroupType.Row ->
		when (number % 3) {
			0 -> "top"
			1 -> "middle"
			2 -> "bottom"
			else -> "null"
		}

	is GroupType.Column ->
		when (number % 3) {
			0 -> "left"
			1 -> "center"
			2 -> "right"
			else -> "null"
		}

	else -> "null"
}
