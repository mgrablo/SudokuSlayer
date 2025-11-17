package com.example.sudokuslayer.domain.game.usecases.visuals

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.markRuleBreakingCells
import com.example.sudokuslayer.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.first

class MarkRuleBreakingCellsUseCase(
	private val settingsRepository: SettingsRepository,
	private val clearRuleBreakingCellsUseCase: ClearRuleBreakingCellsUseCase,
) {
	suspend operator fun invoke(sudokuGrid: SudokuGrid): SudokuGrid {
		val clearedGrid = clearRuleBreakingCellsUseCase(sudokuGrid)
		val highlightInvalid = settingsRepository.highlightInvalidNumbers.first()
		return if (highlightInvalid) {
			clearedGrid.markRuleBreakingCells()
		} else {
			clearedGrid
		}
	}
}
