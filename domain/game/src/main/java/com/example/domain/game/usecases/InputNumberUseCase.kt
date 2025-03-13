package com.example.domain.game.usecases

import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.clearRuleBreakingCells
import com.example.sudoku.model.markRuleBreakingCells
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.plus

class InputNumberUseCase {
	operator fun invoke(
		sudokuGrid: SudokuGrid,
		number: Int,
		row: Int,
		column: Int,
		isNote: Boolean,
		isHint: Boolean,
	): SudokuGrid {
		if (sudokuGrid.getCellAt(row, column).attributes.contains(CellAttributes.GENERATED)) {
			return sudokuGrid
		}

		val cell = sudokuGrid.getCellAt(row, column)

		var updatedGrid =
			if (number == 0) {
				sudokuGrid.withReplacedCell(
					row = row,
					col = column,
					cellData =
						cell.copy(
							number = number,
							cornerNotes = persistentSetOf(),
						),
				)
			} else if (isNote) {
				sudokuGrid.withReplacedCell(
					row = row,
					col = column,
					cellData =
						cell.copy(
							cornerNotes =
								if (cell.cornerNotes.contains(number)) {
									cell.cornerNotes - number
								} else {
									cell.cornerNotes + number
								},
						),
				)
			} else {
				sudokuGrid.withReplacedCell(
					row = row,
					col = column,
					cellData =
						cell.copy(
							number = if (number == cell.number) 0 else number,
							attributes =
								if (isHint) {
									cell.attributes + CellAttributes.HINT_REVEALED
								} else {
									cell.attributes - CellAttributes.HINT_REVEALED
								},
						),
				)
			}

		updatedGrid =
			updatedGrid
				.clearRuleBreakingCells()
				.markRuleBreakingCells()
		return updatedGrid
	}
}
