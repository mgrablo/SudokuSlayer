package com.example.sudokuslayer.domain.game.usecases.visuals

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.clearMatchingNumberHighlight
import com.example.sudoku.model.highlightMatchingCells
import com.example.sudokuslayer.domain.settings.SettingsRepository
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
