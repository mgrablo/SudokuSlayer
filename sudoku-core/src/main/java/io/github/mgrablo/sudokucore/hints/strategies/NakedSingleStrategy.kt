package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData

internal class NakedSingleStrategy : HintStrategy {
	override fun findHints(data: List<SudokuCellData>, houses: List<House>): List<Hint> =
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
