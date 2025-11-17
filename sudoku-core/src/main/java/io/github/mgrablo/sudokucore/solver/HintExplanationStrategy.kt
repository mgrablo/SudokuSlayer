package io.github.mgrablo.sudokucore.solver

import androidx.compose.runtime.Stable
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid

@Stable
interface HintExplanationStrategy {
	fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider = HintStringProvider.DEFAULT,
	): List<HintExplanationStep>
}

class NakedSingleExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider,
	): List<HintExplanationStep> {
		val (row, column, value) = hint
		return listOf(
			// Step 1: Focus on the cell
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.FOCUS_ON_CELL)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.CellCoordinate(row + 1, column + 1),
					HintExplanationPart.Text("!"),
				),
			),
			// Step 2: Explain the naked single logic
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.THE_CELL_AT)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.CellCoordinate(row + 1, column + 1),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.ONE_POSSIBLE_CANDIDATE)),
					HintExplanationPart.Text("."),
				),
			),
			// Step 3: Explain the conclusion
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.ONLY_POSSIBLE_CANDIDATE)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Value(value),
					HintExplanationPart.Text(", "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.THE_CELL_AT)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.CellCoordinate(row + 1, column + 1),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.MUST_CONTAIN)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Value(value),
					HintExplanationPart.Text("."),
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

		// Determine which string to use for blocked reason based on the group type
		val blockedReasonKey = when (hintType.groupType) {
			is GroupType.Row -> HintStringKey.SAME_COLUMN
			is GroupType.Column -> HintStringKey.SAME_ROW
			is GroupType.Block -> HintStringKey.SAME_ROW
		}

		// Build explanation steps with proper structured parts
		return listOf(
			// Step 1: Focus on the cell
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.FOCUS_ON_CELL)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.CellCoordinate(hint.row + 1, hint.col + 1),
					HintExplanationPart.Text("!"),
				),
			),

			// Step 2: Explain hidden single logic - the value can only go in this cell
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.IN)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.ScopeReference(scopeType, scopeIndex),
					HintExplanationPart.Text(", "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.CANNOT_BE_PLACED_IN)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.THEY_ARE_BLOCKED_BY)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(blockedReasonKey)),
					HintExplanationPart.Text("."),
				),
			),

			// Step 3: Show affected cells if there are any
			HintExplanationStep(
				if (affectedCells.isNotEmpty()) {
					listOf(
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.AFFECTED_CELLS_ARE)),
						HintExplanationPart.Text(" "),
						HintExplanationPart.CellCoordinatesGroup(affectedCells),
						HintExplanationPart.Text("."),
					)
				} else {
					listOf(
						HintExplanationPart.Text(
							stringProvider.getString(
								HintStringKey.NO_OTHER_EMPTY_CELLS,
								getScopeTypeString(scopeType, stringProvider),
							),
						),
						HintExplanationPart.Text("."),
					)
				},
			),

			// Step 4: Conclusion
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.THEREFORE)),
					HintExplanationPart.Text(", "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.THE_CELL_AT)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.CellCoordinate(hint.row + 1, hint.col + 1),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.MUST_CONTAIN)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text("."),
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

		// Build the explanation steps
		val steps = mutableListOf<HintExplanationStep>()

		// Step 1: Focus on block
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.FOCUS_ON_BLOCK)),
					HintExplanationPart.Text(" "),
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
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.IN)),
						HintExplanationPart.Text(" "),
						HintExplanationPart.ScopeReference(scopeType, scopeIndex + 1),
						HintExplanationPart.Text(", "),
						HintExplanationPart.Value(hint.value),
						HintExplanationPart.Text(" "),
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.CAN_ONLY_BE_PLACED_IN)),
						HintExplanationPart.Text(" "),
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.FOLLOWING_CELLS)),
						HintExplanationPart.Text(" "),
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
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.THUS_REMOVE_FROM)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.CANNOT_BE_PLACED_IN)),
					HintExplanationPart.Text(" "),
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
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.AFFECTED_CELLS_ARE)),
						HintExplanationPart.Text(" "),
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
					HintExplanationPart.TechniqueName(
						stringProvider.getString(HintStringKey.TECHNIQUE_CLAIMING_CANDIDATE),
					),
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
		stringProvider: HintStringProvider,
	): List<HintExplanationStep> {
		val hintType = hint.type as HintType.PointingCandidate

		// Get block ID and scope type
		val affectedBlockId =
			(hint.row / grid.subgridSize) * grid.subgridSize + (hint.col / grid.subgridSize) + 1
		val scopeType = if (hintType.groupType is GroupType.Row) ScopeType.ROW else ScopeType.COLUMN

		// Get localized scope name
		val scopeTypeString = getScopeTypeString(scopeType, stringProvider)

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
			getScopePartStringLocalized(
				enforcingCells.firstOrNull(),
				hintType.groupType,
				stringProvider,
			)

		// Get the affected cells
		val affectedCells = hint.affectedCells.map { Pair(it.row + 1, it.col + 1) }

		// Build the explanation steps
		val steps = mutableListOf<HintExplanationStep>()

		// Step 1: Focus on the affected block
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.FOCUS_ON_BLOCK)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, affectedBlockId),
					HintExplanationPart.Text("!"),
				),
			),
		)

		// Step 2: Explain for each block where the value is confined to a single row/column
		scopeAndBlockInfo.forEach { (scopeIndex, blockId, cells) ->
			val positionString =
				getScopePartStringLocalized(scopeIndex, hintType.groupType, stringProvider)
			steps.add(
				HintExplanationStep(
					listOf(
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.IN)),
						HintExplanationPart.Text(" "),
						HintExplanationPart.ScopeReference(ScopeType.BLOCK, blockId),
						HintExplanationPart.Text(", "),
						HintExplanationPart.Value(hint.value),
						HintExplanationPart.Text(" "),
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.CAN_ONLY_APPEAR_IN_THE)),
						HintExplanationPart.Text(" "),
						HintExplanationPart.Text(positionString),
						HintExplanationPart.Text(" "),
						HintExplanationPart.Text(scopeTypeString),
						HintExplanationPart.Text("."),
					),
				),
			)
		}

		// Step 3: Explain the pointing pattern and its implication
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.IN)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, affectedBlockId),
					HintExplanationPart.Text(", "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.CANNOT_APPEAR_IN_THE)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(enforcingScopePart),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(scopeTypeString),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.SINCE_CONFINED_TO)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(enforcingScopePart),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(scopeTypeString),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.IN)),
					HintExplanationPart.Text(" "),
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
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.AFFECTED_CELLS_ARE)),
						HintExplanationPart.Text(" "),
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
					HintExplanationPart.TechniqueName(
						stringProvider.getString(HintStringKey.TECHNIQUE_POINTING_CANDIDATE),
					),
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

