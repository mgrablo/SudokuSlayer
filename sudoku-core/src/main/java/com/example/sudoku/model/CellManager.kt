package com.example.sudoku.model

import com.example.sudoku.solver.ClassicSudokuSolver

class CellManager(
	private val data: Array<SudokuCellData>,
	private val gridSize: Int,
	private val blockSize: Int
) {
	fun addAttribute(row: Int, col: Int, attribute: CellAttributes) {
		updateCell(row, col) { it.copy(attributes = it.attributes + attribute) }
	}

	fun removeAttribute(row: Int, col: Int, attribute: CellAttributes) {
		updateCell(row, col) { it.copy(attributes = it.attributes - attribute) }
	}

	fun addCornerNote(row: Int, col: Int, noteNumber: Int) {
		updateCell(row, col) { it.copy(cornerNotes = it.cornerNotes + noteNumber) }
	}

	fun removeCornerNote(row: Int, col: Int, noteNumber: Int) {
		updateCell(row, col) { it.copy(cornerNotes = it.cornerNotes - noteNumber) }
	}

	fun clearCornerNotes(row: Int, col: Int) {
		updateCell(row, col) { it.copy(cornerNotes = emptySet()) }
	}

	fun resetGame() {
		updateFilteredCells(
			filterCondition = { !it.attributes.contains(CellAttributes.GENERATED) },
			updater = { it.copy(number = 0, cornerNotes = emptySet(), candidates = emptySet(), attributes = emptySet()) }
		)
		updateFilteredCells(
			filterCondition = { it.attributes.contains(CellAttributes.GENERATED) },
			updater = { it.copy(attributes = setOf(CellAttributes.GENERATED)) }
		)
	}

	fun clearNotes() {
		updateFilteredCells(
			filterCondition = {!it.attributes.contains(CellAttributes.GENERATED)},
			updater = { it.copy(cornerNotes = emptySet()) }
		)
	}

	fun lockGeneratedCells() {
		updateFilteredCells(
			filterCondition = { it.number != 0 },
			updater = {
				it.copy(
					attributes = it.attributes + CellAttributes.GENERATED
				)
			}
		)
	}

	fun highlightMatchingCells(number: Int) {
		if (number == 0)
			return
		updateFilteredCells(
			filterCondition = { it.number == number  },
			updater = { it.copy(attributes = it.attributes + CellAttributes.NUMBER_MATCH_HIGHLIGHTED) }
		)
	}

	fun highlightRowAndColumn(row: Int, col: Int){
		updateFilteredCells(
			filterCondition = { ( it.row == row || it.col == col ) && !it.attributes.contains(CellAttributes.SELECTED) },
			updater = { it.copy(attributes = it.attributes + CellAttributes.ROW_COLUMN_HIGHLIGHTED) }
		)
	}

	fun clearNumberHighlight() {
		updateFilteredCells(
			filterCondition =  { it.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED)},
			updater = { it.copy(attributes = it.attributes - CellAttributes.NUMBER_MATCH_HIGHLIGHTED)}
		)
	}

	fun clearRowColumnHighlight() {
		updateFilteredCells(
			filterCondition =  { it.attributes.contains(CellAttributes.ROW_COLUMN_HIGHLIGHTED)},
			updater = { it.copy(attributes = it.attributes - CellAttributes.ROW_COLUMN_HIGHLIGHTED)}
		)
	}

	fun fillNotes() {
		updateFilteredCells(
			filterCondition =  { !it.attributes.contains(CellAttributes.GENERATED) },
			updater = { cell ->
				val possibleNumbers = (1..gridSize).filter { ClassicSudokuSolver.isValidMove(SudokuGrid.fromCellData(data), cell.row, cell.col, it) }.toSet()
				cell.copy(cornerNotes = possibleNumbers)
			}
		)
	}

	fun markRuleBreakingCells() {
		for (row in 0 until gridSize) {
			val rowData = data.filter { it.row == row && it.number != 0}
			rowData.groupingBy { it.number }.eachCount().filter { it.value > 1 }.forEach { (number, _) ->
				updateFilteredCells(
					filterCondition = { it.row == row && it.number == number },
					updater = { it.copy(attributes = it.attributes + CellAttributes.RULE_BREAKING) }
				)
			}
		}
		for (col in 0 until gridSize) {
			val colData = data.filter { it.col == col && it.number != 0}
			colData.groupingBy { it.number }.eachCount().filter { it.value > 1 }.forEach { (number, _) ->
				updateFilteredCells(
					filterCondition = { it.col == col && it.number == number },
					updater = { it.copy(attributes = it.attributes + CellAttributes.RULE_BREAKING) }
				)
			}
		}
		for (subgrid in 0 until gridSize) {
			val subgridData = data.filter { it.number != 0 && ((it.row / blockSize) * blockSize + it.col / blockSize) == subgrid}
			subgridData.groupingBy { it.number }.eachCount().filter { it.value > 1 }.forEach { (number, _) ->
				updateFilteredCells(
					filterCondition = {((it.row / blockSize) * blockSize + it.col / blockSize) == subgrid && it.number == number },
					updater = { it.copy(attributes = it.attributes + CellAttributes.RULE_BREAKING) }
				)
			}
		}
	}

	fun clearRuleBreakingCells() {
		updateFilteredCells(
			filterCondition = { it.attributes.contains(CellAttributes.RULE_BREAKING) },
			updater = { it.copy(attributes = it.attributes - CellAttributes.RULE_BREAKING) }
		)
	}

	private fun updateCell(row: Int, col: Int, updater: (SudokuCellData) -> SudokuCellData) {
		val index = row * gridSize + col
		data[index] = updater(data[index])
	}

	private fun updateFilteredCells(
		filterCondition: (SudokuCellData) -> Boolean,
		updater: (SudokuCellData) -> SudokuCellData
	) {
		for (cell in data.filter(filterCondition)) {
			updateCell(cell.row, cell.col, updater)
		}
	}
}