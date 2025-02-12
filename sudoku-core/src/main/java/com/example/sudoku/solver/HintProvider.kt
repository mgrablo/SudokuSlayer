package com.example.sudoku.solver

import com.example.sudoku.model.House
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.symmetricDifference

sealed interface HintType {
	object NakedSingle : HintType
	data class HiddenSingle(val groupType: GroupType) : HintType
	data class PointingCandidate(val groupType: GroupType) : HintType
	data class ClaimingCandidate(val groupType: GroupType) : HintType
}

sealed interface GroupType {
	val id: Int

	data class Row(override val id: Int) : GroupType
	data class Column(override val id: Int) : GroupType
	data class Block(override val id: Int) : GroupType
}

data class Hint(
	val row: Int,
	val col: Int,
	val value: Int,
	val type: HintType? = null,
	val explanationStrategy: HintExplanationStrategy? = null,
	val additionalInfo: String = "",
	val affectedCells: List<SudokuCellData> = emptyList(), // field that contains cells that are affected by the hint
	val enforcingCells: List<SudokuCellData> = emptyList() // field that contains cells that enforce the hint
)

class HintProvider(val gridSize: Int = 9) {
	fun provideHint(data: Array<SudokuCellData>): Hint? {
		// Check Naked Single from entire grid.
		findNakedSingle(data)?.let { return it }

		// Generate Houses from data
		val houses = mutableListOf<House>()
		(0..gridSize).forEach { i ->
			houses.add(House.Block(data.filter { it.row / 3 == i / 3 && it.col / 3 == i % 3 }, i))
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
			val aggregatedCells = lockedEliminations.filter { it.value == representative.value }.flatMap { locked ->
				// Assuming each returned Hint's position represents an affected cell.
				listOf(
					SudokuCellData(
						row = locked.row,
						col = locked.col,
						number = locked.value,
					)
				)
			}
			val enforcingCells = lockedEliminations.filter { it.value == representative.value }.flatMap { it.enforcingCells }
			return representative.copy(
				affectedCells = aggregatedCells,
				enforcingCells = enforcingCells
			)
		}
		return null
	}

	fun findNakedSingle(data: Array<SudokuCellData>): Hint? {
		for (cell in data.filter { it.number == 0 }) {
			if (cell.candidates.size == 1) {
				return Hint(
					row = cell.row,
					col = cell.col,
					value = cell.candidates.first(),
					type = HintType.NakedSingle,
					explanationStrategy = NakedSingleExplanation()
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
			val hiddenSingleType = when (house) {
				is House.Row -> HintType.HiddenSingle(GroupType.Row(house.id))
				is House.Column -> HintType.HiddenSingle(GroupType.Column(house.id))
				is House.Block -> HintType.HiddenSingle(GroupType.Block(house.id))
			}
			return Hint(
				row = hintCell.row,
				col = hintCell.col,
				value = hintValue,
				type = hiddenSingleType,
				explanationStrategy = HiddenSingleExplanation()
			)

		}
		return null
	}

	// Now returns a list of hints representing eliminated candidates
	fun findLockedCandidate(house: House, data: Array<SudokuCellData>): List<Hint>? {
		return when (house) {
			is House.Block -> {
				val hints = findPointingCandidates(house, data)
				if (hints.isNotEmpty()) hints else null
			}

			is House.Row, is House.Column -> {
				val hints = findClaimingCandidates(house, data)
				if (hints.isNotEmpty()) hints else null
			}
		}
	}

	// Works on a Block house only
	fun findPointingCandidates(house: House.Block, data: Array<SudokuCellData>): List<Hint> {
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
				val rowCells = getRow(data, uniqueRows.first())
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
							enforcingCells = candidateCells
						)
					)
				}
			}

			// Candidate is pointing in a column
			if (uniqueCols.size == 1) {
				val colCells = getColumn(data, uniqueCols.first())
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
							enforcingCells = candidateCells
						)
					)
				}
			}
		}
		return hints
	}

	//  Works on a Row or Column house
	fun findClaimingCandidates(house: House, data: Array<SudokuCellData>): List<Hint> {
		if (house !is House.Row && house !is House.Column) {
			throw IllegalArgumentException("House must be a Row or Column")
		}
		val hints = mutableListOf<Hint>()
		val candidateDigits = house.cells.flatMap { it.candidates }.toSet()
		for (digit in candidateDigits) {
			val candidateCells = house.cells.filter { it.number == 0 && digit in it.candidates }
			if (candidateCells.isNotEmpty()) {
				// Check if candidate cells belong to the same block (using block index)
				val uniqueBlocks = candidateCells.map { (it.row / 3) * 3 + (it.col / 3) }.toSet()
				if (uniqueBlocks.size == 1) {
					val blockIndex = uniqueBlocks.first()
					val blockCells = getBox(data, blockIndex / 3, blockIndex % 3)
						.filter {
							!candidateCells.containsCell(it) && it.number == 0 && digit in it.candidates
						}

					blockCells.forEach {
						hints.add(
							Hint(
								row = it.row,
								col = it.col,
								value = digit,
								type = HintType.ClaimingCandidate(
									if (house is House.Row) GroupType.Row(
										it.row
									) else GroupType.Column(it.col)
								),
								explanationStrategy = ClaimingCandidateExplanation(),
								enforcingCells = candidateCells
							)
						)
					}
				}
			}
		}
		return hints
	}

	fun getPossibleValues(data: Array<SudokuCellData>, row: Int, col: Int): Set<Int> {
		if (data[row * 9 + col].number != 0) {
			return emptySet()
		}
		val rowValues = data.filter { it.row == row }.map { it.number }
		val colValues = data.filter { it.col == col }.map { it.number }
		val subgridValues =
			data.filter { it.row / 3 == row / 3 && it.col / 3 == col / 3 }.map { it.number }
		val allValues = (1..9).toSet()
		val possibleValues = allValues - rowValues - colValues - subgridValues
		return possibleValues
	}

	private fun getRow(data: Array<SudokuCellData>, row: Int): List<SudokuCellData> {
		return data.filter { it.row == row }
	}

	private fun getColumn(data: Array<SudokuCellData>, col: Int): List<SudokuCellData> {
		return data.filter { it.col == col }
	}

	private fun getBox(
		data: Array<SudokuCellData>,
		boxRow: Int,
		boxCol: Int
	): List<SudokuCellData> {
		val startRow = boxRow * 3
		val startCol = boxCol * 3
		return data.filter { it.row in startRow until startRow + 3 && it.col in startCol until startCol + 3 }
	}
}

fun Collection<SudokuCellData>.containsCell(cell: SudokuCellData): Boolean {
	return this.any { it.row == cell.row && it.col == cell.col }
}

fun HintProvider.fillCandidates(data: Array<SudokuCellData>): Array<SudokuCellData> {
	return data.map { cell ->
		if (cell.number == 0)
			cell.copy(candidates = getPossibleValues(data, cell.row, cell.col))
		else
			cell
	}.toTypedArray()
}