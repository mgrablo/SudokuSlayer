package io.github.mgrablo.sudokucore.hints

import io.github.mgrablo.sudokucore.model.SudokuCellData
import kotlin.math.sqrt

internal fun getRow(data: List<SudokuCellData>, row: Int): List<SudokuCellData> =
	data.filter { it.row == row }

internal fun getColumn(data: List<SudokuCellData>, col: Int): List<SudokuCellData> =
	data.filter { it.col == col }

internal fun getBlock(data: List<SudokuCellData>, boxRow: Int, boxCol: Int): List<SudokuCellData> {
	val gridSize = sqrt(data.size.toDouble()).toInt()
	val blockSize = sqrt(gridSize.toDouble()).toInt()
	val startRow = boxRow * blockSize
	val startCol = boxCol * blockSize
	return data.filter {
		it.row in startRow until startRow + blockSize &&
			it.col in startCol until startCol + blockSize
	}
}

internal fun Collection<SudokuCellData>.containsCell(cell: SudokuCellData): Boolean =
	this.any { it.row == cell.row && it.col == cell.col }
