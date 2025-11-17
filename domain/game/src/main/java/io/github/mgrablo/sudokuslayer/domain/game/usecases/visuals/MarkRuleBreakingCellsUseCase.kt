package io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals

import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.model.markRuleBreakingCells
import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
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
