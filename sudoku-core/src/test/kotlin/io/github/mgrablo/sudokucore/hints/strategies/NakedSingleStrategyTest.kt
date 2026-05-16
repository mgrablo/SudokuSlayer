package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.generateHouses
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.withCandidates
import kotlinx.collections.immutable.persistentSetOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf

class NakedSingleStrategyTest {
	private val nakedSingleStrategy = NakedSingleStrategy()

	@Test
	fun `should find naked single in a row`() {
		val grid = SudokuGrid.fromStringArray(
			listOf(
				"123456780",
				"000000000",
				"000000000",
				"000000000",
				"000000000",
				"000000000",
				"000000000",
				"000000000",
				"000000000",
			),
		).withCandidates(0, 8, 9) // Only digit 9 can go into (0, 8)
		val houses = generateHouses(grid.data)

		val hints = nakedSingleStrategy.findHints(grid.data, houses)
		val hint = assertInstanceOf<Hint.NakedSingle>(hints[0])

		assertTrue(hints.all { it is Hint.NakedSingle })
		assertEquals(1, hints.size)
		assertEquals(0, hint.row)
		assertEquals(8, hint.col)
		assertEquals(9, hint.number)
	}

	@Test
	fun `should find naked single in a column`() {
		val grid = SudokuGrid.fromStringArray(
			listOf(
				"100000000",
				"200000000",
				"300000000",
				"400000000",
				"500000000",
				"600000000",
				"700000000",
				"800000000",
				"000000000",
			),
		).withCandidates(8, 0, 9) // Only digit 9 can go into (8, 0)
		val houses = generateHouses(grid.data)

		val hints = nakedSingleStrategy.findHints(grid.data, houses)
		val hint = assertInstanceOf<Hint.NakedSingle>(hints[0])

		assertTrue(hints.all { it is Hint.NakedSingle })
		assertEquals(1, hints.size)
		assertEquals(8, hint.row)
		assertEquals(0, hint.col)
		assertEquals(9, hint.number)
	}

	@Test
	fun `should find naked single in a block`() {
		val grid = SudokuGrid.fromStringArray(
			listOf(
				"123000000",
				"406000000",
				"789000000",
				"000000000",
				"000000000",
				"000000000",
				"000000000",
				"000000000",
				"000000000",
			),
		).withCandidates(1, 1, 5) // Only digit 5 can go into (1, 1)
		val houses = generateHouses(grid.data)

		val hints = nakedSingleStrategy.findHints(grid.data, houses)
		val hint = assertInstanceOf<Hint.NakedSingle>(hints[0])

		assertEquals(1, hints.size)
		assertEquals(1, hint.row)
		assertEquals(1, hint.col)
		assertEquals(5, hint.number)
	}

	@Test
	fun `should return all naked singles when multiple exist`() {
		val grid = SudokuGrid.fromStringArray(
			listOf(
				"103456789",
				"200000000",
				"300000000",
				"400000000",
				"500000000",
				"600000000",
				"700000000",
				"800000000",
				"000000000",
			),
		).withCandidates(8, 0, 9) // Only digit 9 can go into (8, 0)
			.withCandidates(0, 1, 2) // Only digit 2 can go into (0, 1)
		val houses = generateHouses(grid.data)

		val hints = nakedSingleStrategy.findHints(grid.data, houses)

		assertEquals(2, hints.size)
		assertEquals(
			9,
			hints.filterIsInstance<Hint.NakedSingle>().find { it.row == 8 && it.col == 0 }?.number,
		)
		assertEquals(
			2,
			hints.filterIsInstance<Hint.NakedSingle>().find { it.row == 0 && it.col == 1 }?.number,
		)
	}

	@Test
	fun `should ignore cells with more than one candidate`() {
		val grid = SudokuGrid()
			.withCandidates(0, 0, 1, 2)
		val houses = generateHouses(grid.data)

		val hints = nakedSingleStrategy.findHints(grid.data, houses)

		assertEquals(0, hints.size)
	}

	@Test
	fun `should ignore filled cells even if they have one candidate`() {
		val grid = SudokuGrid()
			.updateCell(0, 0) {
				it.copy(number = 5, candidates = persistentSetOf(1))
			}
		val houses = generateHouses(grid.data)

		val hints = nakedSingleStrategy.findHints(grid.data, houses)

		assertEquals(0, hints.size)
	}

	@Test
	fun `should return empty list when no naked single is found`() {
		val grid = SudokuGrid()
		val houses = generateHouses(grid.data)

		val hints = nakedSingleStrategy.findHints(grid.data, houses)

		assertEquals(0, hints.size)
	}
}
