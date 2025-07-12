package com.example.domain.game.usecases.input

import com.example.domain.game.usecases.visuals.MarkRuleBreakingCellsUseCase
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InputNumberUseCase(private val markRuleBreakingCellsUseCase: MarkRuleBreakingCellsUseCase) {
	private val mutex = Mutex()

	suspend operator fun invoke(
		sudokuGrid: SudokuGrid,
		number: Int,
		row: Int,
		column: Int,
		isNote: Boolean,
		isHint: Boolean,
	): SudokuGrid = mutex.withLock {
		if (sudokuGrid.getCellAt(row, column).attributes.contains(CellAttributes.GENERATED)) {
			return sudokuGrid
		}

		val cell = sudokuGrid.getCellAt(row, column)

		val updatedCell =
			when {
				number == 0 ->
					cell.copy(
						number = 0,
						cornerNotes = persistentSetOf(),
					)

				isNote ->
					cell.copy(
						number = 0,
						cornerNotes =
						if (cell.cornerNotes.contains(number) && !isHint) {
							cell.cornerNotes - number
						} else {
							cell.cornerNotes + number
						}.sorted().toPersistentSet(),
					)

				else ->
					cell.copy(
						number = if (number == cell.number) 0 else number,
						cornerNotes = persistentSetOf(),
						attributes = if (isHint) {
							cell.attributes + CellAttributes.HINT_REVEALED
						} else {
							cell.attributes -
								CellAttributes.HINT_REVEALED
						},
					)
			}

		val updatedGrid = sudokuGrid.withReplacedCell(row, column, updatedCell)
		return markRuleBreakingCellsUseCase(updatedGrid)
	}
}
