package com.example.sudoku.solver

import androidx.compose.runtime.Stable
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid

// Regex Patterns
// 'column 4'
// *HintType*
// number <4> in cell [2, 1]
// [row, col] - [3, 4]
// columns {1, 2, 3}

@Stable
interface HintExplanationStrategy {
	fun generateHintExplanationSteps(
		grid: SudokuGrid,
		hint: Hint,
	): List<String>
}

class NakedSingleExplanation : HintExplanationStrategy {
	override fun generateHintExplanationSteps(
		gird: SudokuGrid,
		hint: Hint,
	): List<String> {
		val (row, column, value) = hint
		return listOf(
			"Focus on the cell at [${row + 1}, ${column + 1}]!",
			"The cell at [${row + 1}, ${column + 1}] has only one possible candidate remaining after considering the numbers already present in its row, column, and block.",
			"Since the only possible candidate for the cell is <$value>, this cell must contain <$value>.",
			"*Naked Single*",
		)
	}
}

class HiddenSingleExplanation : HintExplanationStrategy {
	override fun generateHintExplanationSteps(
		grid: SudokuGrid,
		hint: Hint,
	): List<String> {
		val hintType = hint.type as HintType.HiddenSingle
		val scope =
			when (hintType.groupType) {
				is GroupType.Row -> "row ${hint.row + 1}"
				is GroupType.Column -> "col ${hint.col + 1}"
				is GroupType.Block -> "3x3 block containing the cell"
			}

		val blockedCells =
			when (hintType.groupType) {
				is GroupType.Row ->
					"columns {" +
						grid
							.getRow(hint.row)
							.filter { it.number == 0 && it.col != hint.col }
							.map { it.col + 1 }
							.joinToString() +
						"}"

				is GroupType.Column ->
					"rows {" +
						grid
							.getColumn(hint.col)
							.filter { it.number == 0 && it.row != hint.row }
							.map { it.row + 1 }
							.joinToString() +
						"}"

				is GroupType.Block -> "other cells"
			}

		val blockedReason =
			when (hintType.groupType) {
				is GroupType.Row ->
					"they are blocked by ${hint.value} in the same column or block"

				is GroupType.Column ->
					"they are blocked by ${hint.value} in the same row or block"

				is GroupType.Block ->
					"those cells are blocked by numbers in the same row or column"
			}

		return listOf(
			"Focus on the cell [${hint.row + 1}, ${hint.col + 1}]!",
			"In '$scope', <${hint.value}> cannot be placed in $blockedCells because $blockedReason.",
			"Therefore, the cell at [${hint.row + 1}, ${hint.col + 1}] must contain <${hint.value}>.",
			"*Hidden Single*",
		)
	}
}

class ClaimingCandidateExplanation : HintExplanationStrategy {
	override fun generateHintExplanationSteps(
		grid: SudokuGrid,
		hint: Hint,
	): List<String> {
		val hintType = hint.type as HintType.ClaimingCandidate
		// Collect coordinates from the affectedCells list
		val affectedCoords =
			hint.affectedCells
				.sortedBy { it.row }
				.map { "[${it.row + 1}, ${it.col + 1}]" }
				.toSet()
				.joinToString(", ")
		val block = grid.getSubgrid(hint.row, hint.col)
		val blockId = (hint.row / 3) * 3 + (hint.col / 3) + 1
		val otherCells = hint.enforcingCells
		val groupType = hintType.groupType

		// Determine the scope based on the claiming candidate type.
		val scope =
			if (groupType is GroupType.Row) {
				"row "
			} else {
				"column "
			}

		val otherCellsCoords =
			otherCells
				.groupBy { if (groupType is GroupType.Row) it.row else it.col }
				.map { (index, value) ->
					index to value.map { "[${it.row + 1}, ${it.col + 1}]" }.joinToString()
				}
		val cellsBasedOnScope =
			otherCellsCoords
				.map { "In '$scope ${it.first + 1}', <${hint.value}> can only be placed in the following cells: ${it.second}." }
				.toTypedArray()

		return listOf(
			"Focus on 'block $blockId'!",
			*cellsBasedOnScope,
			"Thus, you can remove <${hint.value}> from candidates for the other cells in 'block $blockId'. (Cells: $affectedCoords)",
			"*Claiming Candidate*",
		)
	}
}

class PointingCandidateExplanation : HintExplanationStrategy {
	override fun generateHintExplanationSteps(
		grid: SudokuGrid,
		hint: Hint,
	): List<String> {
		val hintType = hint.type as HintType.PointingCandidate
		val affectedBlockId = (hint.row / 3) * 3 + (hint.col / 3) + 1
		val scope = if (hintType.groupType is GroupType.Row) "row" else "column"

		val enforcingCells = hint.enforcingCells
		// Group the cells by the row or column index and map it to the block index
		val scopeAndBlockIdList =
			enforcingCells
				.groupBy { if (hintType.groupType is GroupType.Row) it.row else it.col }
				.map { (index, cells) ->
					Pair<Int, Int?>(
						index,
						cells.firstOrNull()?.let { (it.row / 3) * 3 + (it.col / 3) + 1 },
					)
				}

		val enforcingBlockId =
			enforcingCells.firstOrNull()?.let { (it.row / 3) * 3 + (it.col / 3) + 1 }
		val enforcingScopePart = getScopePartString(enforcingCells.firstOrNull(), hintType.groupType)

		val enforcingCellsExplanation =
			scopeAndBlockIdList
				.map {
					"In 'block ${it.second}', <${hint.value}> can only appear in the '${getScopePartString(it.first, hintType.groupType)} $scope'."
				}.toTypedArray()

		return listOf(
			"Focus at 'block $affectedBlockId'!",
			*enforcingCellsExplanation,
			"In 'block $affectedBlockId', <${hint.value}> cannot appear in '$enforcingScopePart $scope' since it is confined to '$enforcingScopePart $scope' in 'block $enforcingBlockId'.",
			"*Pointing Candidate*",
		)
	}
}

fun HintExplanationStrategy.getScopePartString(
	cell: SudokuCellData?,
	groupType: GroupType,
): String {
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

fun HintExplanationStrategy.getScopePartString(
	number: Int,
	groupType: GroupType,
): String =
	when (groupType) {
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
