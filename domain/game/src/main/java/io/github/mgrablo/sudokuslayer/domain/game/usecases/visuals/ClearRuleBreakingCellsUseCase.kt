package io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals

import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.model.clearRuleBreakingCells

class ClearRuleBreakingCellsUseCase {
	operator fun invoke(sudokuGrid: SudokuGrid): SudokuGrid = sudokuGrid.clearRuleBreakingCells()
}
