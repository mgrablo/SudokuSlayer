package com.example.domain.game.usecases

import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.addAttribute
import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintType

class FocusOnHintCellsUseCase {
	operator fun invoke(
		hint: Hint,
		sudokuGrid: SudokuGrid,
	): SudokuGrid {
		var updatedSudoku = sudokuGrid
		when (hint.type) {
			is HintType.HiddenSingle, is HintType.NakedSingle -> {
				updatedSudoku =
					updatedSudoku.addAttribute(
						hint.row,
						hint.col,
						CellAttributes.HINT_FOCUS,
					)
			}

			is HintType.ClaimingCandidate, is HintType.PointingCandidate -> {
				hint.enforcingCells.forEach { cell ->
					updatedSudoku =
						updatedSudoku.addAttribute(
							cell.row,
							cell.col,
							CellAttributes.HINT_FOCUS,
						)
				}
			}
		}
		return updatedSudoku
	}
}
