package com.example.domain.game.usecases

import com.example.domain.settings.SettingsRepository
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.clearMatchingNumberHighlight
import com.example.sudoku.model.highlightMatchingCells

class HighlightMatchingNumbersUseCase(private val settingsRepository: SettingsRepository) {
	operator fun invoke(sudokuGrid: SudokuGrid, number: Int?): SudokuGrid {
		val clearedGrid = sudokuGrid.clearMatchingNumberHighlight()
		return if (number != null && number != 0) {
			clearedGrid.highlightMatchingCells(number)
		} else {
			clearedGrid
		}
	}
}
