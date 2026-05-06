package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData

/**
 * Naked Single strategy.
 *
 * Occurs when a cell has only one possible candidate remaining.
 * Since all other digits are already present in the same row, column, or block,
 * this remaining digit must be the value for the cell.
 */
internal class NakedSingleStrategy : HintStrategy {
	override fun findHints(data: List<SudokuCellData>, houses: List<House>): List<Hint> =
		// A naked single is any empty cell that has exactly one candidate left in its list
		data.filter { it.number == 0 && it.candidates.size == 1 }
			.map { cell ->
				Hint(
					row = cell.row,
					col = cell.col,
					value = cell.candidates.first(),
					type = HintType.NakedSingle,
					explanationStrategy = NakedSingleExplanation(),
				)
			}
}
