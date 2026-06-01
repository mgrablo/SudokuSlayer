package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.Hint.HiddenSingle
import io.github.mgrablo.sudokucore.hints.toHouseType
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.symmetricDifference

/**
 * Hidden Single strategy.
 *
 * Occurs when a candidate digit appears only once in a specific house (row, column, or block).
 * Even if the cell has other candidates, this digit must be the value for that cell.
 */
internal class HiddenSingleStrategy : HintStrategy {
	override fun findHints(data: List<SudokuCellData>, houses: List<House>): List<Hint> {
		return houses.flatMap { house ->
			val emptyCells = house.cells.filter { it.number == 0 }
			if (emptyCells.isEmpty()) return@flatMap emptyList()

			// Find digits that appear exactly once across all candidate sets in this house
			val uniqueCandidates = symmetricDifference(emptyCells.map { it.candidates })

			uniqueCandidates.map { digit ->
				val hintCell = emptyCells.first { digit in it.candidates }
				HiddenSingle(
					row = hintCell.row,
					col = hintCell.col,
					number = digit,
					houseType = house.toHouseType(),
				)
			}
		}
	}
}
