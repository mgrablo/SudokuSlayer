package io.github.mgrablo.sudokucore.hints

import androidx.compose.runtime.Stable
import io.github.mgrablo.sudokucore.hints.strategies.HiddenSingleExplanation
import io.github.mgrablo.sudokucore.hints.strategies.NakedSingleExplanation
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.solver.ClassicSudokuSolver
import io.github.mgrablo.sudokucore.symmetricDifference
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlin.math.sqrt

@Stable
sealed interface HintType {
	object NakedSingle : HintType

	data class HiddenSingle(val groupType: GroupType) : HintType

	data class PointingCandidate(val groupType: GroupType) : HintType

	data class ClaimingCandidate(val groupType: GroupType) : HintType
}

@Stable
sealed interface GroupType {
	val id: Int

	data class Row(override val id: Int) : GroupType

	data class Column(override val id: Int) : GroupType

	data class Block(override val id: Int) : GroupType
}

@Stable
data class Hint(
	val row: Int,
	val col: Int,
	val value: Int,
	val type: HintType,
	val explanationStrategy: HintExplanationStrategy? = null,
	val additionalInfo: String = "",
	val affectedCells: PersistentSet<SudokuCellData> = persistentSetOf(),
	val enforcingCells: PersistentSet<SudokuCellData> = persistentSetOf(),
)

