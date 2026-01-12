package io.github.mgrablo.sudokucore.hints

import io.github.mgrablo.sudokucore.hints.strategies.ClaimingCandidateStrategy
import io.github.mgrablo.sudokucore.hints.strategies.HiddenSingleStrategy
import io.github.mgrablo.sudokucore.hints.strategies.NakedSingleStrategy
import io.github.mgrablo.sudokucore.hints.strategies.PointingCandidateStrategy
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.solver.ClassicSudokuSolver
import kotlinx.collections.immutable.toPersistentList
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

		// Loop over houses and try to find locked candidate eliminations
		val lockedEliminations =
			pointingCandidateStrategy.findHints(data, houses) +
				claimingCandidateStrategy.findHints(data, houses)

		if (lockedEliminations.isNotEmpty()) {
			// Use the candidate from the first elimination (could be refined to focus candidate that results in singles)
			val representative = lockedEliminations.first()
			// Merge locked eliminations into one Hint.
			val aggregatedCells =
				lockedEliminations
					.filter { it.value == representative.value }
					.flatMap { locked ->
						// Assuming each returned Hint's position represents an affected cell.
						listOf(
							SudokuCellData(
								row = locked.row,
								col = locked.col,
								number = locked.value,
							),
						)
					}.toPersistentList()
			val enforcingCells =
				lockedEliminations
					.filter { it.value == representative.value }
					.flatMap { it.enforcingCells }
					.toPersistentList()
			return representative.copy(
				affectedCells = aggregatedCells.toPersistentSet(),
				enforcingCells = enforcingCells.toPersistentSet(),
			)
		}
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
