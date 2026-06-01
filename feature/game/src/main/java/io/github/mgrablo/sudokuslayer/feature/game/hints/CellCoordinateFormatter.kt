package io.github.mgrablo.sudokuslayer.feature.game.hints

import io.github.mgrablo.sudokucore.model.SudokuCellData

internal fun formatCellCoordinates(cells: Iterable<SudokuCellData>): String =
	cells.joinToString(separator = ", ") { formatCellCoordinate(it.row, it.col) }

internal fun formatCellCoordinate(row: Int, col: Int): String {
	val noBreakSpace = '\u00A0'
	return "[${row + 1},${noBreakSpace}${col + 1}]"
}

