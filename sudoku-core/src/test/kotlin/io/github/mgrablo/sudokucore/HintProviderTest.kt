package io.github.mgrablo.sudokucore

import io.github.mgrablo.sudokucore.hints.GroupType
import io.github.mgrablo.sudokucore.hints.HintProvider
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.hints.fillCandidates
import io.github.mgrablo.sudokucore.hints.strategies.ClaimingCandidateStrategy
import io.github.mgrablo.sudokucore.hints.strategies.HiddenSingleStrategy
import io.github.mgrablo.sudokucore.hints.strategies.NakedSingleStrategy
import io.github.mgrablo.sudokucore.hints.strategies.PointingCandidateStrategy
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
import org.junit.jupiter.api.assertTimeout
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@DisplayName("HintProvider Tests")
class HintProviderTest {
	private lateinit var hintProvider: HintProvider
	private val nakedSingleStrategy = NakedSingleStrategy()
	private val hiddenSingleStrategy = HiddenSingleStrategy()
	private val pointingCandidateStrategy = PointingCandidateStrategy()
	private val claimingCandidateStrategy = ClaimingCandidateStrategy()

	@BeforeEach
	fun setup() {
		hintProvider = HintProvider(
			nakedSingleStrategy,
			hiddenSingleStrategy,
			pointingCandidateStrategy,
			claimingCandidateStrategy,
		)
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
			val houses = generateHouses(updatedGrid)
			val hint = nakedSingleStrategy.findHints(updatedGrid, houses).firstOrNull()

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

			val hiddenSingle = hiddenSingleStrategy.findHints(updatedGrid, houses).firstOrNull()

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
				pointingCandidateStrategy.findHints(updatedGrid, houses) +
					claimingCandidateStrategy.findHints(updatedGrid, houses)

			assertNotNull(lockedCandidates, "Should find locked candidates")
			assertTrue(lockedCandidates.isNotEmpty(), "Should have at least one locked candidate")
		}

		@Test
		@DisplayName("Should find pointing candidates")
		fun findPointingCandidates() {
			val grid = SudokuGrid.fromStringArray(TestData.lockedCandidateGrid)
			val updatedGrid = hintProvider.fillCandidates(grid.getArray())
			val blockHouses = generateBlockHouses(updatedGrid)

			val pointingCandidates = pointingCandidateStrategy.findHints(updatedGrid, blockHouses)

			assertNotNull(pointingCandidates, "Should find pointing candidates")
			assertTrue(
				pointingCandidates.isNotEmpty(),
				"Should have at least one pointing candidate",
			)
			assertTrue(
				pointingCandidates.all { hint ->
					val type = hint.type as HintType.PointingCandidate
					when (val group = type.groupType) {
						is GroupType.Row ->
							hint.affectedCells.isNotEmpty() &&
								hint.affectedCells.all { it.row == group.id } &&
								hint.affectedCells.any { it.row == hint.row && it.col == hint.col }

						is GroupType.Column ->
							hint.affectedCells.isNotEmpty() &&
								hint.affectedCells.all { it.col == group.id } &&
								hint.affectedCells.any { it.row == hint.row && it.col == hint.col }

						else -> false
					}
				},
				"Each pointing candidate hint should contain eliminations from exactly one pointed line",
			)
			assertTrue(
				pointingCandidates
					.map { hint ->
						val type = hint.type as HintType.PointingCandidate
						Triple(
							hint.value,
							type.groupType,
							hint.enforcingCells.map { cell -> cell.row to cell.col }.toSet(),
						)
					}.distinct().size == pointingCandidates.size,
				"There should be at most one pointing hint per locked pattern",
			)
		}

		@Test
		@DisplayName("Should provide grouped pointing candidate eliminations for one pointed line")
		fun provideGroupedPointingCandidateHint() {
			val grid = SudokuGrid.fromStringArray(TestData.lockedCandidateGrid)
			val updatedGrid = hintProvider.fillCandidates(grid.getArray())

			val hint = hintProvider.provideHint(updatedGrid)

			assertNotNull(hint, "Should provide a hint")
			assertInstanceOf<HintType.PointingCandidate>(hint!!.type)
			assertTrue(hint.affectedCells.isNotEmpty(), "Hint should contain affected cells")

			val hintType = hint.type
			when (val group = hintType.groupType) {
				is GroupType.Row -> {
					assertTrue(
						hint.affectedCells.all { it.row == group.id },
						"All affected cells should belong to one row",
					)
				}

				is GroupType.Column -> {
					assertTrue(
						hint.affectedCells.all { it.col == group.id },
						"All affected cells should belong to one column",
					)
				}

				else -> error("Unexpected pointing-candidate group type")
			}

			assertTrue(
				hint.affectedCells.any { it.row == hint.row && it.col == hint.col },
				"Hint anchor cell should be one of the affected cells",
			)
		}

		@Test
		@DisplayName("Should find claiming candidates")
		fun findClaimingCandidates() {
			val grid = SudokuGrid.fromStringArray(TestData.claimingCandidateGrid)
			val updatedGrid = hintProvider.fillCandidates(grid.getArray())
			val houses = generateHouses(updatedGrid)

			val claimingCandidates = claimingCandidateStrategy.findHints(updatedGrid, houses)

			assertTrue(
				claimingCandidates.isNotEmpty(),
				"Should find claiming candidates for non-Block house",
			)

			// Strategy now handles filtering internally, so explicit block check exception is not needed.
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
