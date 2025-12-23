package io.github.mgrablo.sudokucore

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintProvider
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.hints.fillCandidates
import io.github.mgrablo.sudokucore.model.House
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.assertTimeout
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@DisplayName("HintProvider Tests")
class HintProviderTest {
	private lateinit var hintProvider: HintProvider

	@BeforeEach
	fun setup() {
		hintProvider = HintProvider()
	}

	@Nested
	@DisplayName("Basic Hint Tests")
	inner class BasicHints {
		@Test
		fun `getPossibleValues should return correct candidates`() {
			val grid = SudokuGrid.fromStringArray(TestData.standardGrid)
			val possibleValues = hintProvider.getPossibleValues(grid.getArray(), 0, 1)

			assertEquals(
				setOf(3, 6, 8, 9),
				possibleValues.toSet(),
				"Expected possible values {3,6,8,9} at position (0,1)",
			)
		}

		@Test
		fun `findNakedSingle should identify correct cell and value`() {
			val grid = SudokuGrid.fromStringArray(TestData.standardGrid)
			val updatedGrid = hintProvider.fillCandidates(grid.getArray())
			val hint = hintProvider.findNakedSingle(updatedGrid)

			assertNotNull(hint, "Should find a naked single")
			assertAll(
				{ assertEquals(3, hint!!.row, "Naked single should be in row 3") },
				{ assertEquals(2, hint!!.col, "Naked single should be in column 2") },
				{ assertEquals(8, hint!!.value, "Naked single value should be 8") },
				{ assertEquals(HintType.NakedSingle, hint!!.type) },
			)
		}
	}

	@Nested
	@DisplayName("Advanced Hint Tests")
	inner class AdvancedHints {
		@Test
		@DisplayName("Should find hidden single")
		fun findHiddenSingle() {
			val grid = SudokuGrid.fromStringArray(TestData.hiddenSingleGrid)
			val updatedGrid = hintProvider.fillCandidates(grid.getArray())
			val houses = generateHouses(updatedGrid)

			val hiddenSingle =
				houses.firstNotNullOfOrNull { house ->
					hintProvider.findHiddenSingle(house)
				}

			assertNotNull(hiddenSingle, "Should find a hidden single")
			assertInstanceOf<HintType.HiddenSingle>(hiddenSingle!!.type)
		}

		@Test
		@DisplayName("Should find locked candidates")
		fun findLockedCandidate() {
			val grid = SudokuGrid.fromStringArray(TestData.lockedCandidateGrid)
			val updatedGrid = hintProvider.fillCandidates(grid.getArray())
			val houses = generateHouses(updatedGrid)

			val lockedCandidates =
				houses.firstNotNullOfOrNull { house ->
					hintProvider.findLockedCandidate(house, updatedGrid)
				}

			assertNotNull(lockedCandidates, "Should find locked candidates")
			assertTrue(lockedCandidates!!.isNotEmpty(), "Should have at least one locked candidate")
		}

		@Test
		@DisplayName("Should find pointing candidates")
		fun findPointingCandidates() {
			val grid = SudokuGrid.fromStringArray(TestData.lockedCandidateGrid)
			val updatedGrid = hintProvider.fillCandidates(grid.getArray())
			val blockHouses = generateBlockHouses(updatedGrid)

			val pointingCandidates =
				blockHouses
					.mapNotNull { house ->
						hintProvider.findPointingCandidates(house, updatedGrid)
							.takeIf { it.isNotEmpty() }
					}
					.firstOrNull()

			assertNotNull(pointingCandidates, "Should find pointing candidates")
			assertTrue(
				pointingCandidates!!.isNotEmpty(),
				"Should have at least one pointing candidate",
			)
		}

		@Test
		@DisplayName("Should throw exception for House.Block in claiming candidates")
		fun findClaimingCandidates() {
			val grid = SudokuGrid.fromStringArray(TestData.claimingCandidateGrid)
			val updatedGrid = hintProvider.fillCandidates(grid.getArray())
			val houses = generateHouses(updatedGrid)

			val nonBlockHouses = houses.filter { it !is House.Block }
			// this call should work without throwing an exception
			val claimingCandidates: MutableList<Hint> = mutableListOf()
			for (house in nonBlockHouses) {
				val result = hintProvider.findClaimingCandidates(house, updatedGrid)
				if (result.isNotEmpty()) {
					claimingCandidates += result.first()
					break
				}
			}
			assertTrue(
				claimingCandidates.isNotEmpty(),
				"Should find claiming candidates for non-Block house",
			)

			val blockHouse = houses.first { it is House.Block }
			assertThrows<IllegalArgumentException>(
				"Should throw exception for House.Block in claiming candidates",
			) {
				hintProvider.findClaimingCandidates(blockHouse, updatedGrid)
			}
		}
	}

	@Test
	@DisplayName("Should handle empty grid within timeout")
	fun emptyGridTest() {
		val grid = SudokuGrid()
		assertTimeout(1.seconds.toJavaDuration()) {
			val hint = hintProvider.provideHint(data = grid.getArray())
			assertNull(hint, "Should not provide hints for empty grid")
		}
	}

	private fun generateHouses(grid: Collection<SudokuCellData>) = buildList {
		(0..8).forEach { i ->
			add(House.Row(grid.filter { it.row == i }, i))
			add(House.Column(grid.filter { it.col == i }, i))
			add(House.Block(grid.filter { it.row / 3 == i / 3 && it.col / 3 == i % 3 }, i))
		}
	}

	private fun generateBlockHouses(grid: Collection<SudokuCellData>) = buildList {
		for (blockRow in 0 until 3) {
			for (blockCol in 0 until 3) {
				val blockCells =
					grid.filter {
						it.row / 3 == blockRow && it.col / 3 == blockCol
					}
				add(House.Block(blockCells, blockRow * 3 + blockCol))
			}
		}
	}
}