class HintProvider {
	fun provideHint(data: List<SudokuCellData>): Hint? {
		val gridSize = sqrt(data.size.toDouble()).toInt()
		val blockSize = sqrt(gridSize.toDouble()).toInt()
		// Check Naked Single from entire grid.
		findNakedSingle(data)?.let { return it }

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

		// Loop over houses and try to find a hidden single in each one
		for (house in houses) {
			findHiddenSingle(house)?.let { return it }
		}

		// Loop over houses and try to find locked candidate eliminations
		val lockedEliminations = mutableListOf<Hint>()
		for (house in houses) {
			findLockedCandidate(house, data)?.let { lockedEliminations.addAll(it) }
		}
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

	fun findNakedSingle(data: List<SudokuCellData>): Hint? {
		for (cell in data.filter { it.number == 0 }) {
			if (cell.candidates.size == 1) {
				return Hint(
					row = cell.row,
					col = cell.col,
					value = cell.candidates.first(),
					type = HintType.NakedSingle,
					explanationStrategy = NakedSingleExplanation(),
				)
			}
		}
		return null
	}

	fun findHiddenSingle(house: House): Hint? {
		val emptyCells = house.cells.filter { it.number == 0 }
		if (emptyCells.isEmpty()) return null

		val diff = symmetricDifference(emptyCells.map { it.candidates })
		if (diff.isEmpty()) return null

		for (digit in diff) {
			val hintValue = digit
			val hintCell = emptyCells.find { it.candidates.contains(hintValue) }!!
			val hiddenSingleType =
				when (house) {
					is House.Row -> HintType.HiddenSingle(GroupType.Row(house.id))
					is House.Column -> HintType.HiddenSingle(GroupType.Column(house.id))
					is House.Block -> HintType.HiddenSingle(GroupType.Block(house.id))
				}
			return Hint(
				row = hintCell.row,
				col = hintCell.col,
				value = hintValue,
				type = hiddenSingleType,
				explanationStrategy = HiddenSingleExplanation(),
			)
		}
		return null
	}

	// Now returns a list of hints representing eliminated candidates
	fun findLockedCandidate(house: House, data: List<SudokuCellData>): List<Hint>? = when (house) {
		is House.Block -> {
			val hints = findPointingCandidates(house, data)
			hints.ifEmpty { null }
		}

		is House.Row, is House.Column -> {
			val hints = findClaimingCandidates(house, data)
			hints.ifEmpty { null }
		}
	}

	// Works on a Block house only
	fun findPointingCandidates(house: House.Block, data: List<SudokuCellData>): List<Hint> {
		val hints = mutableListOf<Hint>()
		val emptyCells = house.cells.filter { it.number == 0 }
		val candidateDigits = emptyCells.flatMap { it.candidates }.toSet()
		for (digit in candidateDigits) {
			val candidateCells = emptyCells.filter { digit in it.candidates }
			if (candidateCells.size < 2) continue

			val uniqueRows = candidateCells.map { it.row }.toSet()
			val uniqueCols = candidateCells.map { it.col }.toSet()

			// Candidate is pointing in a row
			if (uniqueRows.size == 1) {
				val rowCells =
					getRow(data, uniqueRows.first())
						.filter { !emptyCells.containsCell(it) && it.number == 0 }
						.filter { digit in it.candidates }

				rowCells.forEach {
					hints.add(
						Hint(
							row = it.row,
							col = it.col,
							value = digit,
							type = HintType.PointingCandidate(GroupType.Row(it.row)),
							explanationStrategy = PointingCandidateExplanation(),
							enforcingCells = candidateCells.toPersistentSet(),
						),
					)
				}
			}

			// Candidate is pointing in a column
			if (uniqueCols.size == 1) {
				val colCells =
					getColumn(data, uniqueCols.first())
						.filter { !emptyCells.containsCell(it) && it.number == 0 }
						.filter { digit in it.candidates }

				colCells.forEach {
					hints.add(
						Hint(
							row = it.row,
							col = it.col,
							value = digit,
							type = HintType.PointingCandidate(GroupType.Column(it.col)),
							explanationStrategy = PointingCandidateExplanation(),
							enforcingCells = candidateCells.toPersistentSet(),
						),
					)
				}
			}
		}
		return hints
	}

	//  Works on a Row or Column house
	fun findClaimingCandidates(house: House, data: List<SudokuCellData>): List<Hint> {
		if (house !is House.Row && house !is House.Column) {
			throw IllegalArgumentException("House must be a Row or Column")
		}
		val hints = mutableListOf<Hint>()
		val candidateDigits = house.cells.flatMap { it.candidates }.toSet()
		val gridSize = sqrt(data.size.toDouble()).toInt()
		val blockSize = sqrt(gridSize.toDouble()).toInt()
		for (digit in candidateDigits) {
			val candidateCells = house.cells.filter { it.number == 0 && digit in it.candidates }
			if (candidateCells.isNotEmpty()) {
				// Check if candidate cells belong to the same block (using block index)
				val uniqueBlocks =
					candidateCells.map { (it.row / blockSize) * blockSize + (it.col / blockSize) }
						.toSet()
				if (uniqueBlocks.size == 1) {
					val blockIndex = uniqueBlocks.first()
					val blockCells =
						getBlock(data, blockIndex / blockSize, blockIndex % blockSize)
							.filter {
								!candidateCells.containsCell(it) && it.number == 0 && digit in it.candidates
							}

					blockCells.forEach {
						hints.add(
							Hint(
								row = it.row,
								col = it.col,
								value = digit,
								type =
									HintType.ClaimingCandidate(
										if (house is House.Row) {
											GroupType.Row(
												it.row,
											)
										} else {
											GroupType.Column(it.col)
										},
									),
								explanationStrategy = ClaimingCandidateExplanation(),
								enforcingCells = candidateCells.toPersistentSet(),
							),
						)
					}
				}
			}
		}
		return hints
	}

	fun getPossibleValues(data: List<SudokuCellData>, row: Int, col: Int): Set<Int> {
		val possibleValues =
			ClassicSudokuSolver.getValidMoves(SudokuGrid.fromCellData(data), row, col).toSet()
		return possibleValues
	}

	private fun getRow(data: List<SudokuCellData>, row: Int): List<SudokuCellData> =
		data.filter { it.row == row }

	private fun getColumn(data: List<SudokuCellData>, col: Int): List<SudokuCellData> =
		data.filter { it.col == col }

	private fun getBlock(data: List<SudokuCellData>, boxRow: Int, boxCol: Int): List<SudokuCellData> {
		val gridSize = sqrt(data.size.toDouble()).toInt()
		val blockSize = sqrt(gridSize.toDouble()).toInt()
		val startRow = boxRow * blockSize
		val startCol = boxCol * blockSize
		return data.filter {
			it.row in startRow until startRow + blockSize &&
				it.col in startCol until startCol + blockSize
		}
	}
}

fun Collection<SudokuCellData>.containsCell(cell: SudokuCellData): Boolean =
	this.any { it.row == cell.row && it.col == cell.col }

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
