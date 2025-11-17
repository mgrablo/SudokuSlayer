package io.github.mgrablo.sudokucore.model

import io.github.mgrablo.sudokucore.solver.ClassicSudokuSolver
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

fun SudokuGrid.addAttribute(row: Int, column: Int, attribute: CellAttributes): SudokuGrid =
	updateCell(row, column) { it.copy(attributes = it.attributes + attribute) }

fun SudokuGrid.removeAttribute(row: Int, column: Int, attribute: CellAttributes): SudokuGrid =
	updateCell(row, column) { it.copy(attributes = it.attributes - attribute) }

fun SudokuGrid.addCornerNote(row: Int, column: Int, note: Int): SudokuGrid =
	updateCell(row, column) {
		it.copy(cornerNotes = it.cornerNotes + note)
	}

fun SudokuGrid.removeCornerNote(row: Int, column: Int, note: Int): SudokuGrid =
	updateCell(row, column) {
		it.copy(cornerNotes = it.cornerNotes - note)
	}

fun SudokuGrid.clearCornerNotes(row: Int, column: Int): SudokuGrid = updateCell(row, column) {
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

fun SudokuGrid.highlightRowAndColumn(row: Int, col: Int): SudokuGrid =
	updateCells({ it.row == row || it.col == col }) {
		it.copy(attributes = it.attributes + CellAttributes.ROW_COLUMN_HIGHLIGHTED)
	}

fun SudokuGrid.clearRowColumnHighlight(): SudokuGrid =
	updateCells({ it.attributes.contains(CellAttributes.ROW_COLUMN_HIGHLIGHTED) }) {
		it.copy(attributes = it.attributes - CellAttributes.ROW_COLUMN_HIGHLIGHTED)
	}

fun SudokuGrid.markRuleBreakingCells(): SudokuGrid {
	var updatedSudoku = this
	val ruleBreakingCells = mutableSetOf<Triple<Int, Int, Int>>()

	data
		.groupBy { it.row }
		.forEach { (_, rowCells) ->
			rowCells
				.filter { it.number != 0 }
				.groupBy { it.number }
				.filter { it.value.size > 1 }
				.forEach { (number, cells) ->
					cells.forEach { cell ->
						ruleBreakingCells.add(Triple(cell.row, cell.col, number))
					}
				}
		}

	data
		.groupBy { it.col }
		.forEach { (_, colCells) ->
			colCells
				.filter { it.number != 0 }
				.groupBy { it.number }
				.filter { it.value.size > 1 }
				.forEach { (number, cells) ->
					cells.forEach { cell ->
						ruleBreakingCells.add(Triple(cell.row, cell.col, number))
					}
				}
		}

	data
		.groupBy { (it.row / subgridSize) * subgridSize + it.col / subgridSize }
		.forEach { (_, blockCells) ->
			blockCells
				.filter { it.number != 0 }
				.groupBy { it.number }
				.filter { it.value.size > 1 }
				.forEach { (number, cells) ->
					cells.forEach { cell ->
						ruleBreakingCells.add(Triple(cell.row, cell.col, number))
					}
				}
		}

	return updateCells(
		predicate = { cell ->
			ruleBreakingCells.any {
				it.first == cell.row && it.second == cell.col && it.third == cell.number
			}
		},
		transform = { it.copy(attributes = it.attributes + CellAttributes.RULE_BREAKING) },
	)
}

fun SudokuGrid.clearRuleBreakingCells(): SudokuGrid =
	updateCells({ it.attributes.contains(CellAttributes.RULE_BREAKING) }) {
		it.copy(attributes = it.attributes - CellAttributes.RULE_BREAKING)
	}
