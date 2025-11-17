package io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals

import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.model.clearMatchingNumberHighlight
import io.github.mgrablo.sudokucore.model.highlightMatchingCells
import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.first

class HighlightMatchingNumbersUseCase(private val settingsRepository: SettingsRepository) {
	suspend operator fun invoke(sudokuGrid: SudokuGrid, number: Int?): SudokuGrid {
		val clearedGrid = sudokuGrid.clearMatchingNumberHighlight()
		val highlightMatchingNumbers = settingsRepository.highlightMatchingNumbers.first()
		return if (number != null && number != 0 && highlightMatchingNumbers) {
			clearedGrid.highlightMatchingCells(number)
		} else {
			clearedGrid
		}
	}
}