private fun getScopePartStringLocalized(
	cell: SudokuCellData?,
	groupType: GroupType,
	stringProvider: HintStringProvider,
): String {
	if (cell == null) return "null"
	return when (groupType) {
		is GroupType.Row ->
			when (cell.row % 3) {
				0 -> stringProvider.getString(HintStringKey.TOP)
				1 -> stringProvider.getString(HintStringKey.MIDDLE)
				2 -> stringProvider.getString(HintStringKey.BOTTOM)
				else -> "null"
			}

		is GroupType.Column ->
			when (cell.col % 3) {
				0 -> stringProvider.getString(HintStringKey.LEFT)
				1 -> stringProvider.getString(HintStringKey.CENTER)
				2 -> stringProvider.getString(HintStringKey.RIGHT)
				else -> "null"
			}

		else -> "null"
	}
}

private fun getScopePartStringLocalized(
	number: Int,
	groupType: GroupType,
	stringProvider: HintStringProvider,
): String = when (groupType) {
	is GroupType.Row ->
		when (number % 3) {
			0 -> stringProvider.getString(HintStringKey.TOP)
			1 -> stringProvider.getString(HintStringKey.MIDDLE)
			2 -> stringProvider.getString(HintStringKey.BOTTOM)
			else -> "null"
		}

	is GroupType.Column ->
		when (number % 3) {
			0 -> stringProvider.getString(HintStringKey.LEFT)
			1 -> stringProvider.getString(HintStringKey.CENTER)
			2 -> stringProvider.getString(HintStringKey.RIGHT)
			else -> "null"
		}

	else -> "null"
}

private fun getScopeTypeString(scopeType: ScopeType, stringProvider: HintStringProvider): String =
	when (scopeType) {
		ScopeType.ROW -> stringProvider.getString(HintStringKey.ROW)
		ScopeType.COLUMN -> stringProvider.getString(HintStringKey.COLUMN)
		ScopeType.BLOCK -> stringProvider.getString(HintStringKey.BLOCK)
		ScopeType.BLOCK_PART -> stringProvider.getString(HintStringKey.BLOCK_PART)
	}
