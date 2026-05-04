package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.GroupType
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.hints.containsCell
import io.github.mgrablo.sudokucore.hints.getBlockCells
import io.github.mgrablo.sudokucore.hints.getBlockId
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import kotlinx.collections.immutable.toPersistentSet
import kotlin.math.sqrt

/**
 * Claiming Candidate strategy.
 *
 * Occurs when all occurrences of a candidate digit within a row or column are confined to a single block.
 * This allows us to eliminate that candidate from all other cells in that same block.
 */
internal class ClaimingCandidateStrategy : HintStrategy {
	override fun findHints(data: List<SudokuCellData>, houses: List<House>): List<Hint> {
		val gridSize = sqrt(data.size.toDouble()).toInt()
		val blockSize = sqrt(gridSize.toDouble()).toInt()

		// Claiming candidates are found by looking at rows and columns
		return houses.filter { it is House.Row || it is House.Column }
			.flatMap { house -> findClaimingCandidates(house, data, blockSize) }
	}

	private fun findClaimingCandidates(
		house: House,
		data: List<SudokuCellData>,
		blockSize: Int,
	): List<Hint> {
		val emptyCellsInHouse = house.cells.filter { it.number == 0 }
		// The distinct digits that appear as candidates in this row/column
		val candidateDigits = emptyCellsInHouse.flatMap { it.candidates }.distinct()

		return candidateDigits.mapNotNull { digit ->
			val cellsWithDigit = emptyCellsInHouse.filter { digit in it.candidates }
			// A claiming candidate must appear in at least two cells in the row/column,
			// a single candidate cell doesn't create an elimination pattern
			if (cellsWithDigit.size < 2) return@mapNotNull null

			// Check if all occurrences of this digit in the house are in the same block
			val blockId = cellsWithDigit
				.map { getBlockId(it.row, it.col, blockSize) }
				.distinct()
				.singleOrNull() ?: return@mapNotNull null

			// Find cells in that same block that are NOT in the current house
			// and contain the digit as a candidate, these are the cells where the digit can be eliminated
			val affectedCells = getBlockCells(data, blockId / blockSize, blockId % blockSize)
				.filter { cell ->
					cell.number == 0 &&
						digit in cell.candidates &&
						!house.cells.containsCell(cell)
				}

			if (affectedCells.isEmpty()) return@mapNotNull null

			// Use the first affected cell as the hint's location
			val anchor = affectedCells.first()
			Hint(
				row = anchor.row,
				col = anchor.col,
				value = digit,
				type = HintType.ClaimingCandidate(house.toGroupType()),
				explanationStrategy = ClaimingCandidateExplanation(),
				affectedCells = affectedCells.toPersistentSet(),
				enforcingCells = cellsWithDigit.toPersistentSet(),
			)
		}
	}

	private fun House.toGroupType(): GroupType = when (this) {
		is House.Row -> GroupType.Row(id)
		is House.Column -> GroupType.Column(id)
		is House.Block -> GroupType.Block(id)
	}
}
