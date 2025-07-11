package com.example.domain.game.usecases

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.clearMatchingNumberHighlight
import com.example.sudoku.model.highlightMatchingCells

class HighlightMatchingNumbersUseCase {
	operator fun invoke(sudokuGrid: SudokuGrid, number: Int?): SudokuGrid {
		val clearedGrid = sudokuGrid.clearMatchingNumberHighlight()
		return if (number != null) {
			clearedGrid.highlightMatchingCells(number)
		} else {
			clearedGrid
		}
	}
}
