package io.github.mgrablo.sudokuslayer.domain.game.usecases.game

import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokuslayer.domain.core.CellChange
import io.github.mgrablo.sudokuslayer.domain.core.changedTo
import io.github.mgrablo.sudokuslayer.domain.game.GameUpdate
import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
import kotlinx.collections.immutable.minus
import kotlinx.coroutines.flow.first

class AutoClearNotesUseCase(private val settingsRepository: SettingsRepository) {
	suspend operator fun invoke(
		sudokuGrid: SudokuGrid,
		row: Int,
		column: Int,
		number: Int,
	): GameUpdate {
		if (!settingsRepository.autoClearNotes.first()) return GameUpdate(sudokuGrid, emptyList())
		if (number == 0) return GameUpdate(sudokuGrid, emptyList())

		var result = sudokuGrid
		val cellChanges = mutableListOf<CellChange>()
		for (i in 0 until sudokuGrid.gridSize) {
			// Same column
			if (i != row && result.getCellAt(i, column).cornerNotes.contains(number)) {
				val oldCell = result.getCellAt(i, column)
				val newCell = oldCell.copy(cornerNotes = oldCell.cornerNotes - number)
				result = result.withReplacedCell(
					row = i,
					col = column,
					cellData = newCell,
				)
				cellChanges.add(oldCell changedTo newCell)
			}
			// Same row
			if (i != column && result.getCellAt(row, i).cornerNotes.contains(number)) {
				val oldCell = result.getCellAt(row, i)
				val newCell = oldCell.copy(cornerNotes = oldCell.cornerNotes - number)
				result = result.withReplacedCell(
					row = row,
					col = i,
					cellData = newCell,
				)
				cellChanges.add(oldCell changedTo newCell)
			}
		}

		// Same block
		val subgridSize = result.subgridSize
		result.data.filter {
			(it.row / subgridSize) * subgridSize + it.col / subgridSize ==
				(row / subgridSize) * subgridSize + column / subgridSize &&
				it.row != row &&
				it.col != column &&
				it.number == 0 &&
				it.cornerNotes.contains(number)
		}.forEach { oldCell ->
			val newCell = oldCell.copy(cornerNotes = oldCell.cornerNotes - number)
			result = result.withReplacedCell(
				row = oldCell.row,
				col = oldCell.col,
				cellData = newCell,
			)
			cellChanges.add(oldCell changedTo newCell)
		}

		return GameUpdate(
			resultingGrid = result,
			changes = cellChanges,
		)
	}
}
