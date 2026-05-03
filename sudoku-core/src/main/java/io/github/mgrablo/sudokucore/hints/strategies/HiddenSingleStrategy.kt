package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.GroupType
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.symmetricDifference

internal class HiddenSingleStrategy : HintStrategy {
	override fun findHints(data: List<SudokuCellData>, houses: List<House>): List<Hint> {
		val hints = mutableListOf<Hint>()
		for (house in houses) {
			val emptyCells = house.cells.filter { it.number == 0 }
			if (emptyCells.isEmpty()) continue

			val diff = symmetricDifference(emptyCells.map { it.candidates })
			if (diff.isEmpty()) continue

			for (digit in diff) {
				val hintCell = emptyCells.find { it.candidates.contains(digit) }!!
				val hiddenSingleType = when (house) {
					is House.Row -> HintType.HiddenSingle(GroupType.Row(house.id))
					is House.Column -> HintType.HiddenSingle(GroupType.Column(house.id))
					is House.Block -> HintType.HiddenSingle(GroupType.Block(house.id))
				}
				hints.add(
					Hint(
						row = hintCell.row,
						col = hintCell.col,
						value = digit,
						type = hiddenSingleType,
						explanationStrategy = HiddenSingleExplanation(),
					),
				)
			}
		}
		return hints
	}
}
