package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.generateHouses
import io.github.mgrablo.sudokucore.hints.GroupType
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.withCandidates
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

class ClaimingCandidateStrategyTest {
	private val claimingCandidateStrategy = ClaimingCandidateStrategy()

	@Test
	fun `should find claiming candidate in a row`() {
		// In row 0, digit 1 only appears inside block 0, so it can be eliminated from other cells in block 0
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
			.withCandidates(0, 1, 1, 3)
			.withCandidates(1, 1, 1) // affected cell in block 0
			.withCandidates(4, 1, 1) // 1 outside block 0 to break column candidate
		val houses = generateHouses(grid.data)

		val hints = claimingCandidateStrategy.findHints(grid.data, houses)

		val rowHint = hints.singleOrNull {
			it.type is HintType.ClaimingCandidate &&
				it.type.groupType is GroupType.Row
		}
		assertNotNull(rowHint)
		assertEquals(1, rowHint.row)
		assertEquals(1, rowHint.col)
		assertEquals(1, rowHint.value)

		val rowType = rowHint.type as HintType.ClaimingCandidate
		assertEquals(GroupType.Row(0), rowType.groupType)

		assertTrue(rowHint.enforcingCells.all { it.row == 0 && it.col < 3 })
		assertTrue(rowHint.affectedCells.all { it.row / 3 == 0 && it.col / 3 == 0 })
		assertTrue(rowHint.affectedCells.isNotEmpty())
	}

	@Test
	fun `should find claiming candidate in a column`() {
		// In column 0, digit 1 only appears inside block 0, so it can be eliminated from other cells in block 0
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
			.withCandidates(1, 0, 1, 3)
			.withCandidates(0, 1, 1) // same block, same column, keeps the pattern active
			.withCandidates(0, 4, 1) // 1 outside block 0 to break row candidate
		val houses = generateHouses(grid.data)

		val hints = claimingCandidateStrategy.findHints(grid.data, houses)

		val colHint = hints.singleOrNull {
			it.type is HintType.ClaimingCandidate &&
				it.type.groupType is GroupType.Column
		}
		assertNotNull(colHint)
		assertEquals(0, colHint.row)
		assertEquals(1, colHint.col)
		assertEquals(1, colHint.value)

		val colType = colHint.type as HintType.ClaimingCandidate
		assertEquals(GroupType.Column(0), colType.groupType)

		assertTrue(colHint.enforcingCells.all { it.col == 0 && it.row < 3 })
		assertTrue(colHint.affectedCells.all { it.row / 3 == 0 && it.col / 3 == 0 })
		assertTrue(colHint.affectedCells.isNotEmpty())
	}

	@Test
	fun `should return multiple hints when multiple claiming candidates exist`() {
		val grid = SudokuGrid()
			// row 0 => claiming candidate for digit 1 in block 0
			.withCandidates(0, 0, 1, 2)
			.withCandidates(0, 1, 1, 3)
			.withCandidates(1, 0, 1, 4)
			.withCandidates(1, 1, 2)
			// column 8 => claiming candidate for digit 9 in block 2
			.withCandidates(0, 8, 9, 5)
			.withCandidates(1, 8, 9, 6)
			.withCandidates(2, 7, 9, 7)
		val houses = generateHouses(grid.data)

		val hints = claimingCandidateStrategy.findHints(grid.data, houses)

		assertTrue(hints.size >= 2)
		assertTrue(
			hints.any {
				it.value == 1 && it.type is HintType.ClaimingCandidate &&
					it.type.groupType is GroupType.Row
			},
		)
		assertTrue(
			hints.any {
				it.value == 9 && it.type is HintType.ClaimingCandidate &&
					it.type.groupType is GroupType.Column
			},
		)
	}

	@Test
	fun `should return empty list when no claiming candidate exists`() {
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
			.withCandidates(0, 4, 1, 3) // same row, different block
			.withCandidates(1, 0, 1, 4) // different row, same block
		val houses = generateHouses(grid.data)

		val hints = claimingCandidateStrategy.findHints(grid.data, houses)

		assertTrue(hints.isEmpty())
	}
}
