package io.github.mgrablo.sudokuslayer.domain.game.usecases.game

import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokuslayer.domain.core.CellChange
import io.github.mgrablo.sudokuslayer.domain.core.changedTo
import io.github.mgrablo.sudokuslayer.domain.game.GameUpdate
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.InputNumberUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals.HighlightMatchingNumbersUseCase

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
