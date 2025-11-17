package com.example.sudokuslayer.domain.game.usecases.visuals

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.clearRuleBreakingCells

class ClearRuleBreakingCellsUseCase {
	operator fun invoke(sudokuGrid: SudokuGrid): SudokuGrid = sudokuGrid.clearRuleBreakingCells()
}
