package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.generateHouses
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.withCandidates
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf

class PointingCandidateStrategyTest {
	private val pointingCandidateStrategy = PointingCandidateStrategy()

	@Test
	fun `should find pointing candidate in a row`() {
		// In block 0, digit 1 only appears in row 0, so it can be eliminated from row 0 outside the block
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
			.withCandidates(0, 1, 1, 3)
			.withCandidates(0, 3, 1, 4) // outside block 0, same row
			.withCandidates(1, 0, 2)
			.withCandidates(1, 1, 2)
		val houses = generateHouses(grid.data)

		val hints = pointingCandidateStrategy.findHints(grid.data, houses)

		val rowHint = hints.single {
			it is Hint.PointingCandidate &&
				it.groupType is Hint.GroupType.Row
		}
		assertInstanceOf<Hint.PointingCandidate>(rowHint)
		assertEquals(1, rowHint.number)
		assertEquals(Hint.GroupType.Row(0), rowHint.groupType)

		assertTrue(rowHint.enforcingCells.all { it.row == 0 && it.col < 3 })
		assertTrue(rowHint.affectedCells.all { it.row == 0 && it.col >= 3 })
		assertTrue(rowHint.affectedCells.isNotEmpty())
	}

	@Test
	fun `should find pointing candidate in a column`() {
		// In block 0, digit 1 only appears in column 0, so it can be eliminated from column 0 outside the block
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
			.withCandidates(1, 0, 1, 3)
			.withCandidates(3, 0, 1, 4) // outside block 0, same column
			.withCandidates(0, 1, 2)
			.withCandidates(1, 1, 2)
		val houses = generateHouses(grid.data)

		val hints = pointingCandidateStrategy.findHints(grid.data, houses)

		val colHint = hints.single {
			it is Hint.PointingCandidate &&
				it.groupType is Hint.GroupType.Column
		}
		assertInstanceOf<Hint.PointingCandidate>(colHint)
		assertEquals(1, colHint.number)
		assertEquals(Hint.GroupType.Column(0), colHint.groupType)

		assertTrue(colHint.enforcingCells.all { it.col == 0 && it.row < 3 })
		assertTrue(colHint.affectedCells.all { it.col == 0 && it.row >= 3 })
		assertTrue(colHint.affectedCells.isNotEmpty())
	}

	@Test
	fun `should return multiple hints when multiple pointing candidates exist`() {
		val grid = SudokuGrid()
			// block 0: row pointing candidate for digit 1
			.withCandidates(0, 0, 1, 2)
			.withCandidates(0, 1, 1, 3)
			.withCandidates(0, 3, 1, 4)
			// block 4: column pointing candidate for digit 9
			.withCandidates(3, 3, 9, 2)
			.withCandidates(4, 3, 9, 5)
			.withCandidates(6, 3, 9, 7)
		val houses = generateHouses(grid.data)

		val hints = pointingCandidateStrategy.findHints(grid.data, houses)

		assertTrue(hints.size >= 2)
		assertTrue(
			hints.any {
				it is Hint.PointingCandidate &&
					it.number == 1 &&
					it.groupType is Hint.GroupType.Row
			},
		)
		assertTrue(
			hints.any {
				it is Hint.PointingCandidate &&
					it.number == 9 &&
					it.groupType is Hint.GroupType.Column
			},
		)
	}

	@Test
	fun `should return empty list when no pointing candidate exists`() {
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
			.withCandidates(0, 1, 1, 3)
			.withCandidates(1, 0, 1, 4) // spread across multiple rows, so no pointing candidate
		val houses = generateHouses(grid.data)

		val hints = pointingCandidateStrategy.findHints(grid.data, houses)

		assertTrue(hints.isEmpty())
	}
}
