package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.generateHouses
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.withCandidates
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertNotNull

class HiddenSingleStrategyTest {
	private val hiddenSingleStrategy = HiddenSingleStrategy()

	@Test
	fun `should find hidden single in a row`() {
		// In row 0, digit 1 only appears as a candidate in (0,0)
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
			.withCandidates(0, 1, 2)
		val houses = generateHouses(grid.data)

		val hints = hiddenSingleStrategy.findHints(grid.data, houses)

		val rowHint = hints.single {
			it is Hint.HiddenSingle && it.groupType is Hint.GroupType.Row
		}
		assertInstanceOf<Hint.HiddenSingle>(rowHint)
		assertEquals(0, rowHint.row)
		assertEquals(0, rowHint.col)
		assertEquals(1, rowHint.number)
	}

	@Test
	fun `should find hidden single in a column`() {
		// In column 0, digit 1 only appears as a candidate in (0,0)
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
			.withCandidates(1, 0, 2)
			.withCandidates(0, 1, 1) // Breaks Row 0 hidden single
			.withCandidates(1, 1, 1) // Breaks Block 0 hidden single
		val houses = generateHouses(grid.data)

		val hints = hiddenSingleStrategy.findHints(grid.data, houses)

		val colHint = hints.single {
			it is Hint.HiddenSingle && it.groupType is Hint.GroupType.Column
		}
		assertInstanceOf<Hint.HiddenSingle>(colHint)
		assertEquals(0, colHint.row)
		assertEquals(0, colHint.col)
		assertEquals(1, colHint.number)
	}

	@Test
	fun `should find hidden single in a block`() {
		// In block 0 (top-left), digit 1 only appears as a candidate in (0,0)
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2) // the block hidden single candidate
			.withCandidates(0, 1, 2)
			.withCandidates(1, 0, 2)
			.withCandidates(1, 1, 2)
		val houses = generateHouses(grid.data)

		val hints = hiddenSingleStrategy.findHints(grid.data, houses)

		val blockHint = hints.find {
			it is Hint.HiddenSingle &&
				it.groupType is Hint.GroupType.Block &&
				it.number == 1 && it.row == 0 && it.col == 0
		}
		assertNotNull(blockHint)
		assertInstanceOf<Hint.HiddenSingle>(blockHint)
		assertEquals(0, blockHint.row)
		assertEquals(0, blockHint.col)
		assertEquals(1, blockHint.number)
	}

	@Test
	fun `should return all hidden singles when multiple exist`() {
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2) // 1 can only go into (0, 0)
			.withCandidates(0, 1, 2)
			.withCandidates(8, 8, 9, 7) // 9 can only go into (8, 8)
			.withCandidates(8, 7, 7)
		val houses = generateHouses(grid.data)

		val hints = hiddenSingleStrategy.findHints(grid.data, houses)

		assertTrue(hints.any { it is Hint.HiddenSingle && it.number == 1 && it.row == 0 && it.col == 0 })
		assertTrue(hints.any { it is Hint.HiddenSingle && it.number == 9 && it.row == 8 && it.col == 8 })
	}

	@Test
	fun `should return empty list when no hidden single is found`() {
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
			.withCandidates(0, 1, 1, 2)
			.withCandidates(1, 0, 1, 2)
			.withCandidates(1, 1, 1, 2)
		val houses = generateHouses(grid.data)

		val hints = hiddenSingleStrategy.findHints(grid.data, houses)

		assertTrue(hints.isEmpty())
	}
}
