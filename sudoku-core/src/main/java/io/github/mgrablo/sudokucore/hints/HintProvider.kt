package io.github.mgrablo.sudokucore.hints

import io.github.mgrablo.sudokucore.hints.strategies.ClaimingCandidateStrategy
import io.github.mgrablo.sudokucore.hints.strategies.HiddenSingleStrategy
import io.github.mgrablo.sudokucore.hints.strategies.NakedSingleStrategy
import io.github.mgrablo.sudokucore.hints.strategies.PointingCandidateStrategy
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.solver.ClassicSudokuSolver
import kotlinx.collections.immutable.toPersistentSet
import kotlin.math.sqrt

class HintProvider internal constructor(
	private val nakedSingleStrategy: NakedSingleStrategy,
	private val hiddenSingleStrategy: HiddenSingleStrategy,
	private val pointingCandidateStrategy: PointingCandidateStrategy,
	private val claimingCandidateStrategy: ClaimingCandidateStrategy,
) {

	fun provideHint(data: List<SudokuCellData>): Hint? {
		val gridSize = sqrt(data.size.toDouble()).toInt()
		val blockSize = sqrt(gridSize.toDouble()).toInt()

		// Generate Houses from data
		val houses = mutableListOf<House>()
		(0..gridSize).forEach { i ->
			houses.add(
				House.Block(
					data.filter { it.row / blockSize == i / blockSize && it.col / blockSize == i % blockSize },
					i,
				),
			)
			houses.add(House.Row(data.filter { it.row == i }, i))
			houses.add(House.Column(data.filter { it.col == i }, i))
		}

		// Check Naked Single from entire grid.
		nakedSingleStrategy.findHints(data, houses).firstOrNull()?.let { return it }

		// Loop over houses and try to find a hidden single
		hiddenSingleStrategy.findHints(data, houses).firstOrNull()?.let { return it }

		// Return the first available locked-candidate pattern.
		pointingCandidateStrategy.findHints(data, houses).firstOrNull()?.let { return it }
		claimingCandidateStrategy.findHints(data, houses).firstOrNull()?.let { return it }
		return null
	}

	fun getPossibleValues(data: List<SudokuCellData>, row: Int, col: Int): Set<Int> {
		val possibleValues =
			ClassicSudokuSolver.getValidMoves(SudokuGrid.fromCellData(data), row, col).toSet()
		return possibleValues
	}
}

fun HintProvider.fillCandidates(data: List<SudokuCellData>): List<SudokuCellData> = data
	.map { cell ->
		if (cell.number == 0) {
			cell.copy(
				candidates = getPossibleValues(
					data,
					cell.row,
					cell.col,
				).toPersistentSet(),
			)
		} else {
			cell
		}
	}.toList()
