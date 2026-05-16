package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.containsCell
import io.github.mgrablo.sudokucore.hints.getColumnCells
import io.github.mgrablo.sudokucore.hints.getRowCells
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import kotlinx.collections.immutable.toPersistentSet

/**
 * Pointing Candidate strategy.
 *
 * Occurs when all occurrences of a candidate digit within a block are aligned in a single row or column.
 * This allows us to eliminate that candidate from all other cells in that same row or column outside the block.
 */
internal class PointingCandidateStrategy : HintStrategy {
	override fun findHints(data: List<SudokuCellData>, houses: List<House>): List<Hint> =
		// Pointing candidates are found by analyzing blocks
		houses.filterIsInstance<House.Block>().flatMap { house ->
			findPointingCandidates(house, data)
		}

	private fun findPointingCandidates(block: House.Block, data: List<SudokuCellData>): List<Hint> {
		val emptyCellsInBlock = block.cells.filter { it.number == 0 }
		// Identify all unique candidate digits currently present in this block
		val candidateDigits = emptyCellsInBlock.flatMap { it.candidates }.distinct()

		return candidateDigits.flatMap { digit ->
			val cellsWithDigit = emptyCellsInBlock.filter { digit in it.candidates }
			
			// A pointing candidate requires at least two cells to form a line.
			// If it's only in one cell, it might be a Hidden Single instead.
			if (cellsWithDigit.size < 2) return@flatMap emptyList()

			// Check both row and column directions for potential alignment
			listOfNotNull(
				checkDirection(
					digit = digit,
					candidateCells = cellsWithDigit,
					blockCells = block.cells,
					fullGridData = data,
					getGroupId = { it.row },
					getGroupCells = ::getRowCells,
					createGroupType = { Hint.GroupType.Row(it) },
				),
				checkDirection(
					digit = digit,
					candidateCells = cellsWithDigit,
					blockCells = block.cells,
					fullGridData = data,
					getGroupId = { it.col },
					getGroupCells = ::getColumnCells,
					createGroupType = { Hint.GroupType.Column(it) },
				),
			)
		}
	}

	/**
	 * Check if all candidate cells for this digit are aligned in one row/column.
	 * If so, eliminate the digit from other cells in that row/column outside the block.
	 */
	private fun checkDirection(
		digit: Int,
		candidateCells: List<SudokuCellData>,
		blockCells: List<SudokuCellData>,
		fullGridData: List<SudokuCellData>,
		getGroupId: (SudokuCellData) -> Int,
		getGroupCells: (List<SudokuCellData>, Int) -> List<SudokuCellData>,
		createGroupType: (Int) -> Hint.GroupType,
	): Hint? {
		// Check if all candidate cells are in the same row/column.
		val groupId = candidateCells
			.map(getGroupId)
			.distinct()
			.singleOrNull() ?: return null

		// Find cells in the same row/column that are OUTSIDE the current block.
		// If any of these cells contain the digit as a candidate, they can be eliminated.
		val affectedCells = getGroupCells(fullGridData, groupId)
			.filter { cell ->
				cell.number == 0 &&
					digit in cell.candidates &&
					!blockCells.containsCell(cell)
			}

		if (affectedCells.isEmpty()) return null

		return Hint.PointingCandidate(
			number = digit,
			groupType = createGroupType(groupId),
			affectedCells = affectedCells.toPersistentSet(),
			enforcingCells = candidateCells.toPersistentSet(),
		)
	}
}
