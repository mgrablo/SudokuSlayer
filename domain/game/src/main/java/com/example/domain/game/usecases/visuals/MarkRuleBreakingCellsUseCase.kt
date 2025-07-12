package com.example.domain.game.usecases.visuals

import com.example.domain.settings.SettingsRepository
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.markRuleBreakingCells

class MarkRuleBreakingCellsUseCase(private val settingsRepository: SettingsRepository) {
	operator fun invoke(sudokuGrid: SudokuGrid): SudokuGrid = sudokuGrid.markRuleBreakingCells()
}
