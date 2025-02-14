package com.example.sudoku.model

import com.example.sudoku.solver.ClassicSudokuSolver
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet

fun SudokuGrid.updateCells(
	predicate: (SudokuCellData) -> Boolean,
	transform: (SudokuCellData) -> SudokuCellData,
): SudokuGrid {
	val newCells =
		data
			.map { cell ->
				if (predicate(cell)) transform(cell) else cell
			}.toPersistentList()

	persistentSetOf<Int>(1, 2, 3) + 4
	return copy(data = newCells)
}

fun SudokuGrid.addAttribute(
	predicate: (SudokuCellData) -> Boolean,
	attribute: CellAttributes,
): SudokuGrid = updateCells(predicate) { it.copy(attributes = it.attributes + attribute) }

fun SudokuGrid.removeAttribute(
	predicate: (SudokuCellData) -> Boolean,
	attribute: CellAttributes,
): SudokuGrid = updateCells(predicate) { it.copy(attributes = it.attributes - attribute) }

fun SudokuGrid.addAttribute(
	row: Int,
	column: Int,
	attribute: CellAttributes,
): SudokuGrid = updateCell(row, column) { it.copy(attributes = it.attributes + attribute) }

fun SudokuGrid.removeAttribute(
	row: Int,
	column: Int,
	attribute: CellAttributes,
): SudokuGrid = updateCell(row, column) { it.copy(attributes = it.attributes - attribute) }

fun SudokuGrid.addCornerNote(
	row: Int,
	column: Int,
	note: Int,
): SudokuGrid =
	updateCell(row, column) {
		it.copy(cornerNotes = it.cornerNotes + note)
	}

fun SudokuGrid.removeCornerNote(
	row: Int,
	column: Int,
	note: Int,
): SudokuGrid =
	updateCell(row, column) {
		it.copy(cornerNotes = it.cornerNotes - note)
	}

fun SudokuGrid.clearCornerNotes(
	row: Int,
	column: Int,
): SudokuGrid =
	updateCell(row, column) {
		it.copy(cornerNotes = persistentSetOf())
	}

fun SudokuGrid.clearAllCornerNotes(): SudokuGrid =
	updateCells({ !it.attributes.contains(CellAttributes.GENERATED) }) {
		it.copy(cornerNotes = persistentSetOf())
	}

fun SudokuGrid.clearGrid(): SudokuGrid =
	updateCells({ !it.attributes.contains(CellAttributes.GENERATED) }) {
		it.copy(
			number = 0,
			cornerNotes = persistentSetOf(),
			attributes = persistentSetOf(),
			candidates = persistentSetOf(),
		)
	}

fun SudokuGrid.fillNotes(): SudokuGrid =
	updateCells({ !it.attributes.contains(CellAttributes.GENERATED) }) {
		val candidates = ClassicSudokuSolver.getValidMoves(this, it.row, it.col).toPersistentSet()
		it.copy(cornerNotes = candidates)
	}

fun SudokuGrid.highlightMatchingCells(number: Int): SudokuGrid =
	updateCells({ it.number == number && number != 0 }) {
		it.copy(attributes = it.attributes + CellAttributes.NUMBER_MATCH_HIGHLIGHTED)
	}

fun SudokuGrid.clearMatchingNumberHighlight(): SudokuGrid =
	updateCells({ it.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED) }) {
		it.copy(attributes = it.attributes - CellAttributes.NUMBER_MATCH_HIGHLIGHTED)
	}

fun SudokuGrid.highlightRowAndColumn(
	row: Int,
	col: Int,
): SudokuGrid =
	updateCells({ it.row == row || it.col == col }) {
		it.copy(attributes = it.attributes + CellAttributes.ROW_COLUMN_HIGHLIGHTED)
	}

fun SudokuGrid.clearRowColumnHighlight(): SudokuGrid =
	updateCells({ it.attributes.contains(CellAttributes.ROW_COLUMN_HIGHLIGHTED) }) {
		it.copy(attributes = it.attributes - CellAttributes.ROW_COLUMN_HIGHLIGHTED)
	}

fun SudokuGrid.markRuleBreakingCells(): SudokuGrid {
	var updatedSudoku = this
	for (i in 0 until gridSize) {
		val rowData = data.filter { it.row == i && it.number != 0 }
		rowData.groupingBy { it.number }.eachCount().filter { it.value > 1 }.forEach { (number, _) ->
			updatedSudoku =
				updateCells(
					predicate = { it.row == i && it.number == number },
					transform = { it.copy(attributes = it.attributes + CellAttributes.RULE_BREAKING) },
				)
		}
		val colData = data.filter { it.col == i && it.number != 0 }
		colData.groupingBy { it.number }.eachCount().filter { it.value > 1 }.forEach { (number, _) ->
			updatedSudoku =
				updateCells(
					predicate = { it.col == i && it.number == number },
					transform = { it.copy(attributes = it.attributes + CellAttributes.RULE_BREAKING) },
				)
		}
		val subgridData = data.filter { it.number != 0 && ((it.row / subgridSize) * subgridSize + it.col / subgridSize) == i }
		subgridData.groupingBy { it.number }.eachCount().filter { it.value > 1 }.forEach { (number, _) ->
			updatedSudoku =
				updateCells(
					predicate = { ((it.row / subgridSize) * subgridSize + it.col / subgridSize) == i && it.number == number },
					transform = { it.copy(attributes = it.attributes + CellAttributes.RULE_BREAKING) },
				)
		}
	}
	return updatedSudoku
}

fun SudokuGrid.clearRuleBreakingCells(): SudokuGrid =
	updateCells({ it.attributes.contains(CellAttributes.RULE_BREAKING) }) {
		it.copy(attributes = it.attributes - CellAttributes.RULE_BREAKING)
	}
