package com.example.domain.game.usecases.visuals

import com.example.domain.settings.SettingsRepository
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.markRuleBreakingCells
import kotlinx.coroutines.flow.firstOrNull

class MarkRuleBreakingCellsUseCase(
	private val settingsRepository: SettingsRepository,
	private val clearRuleBreakingCellsUseCase: ClearRuleBreakingCellsUseCase,
) {
	suspend operator fun invoke(sudokuGrid: SudokuGrid): SudokuGrid {
		val clearedGrid = clearRuleBreakingCellsUseCase(sudokuGrid)
		val highlightInvalid = settingsRepository.highlightInvalidNumbers.firstOrNull() ?: true
		return if (highlightInvalid) {
			clearedGrid.markRuleBreakingCells()
		} else {
			clearedGrid
		}
	}
}
