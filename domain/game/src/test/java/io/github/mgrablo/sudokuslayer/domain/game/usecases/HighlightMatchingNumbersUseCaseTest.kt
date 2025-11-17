package io.github.mgrablo.sudokuslayer.domain.game.usecases

import io.github.mgrablo.sudokucore.model.CellAttributes
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.model.updateCells
import io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals.HighlightMatchingNumbersUseCase
import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.plus
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HighlightMatchingNumbersUseCaseTest {

	@RelaxedMockK
	private lateinit var mockSettingsRepository: SettingsRepository

	private lateinit var useCase: HighlightMatchingNumbersUseCase

	private val sampleGridData = listOf(
		intArrayOf(1, 2, 0, 0),
		intArrayOf(3, 4, 1, 0),
		intArrayOf(0, 1, 0, 2),
		intArrayOf(0, 0, 4, 1),
	)

	@BeforeEach
	fun setUp() {
		MockKAnnotations.init(this)
		useCase = HighlightMatchingNumbersUseCase(mockSettingsRepository)

		every { mockSettingsRepository.highlightMatchingNumbers } returns flowOf(true)
	}

	private fun createGridWithHighlight(numberToHighlight: Int): SudokuGrid {
		val baseGrid = SudokuGrid.fromIntArray(sampleGridData, gridSize = 4)
		return baseGrid.updateCells(
			predicate = { it.number == numberToHighlight },
			transform = { it.copy(attributes = it.attributes + CellAttributes.NUMBER_MATCH_HIGHLIGHTED) },
		)
	}

	@Test
	fun `Highlighting matching numbers with valid number`() = runTest {
		val initialGrid = SudokuGrid.fromIntArray(sampleGridData, gridSize = 4)
		val numberToHighlight = 1

		val resultGrid = useCase(initialGrid, numberToHighlight)

		resultGrid.data.forEach { cell ->
			if (cell.number == numberToHighlight) {
				assertTrue(
					cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
					"Cell at (${cell.row}, ${cell.col}) with number $numberToHighlight should be highlighted.",
				)
			} else {
				assertFalse(
					cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
					"Cell at (${cell.row}, ${cell.col}) with number ${cell.number} should not be highlighted.",
				)
			}
		}
	}

	@Test
	fun `Highlighting with null number`() = runTest {
		val initialGridWithHighlights =
			createGridWithHighlight(1) // Start with some cells highlighted
		assertTrue(
			initialGridWithHighlights.data.any {
				it.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED)
			},
		)

		val resultGrid = useCase(initialGridWithHighlights, null)

		resultGrid.data.forEach { cell ->
			assertFalse(
				cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
				"Cell at (${cell.row}, ${cell.col}) should not have number match highlight when input is null.",
			)
		}
	}

	@Test
	fun `Highlighting with zero as number`() = runTest {
		val initialGridWithHighlights =
			createGridWithHighlight(2) // Start with some cells highlighted
		assertTrue(
			initialGridWithHighlights.data.any {
				it.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED)
			},
		)

		val resultGrid = useCase(initialGridWithHighlights, 0)

		resultGrid.data.forEach { cell ->
			assertFalse(
				cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
				"Cell at (${cell.row}, ${cell.col}) should not have number match highlight when input is 0.",
			)
		}
	}

	@Test
	fun `Grid state after clearing highlights`() = runTest {
		// Create a grid with cell (0,0) value 1 and already highlighted
		val initialGrid = SudokuGrid.fromIntArray(sampleGridData, gridSize = 4)
			.updateCell(
				0,
				0,
			) { it.copy(attributes = persistentSetOf(CellAttributes.NUMBER_MATCH_HIGHLIGHTED)) }

		// Highlight a different number, e.g., 2. This should clear previous highlight on 1.
		val numberToHighlight = 2
		val resultGrid = useCase(initialGrid, numberToHighlight)

		val cell00 = resultGrid.getCellAt(0, 0)
		assertFalse(
			cell00.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
			"Previously highlighted cell (0,0) for number 1 should be cleared.",
		)

		resultGrid.data.filter { it.number == numberToHighlight }.forEach { cell ->
			assertTrue(
				cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
				"Cell with number $numberToHighlight should be highlighted.",
			)
		}
	}

	@Test
	fun `Grid state with no matching numbers`() = runTest {
		val initialGrid = SudokuGrid.fromIntArray(sampleGridData, gridSize = 4)
		val numberToHighlight = 5 // This number is not in sampleGridData

		val resultGrid = useCase(initialGrid, numberToHighlight)

		resultGrid.data.forEach { cell ->
			assertFalse(
				cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
				"No cells should be highlighted if the number is not present.",
			)
		}
	}

	@Test
	fun `Grid state with all cells matching the number`() = runTest {
		val allMatchData = listOf(
			intArrayOf(7, 7, 7),
			intArrayOf(7, 7, 7),
			intArrayOf(7, 7, 7),
		)
		val initialGrid = SudokuGrid.fromIntArray(allMatchData, gridSize = 3)
		val numberToHighlight = 7

		val resultGrid = useCase(initialGrid, numberToHighlight)

		resultGrid.data.forEach { cell ->
			assertTrue(
				cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
				"All cells matching the number $numberToHighlight should be highlighted.",
			)
		}
	}

	@Test
	fun `Highlighting behavior based on SettingsRepository highlightMatching setting`() = runTest {
		val initialGrid = SudokuGrid.fromIntArray(sampleGridData, gridSize = 4)
		val numberToHighlight = 1

		every { mockSettingsRepository.highlightMatchingNumbers } returns flowOf(false)
		var resultGrid = useCase(initialGrid, numberToHighlight)

		assertFalse(
			resultGrid.getCellAt(0, 0).attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
			"Cell (0,0) should not be highlighted  if setting is false. ",
		)

		every { mockSettingsRepository.highlightMatchingNumbers } returns flowOf(true)
		resultGrid = useCase(initialGrid, numberToHighlight)
		assertTrue(
			resultGrid.getCellAt(0, 0).attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
			"Cell (0,0) should be highlighted when setting is true.",
		)

		resultGrid = useCase(initialGrid, null)
		assertFalse(
			resultGrid.getCellAt(0, 0).attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
			"Cell (0,0) should not be highlighted when number is null, regardless of setting.",
		)
	}

	@Test
	fun `Input SudokuGrid immutability or expected mutation`() = runTest {
		val initialGrid = SudokuGrid.fromIntArray(sampleGridData, gridSize = 4)
		val initialGridDataSnapshot =
			initialGrid.data.toList() // Save a deep copy of data for comparison

		val resultGrid = useCase(initialGrid, 1)

		assertNotSame(initialGrid, resultGrid, "UseCase should return a new SudokuGrid instance.")
		assertEquals(
			initialGridDataSnapshot.size,
			initialGrid.data.size,
			"Original grid size should not change.",
		)
		for (i in initialGridDataSnapshot.indices) {
			assertEquals(
				initialGridDataSnapshot[i],
				initialGrid.data[i],
				"Original grid cell data at index $i should remain unchanged.",
			)
		}
		// Verify a specific cell in original grid didn't get highlights
		assertFalse(
			initialGrid.getCellAt(
				0,
				0,
			).attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
			"Original grid cell (0,0) should not be modified.",
		)
	}

	@Test
	fun `Highlighting with number outside typical Sudoku range e g 9 or 1`() = runTest {
		val initialGrid = SudokuGrid.fromIntArray(sampleGridData, gridSize = 4)

		// Test with a number like 10 (not in grid)
		var resultGrid = useCase(initialGrid, 10)
		resultGrid.data.forEach { cell ->
			assertFalse(
				cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
				"No cells should be highlighted for number 10.",
			)
		}

		// Test with a negative number like -1 (not in grid)
		resultGrid = useCase(initialGrid, -1)
		resultGrid.data.forEach { cell ->
			assertFalse(
				cell.attributes.contains(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
				"No cells should be highlighted for number -1.",
			)
		}
	}
}
