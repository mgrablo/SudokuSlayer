package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.GroupType
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.hints.containsCell
import io.github.mgrablo.sudokucore.hints.getBlock
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlin.math.sqrt

internal class ClaimingCandidateStrategy : HintStrategy {
	override fun findHints(data: List<SudokuCellData>, houses: List<House>): List<Hint> {
		val hints = mutableListOf<Hint>()
		houses.filter { it is House.Row || it is House.Column }.forEach { house ->
			hints.addAll(findClaimingCandidates(house, data))
		}
		return hints
	}

	private fun findClaimingCandidates(house: House, data: List<SudokuCellData>): List<Hint> {
		val hints = mutableListOf<Hint>()
		val candidateDigits = house.cells.flatMap { it.candidates }.toSet()
		val gridSize = sqrt(data.size.toDouble()).toInt()
		val blockSize = sqrt(gridSize.toDouble()).toInt()
		for (digit in candidateDigits) {
			val candidateCells = house.cells.filter { it.number == 0 && digit in it.candidates }
			if (candidateCells.isNotEmpty()) {
				val uniqueBlocks =
					candidateCells.map { (it.row / blockSize) * blockSize + (it.col / blockSize) }
						.toSet()
				if (uniqueBlocks.size == 1) {
					val blockIndex = uniqueBlocks.first()
					val blockCells =
						getBlock(data, blockIndex / blockSize, blockIndex % blockSize)
							.filter {
								!candidateCells.containsCell(it) && it.number == 0 &&
									digit in it.candidates
							}

					blockCells.forEach {
						hints.add(
							Hint(
								row = it.row,
								col = it.col,
								value = digit,
								type =
									HintType.ClaimingCandidate(
										if (house is House.Row) {
											GroupType.Row(
												house.id,
											)
										} else {
											GroupType.Column(house.id)
										},
									),
								explanationStrategy = ClaimingCandidateExplanation(),
												affectedCells = persistentSetOf(it),
								enforcingCells = candidateCells.toPersistentSet(),
							),
						)
					}
				}
			}
		}
		return hints
	}
}
