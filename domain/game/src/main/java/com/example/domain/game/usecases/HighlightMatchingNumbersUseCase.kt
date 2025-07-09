package com.example.domain.game.usecases

import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.addAttribute
import com.example.sudoku.model.removeAttribute

class HighlightMatchingNumbersUseCase {
	operator fun invoke(sudokuGrid: SudokuGrid, number: Int?): SudokuGrid {
		var updatedSudoku =
			sudokuGrid.removeAttribute(
				predicate = { cell -> cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED) },
				attribute = CellAttributes.NUMBER_MATCH_HIGHLIGHTED,
			)

		updatedSudoku = number?.let {
			updatedSudoku.addAttribute(
				predicate = { cell -> number != 0 && cell.number == number },
				attribute = CellAttributes.NUMBER_MATCH_HIGHLIGHTED,
			)
		} ?: updatedSudoku.removeAttribute(
			predicate = { cell -> cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED) },
			attribute = CellAttributes.NUMBER_MATCH_HIGHLIGHTED,
		)

		return updatedSudoku
	}
}

class ClearHighlightedNumbersUseCase(
	private val highlightMatchingNumbersUseCase: HighlightMatchingNumbersUseCase,
) {
	operator fun invoke(sudoku: SudokuGrid): SudokuGrid = highlightMatchingNumbersUseCase(
		sudokuGrid = sudoku,
		number = null,
	)
}
