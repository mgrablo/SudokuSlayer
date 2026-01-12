package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.GroupType
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.hints.containsCell
import io.github.mgrablo.sudokucore.hints.getColumn
import io.github.mgrablo.sudokucore.hints.getRow
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import kotlinx.collections.immutable.toPersistentSet

internal class PointingCandidateStrategy : HintStrategy {
	override fun findHints(data: List<SudokuCellData>, houses: List<House>): List<Hint> {
		val hints = mutableListOf<Hint>()
		houses.filterIsInstance<House.Block>().forEach { house ->
			hints.addAll(findPointingCandidates(house, data))
		}
		return hints
	}

	private fun findPointingCandidates(house: House.Block, data: List<SudokuCellData>): List<Hint> {
		val hints = mutableListOf<Hint>()
		val emptyCells = house.cells.filter { it.number == 0 }
		val candidateDigits = emptyCells.flatMap { it.candidates }.toSet()
		for (digit in candidateDigits) {
			val candidateCells = emptyCells.filter { digit in it.candidates }
			if (candidateCells.size < 2) continue

			val uniqueRows = candidateCells.map { it.row }.toSet()
			val uniqueCols = candidateCells.map { it.col }.toSet()

			// Candidate is pointing in a row
			if (uniqueRows.size == 1) {
				val rowCells =
					getRow(data, uniqueRows.first())
						.filter { !emptyCells.containsCell(it) && it.number == 0 }
						.filter { digit in it.candidates }

				rowCells.forEach {
					hints.add(
						Hint(
							row = it.row,
							col = it.col,
							value = digit,
							type = HintType.PointingCandidate(GroupType.Row(it.row)),
							explanationStrategy = PointingCandidateExplanation(),
							enforcingCells = candidateCells.toPersistentSet(),
						),
					)
				}
			}

			// Candidate is pointing in a column
			if (uniqueCols.size == 1) {
				val colCells =
					getColumn(data, uniqueCols.first())
						.filter { !emptyCells.containsCell(it) && it.number == 0 }
						.filter { digit in it.candidates }

				colCells.forEach {
					hints.add(
						Hint(
							row = it.row,
							col = it.col,
							value = digit,
							type = HintType.PointingCandidate(GroupType.Column(it.col)),
							explanationStrategy = PointingCandidateExplanation(),
							enforcingCells = candidateCells.toPersistentSet(),
						),
					)
				}
			}
		}
		return hints
	}
}
