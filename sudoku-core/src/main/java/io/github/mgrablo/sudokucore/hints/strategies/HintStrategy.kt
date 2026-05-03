package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData

internal interface HintStrategy {
	fun findHints(data: List<SudokuCellData>, houses: List<House>): List<Hint>
}
