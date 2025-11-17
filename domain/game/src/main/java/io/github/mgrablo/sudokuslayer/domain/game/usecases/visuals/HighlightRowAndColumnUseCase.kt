package io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals

import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.model.clearRowColumnHighlight
import io.github.mgrablo.sudokucore.model.highlightRowAndColumn

class HighlightRowAndColumnUseCase {
	operator fun invoke(sudoku: SudokuGrid, row: Int, column: Int): SudokuGrid =
		sudoku.highlightRowAndColumn(row, column)
}

class ClearHighlightedRowAndColumnUseCase {
	operator fun invoke(sudoku: SudokuGrid): SudokuGrid = sudoku.clearRowColumnHighlight()
}
