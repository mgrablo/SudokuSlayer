package io.github.mgrablo.sudokucore

import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.model.updateCells
import kotlinx.collections.immutable.toPersistentSet
import kotlin.math.sqrt

/**
 * Generates all houses (rows, columns, and blocks) for the given grid data.
 */
fun generateHouses(data: List<SudokuCellData>): List<House> {
	val gridSize = sqrt(data.size.toDouble()).toInt()
	val blockSize = sqrt(gridSize.toDouble()).toInt()

	return buildList {
		(0 until gridSize).forEach { i ->
			add(House.Row(data.filter { it.row == i }, i))
			add(House.Column(data.filter { it.col == i }, i))
		}
		for (r in 0 until gridSize step blockSize) {
			for (c in 0 until gridSize step blockSize) {
				val blockId = (r / blockSize) * blockSize + (c / blockSize)
				val blockCells = data.filter {
					it.row in r until r + blockSize && it.col in c until c + blockSize
				}
				add(House.Block(blockCells, blockId))
			}
		}
	}
}

/**
 * Sets candidates for a specific cell. Useful for unit testing hint strategies in isolation.
 */
fun SudokuGrid.withCandidates(row: Int, col: Int, vararg candidates: Int): SudokuGrid =
	updateCell(row, col) { it.copy(candidates = candidates.toSet().toPersistentSet()) }

/**
 * Sets candidates for multiple cells at once using a map of (row, col) to candidates.
 */
fun SudokuGrid.withCandidates(candidatesMap: Map<Pair<Int, Int>, Set<Int>>): SudokuGrid =
	updateCells({ (it.row to it.col) in candidatesMap.keys }) { cell ->
		cell.copy(candidates = (candidatesMap[cell.row to cell.col] ?: emptySet()).toPersistentSet())
	}
