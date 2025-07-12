package com.example.domain.game.usecases.visuals

import com.example.domain.settings.SettingsRepository
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.clearMatchingNumberHighlight
import com.example.sudoku.model.highlightMatchingCells
import kotlinx.coroutines.flow.firstOrNull

class HighlightMatchingNumbersUseCase(private val settingsRepository: SettingsRepository) {
	suspend operator fun invoke(sudokuGrid: SudokuGrid, number: Int?): SudokuGrid {
		val clearedGrid = sudokuGrid.clearMatchingNumberHighlight()
		val highlightMatchingNumbers = settingsRepository.highlightMatchingNumbers.firstOrNull() ?: true
		return if (number != null && number != 0 && highlightMatchingNumbers) {
			clearedGrid.highlightMatchingCells(number)
		} else {
			clearedGrid
		}
	}
}
