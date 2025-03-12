package com.example.domain.game.usecases

import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.addAttribute
import com.example.sudoku.model.removeAttribute

class HighlightRowAndColumnUseCase {
	operator fun invoke(
		sudoku: SudokuGrid,
		row: Int,
		column: Int,
	): SudokuGrid =
		sudoku.addAttribute(
			predicate = { cell -> cell.row == row || cell.col == column },
			attribute = CellAttributes.ROW_COLUMN_HIGHLIGHTED,
		)
}

class ClearHighlightedRowAndColumnUseCase {
	operator fun invoke(sudoku: SudokuGrid): SudokuGrid =
		sudoku.removeAttribute(
			predicate = { cell -> cell.attributes.contains(CellAttributes.ROW_COLUMN_HIGHLIGHTED) },
			attribute = CellAttributes.ROW_COLUMN_HIGHLIGHTED,
		)
}
