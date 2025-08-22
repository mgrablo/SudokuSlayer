package com.example.domain.game.usecases.game

import com.example.domain.core.CellChange
import com.example.domain.core.changedTo
import com.example.domain.game.GameUpdate
import com.example.domain.game.usecases.input.InputNumberUseCase
import com.example.domain.game.usecases.visuals.HighlightMatchingNumbersUseCase
import com.example.sudoku.model.SudokuGrid

class CalculateGridChangesUseCase(
	private val inputNumber: InputNumberUseCase,
	private val highlightMatching: HighlightMatchingNumbersUseCase,
	private val autoClearNotes: AutoClearNotesUseCase,
) {
	suspend operator fun invoke(
		initialGrid: SudokuGrid,
		row: Int,
		column: Int,
		number: Int,
		isNote: Boolean,
		isHint: Boolean,
	): GameUpdate {
		val changes = mutableListOf<CellChange>()
		val backupCell = initialGrid.getCellAt(row, column)
		var updatedSudoku: SudokuGrid =
			inputNumber(
				sudokuGrid = initialGrid,
				number = number,
				row = row,
				column = column,
				isNote = isNote,
				isHint = isHint,
			)
		changes.add(backupCell changedTo updatedSudoku.getCellAt(row, column))
		if (!isNote) {
			updatedSudoku = highlightMatching(updatedSudoku, number)
			val (newSudoku, noteChanges) = autoClearNotes(
				sudokuGrid = updatedSudoku,
				row = row,
				column = column,
				number = number,
			)
			updatedSudoku = newSudoku
			changes.addAll(noteChanges)
		}

		return GameUpdate(
			resultingGrid = updatedSudoku,
			changes = changes,
		)
	}
}
