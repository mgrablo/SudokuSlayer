package com.example.sudokuslayer.domain.game.usecases.input

import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.removeAttribute
import com.example.sudokuslayer.domain.game.usecases.visuals.ClearHighlightedRowAndColumnUseCase
import com.example.sudokuslayer.domain.game.usecases.visuals.HighlightMatchingNumbersUseCase
import com.example.sudokuslayer.domain.game.usecases.visuals.HighlightRowAndColumnUseCase
import kotlinx.collections.immutable.plus

class SelectCellUseCase(
	private val highlightRowAndColumnUseCase: HighlightRowAndColumnUseCase,
	private val highlightMatchingNumbersUseCase: HighlightMatchingNumbersUseCase,
	private val clearHighlightedRowAndColumnUseCase: ClearHighlightedRowAndColumnUseCase,
) {
	suspend operator fun invoke(sudoku: SudokuGrid, selectedCell: Pair<Int, Int>? = null): SudokuGrid {
		var updatedSudoku =
			sudoku.removeAttribute(
				predicate = { cell -> cell.attributes.contains(CellAttributes.SELECTED) },
				attribute = CellAttributes.SELECTED,
			)

		if (selectedCell == null) {
			updatedSudoku = highlightMatchingNumbersUseCase(updatedSudoku, null)
		} else {
			selectedCell.let { (row, col) ->
				if (updatedSudoku.getCellAt(row, col).number != 0) {
					updatedSudoku = highlightMatchingNumbersUseCase(updatedSudoku, null)
				}
			}
		}
		updatedSudoku =
			clearHighlightedRowAndColumnUseCase(
				sudoku = updatedSudoku,
			)

		selectedCell?.let { (row, col) ->
			val cell = updatedSudoku.getCellAt(row, col)
			updatedSudoku =
				updatedSudoku.withReplacedCell(
					row = row,
					col = col,
					cellData =
					cell.copy(
						attributes = cell.attributes + CellAttributes.SELECTED,
					),
				)
			updatedSudoku =
				highlightRowAndColumnUseCase(
					sudoku = updatedSudoku,
					row = row,
					column = col,
				)
			if (cell.number != 0) {
				updatedSudoku =
					highlightMatchingNumbersUseCase(
						sudokuGrid = updatedSudoku,
						number = cell.number,
					)
			}
		}

		return updatedSudoku
	}
}
