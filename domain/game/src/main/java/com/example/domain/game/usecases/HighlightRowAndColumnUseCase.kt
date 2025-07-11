package com.example.domain.game.usecases

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.clearRowColumnHighlight
import com.example.sudoku.model.highlightRowAndColumn

class HighlightRowAndColumnUseCase {
	operator fun invoke(sudoku: SudokuGrid, row: Int, column: Int): SudokuGrid =
		sudoku.highlightRowAndColumn(row, column)
}

class ClearHighlightedRowAndColumnUseCase {
	operator fun invoke(sudoku: SudokuGrid): SudokuGrid = sudoku.clearRowColumnHighlight()
}
