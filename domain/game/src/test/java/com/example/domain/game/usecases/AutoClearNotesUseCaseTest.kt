package com.example.domain.game.usecases

import com.example.domain.game.usecases.game.AutoClearNotesUseCase
import com.example.domain.settings.SettingsRepository
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AutoClearNotesUseCaseTest {
	@RelaxedMockK
	private lateinit var mockSettingsRepository: SettingsRepository
	private lateinit var autoClearNotesUseCase: AutoClearNotesUseCase

	@BeforeEach
	fun setUp() {
		MockKAnnotations.init(this)
		autoClearNotesUseCase = AutoClearNotesUseCase(mockSettingsRepository)
		every { mockSettingsRepository.autoClearNotes } returns flowOf(true)
	}

	@Test
	fun `Test with number 0`() = runTest {
		// Test if the function returns the original SudokuGrid when the input number is 0.
		val grid = SudokuGrid().withValue(0, 0, 1)
		val result = autoClearNotesUseCase(grid, 0, 0, 0).resultingGrid
		assert(result == grid)
	}

	@Test
	fun `Test with a number not present in any corner notes`() = runTest {
		// Test if the function returns the original SudokuGrid when the input number is not present in any corner notes of the relevant cells (same row, column, or block).
		val grid = SudokuGrid()
			.withReplacedCell(0, 1, SudokuCellData(0, 1, cornerNotes = persistentSetOf(2, 3)))
			.withReplacedCell(1, 0, SudokuCellData(1, 0, cornerNotes = persistentSetOf(4, 5)))
			.withReplacedCell(1, 1, SudokuCellData(1, 1, cornerNotes = persistentSetOf(6, 7)))

		val result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid

		assertEquals(grid, result)
	}

	@Test
	fun `Test with a number present in corner notes of cells in the same column`() = runTest {
		// Test if the function correctly removes the number from the corner notes of cells in the same column (excluding the input cell).
		val grid = SudokuGrid()
			.withReplacedCell(1, 0, SudokuCellData(1, 0, cornerNotes = persistentSetOf(1, 2)))
			.withReplacedCell(2, 0, SudokuCellData(2, 0, cornerNotes = persistentSetOf(1, 3)))
			.withReplacedCell(6, 1, SudokuCellData(6, 1, cornerNotes = persistentSetOf(1, 4)))

		val result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid

		assertTrue(result.getCellAt(1, 0).cornerNotes.containsAll(persistentSetOf(2)))
		assertTrue(result.getCellAt(2, 0).cornerNotes.containsAll(persistentSetOf(3)))
		assertTrue(
			result.getCellAt(6, 1).cornerNotes.containsAll(
				persistentSetOf(1, 4),
			),
		) // Unchanged
	}

	@Test
	fun `Test with a number present in corner notes of cells in the same row`() = runTest {
		// Test if the function correctly removes the number from the corner notes of cells in the same row (excluding the input cell).
		val grid = SudokuGrid()
			.withReplacedCell(0, 1, SudokuCellData(0, 1, cornerNotes = persistentSetOf(1, 2)))
			.withReplacedCell(0, 2, SudokuCellData(0, 2, cornerNotes = persistentSetOf(1, 3)))
			.withReplacedCell(6, 6, SudokuCellData(6, 6, cornerNotes = persistentSetOf(1, 4)))

		val result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid

		assertTrue(result.getCellAt(0, 1).cornerNotes.containsAll(persistentSetOf(2)))
		assertTrue(result.getCellAt(0, 2).cornerNotes.containsAll(persistentSetOf(3)))
		assertTrue(
			result.getCellAt(6, 6).cornerNotes.containsAll(
				persistentSetOf(1, 4),
			),
		) // Unchanged
	}

	@Test
	fun `Test with a number present in corner notes of cells in the same block`() = runTest {
		// Test if the function correctly removes the number from the corner notes of cells in the same block (excluding the input cell and cells with non-zero numbers).
		val grid = SudokuGrid()
			.withReplacedCell(1, 1, SudokuCellData(1, 1, cornerNotes = persistentSetOf(1, 2)))
			.withReplacedCell(2, 2, SudokuCellData(2, 2, cornerNotes = persistentSetOf(1, 3)))
			.withReplacedCell(
				3,
				3,
				SudokuCellData(3, 3, cornerNotes = persistentSetOf(1, 4)),
			) // Different block

		val result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid

		assertTrue(result.getCellAt(1, 1).cornerNotes.containsAll(persistentSetOf(2)))
		assertTrue(result.getCellAt(2, 2).cornerNotes.containsAll(persistentSetOf(3)))
		assertTrue(
			result.getCellAt(3, 3).cornerNotes.containsAll(
				persistentSetOf(
					1,
					4,
				),
			),
		) // Unchanged
	}

	@Test
	fun `Test with a number present in corner notes of cells in the same row  column  and block`() =
		runTest {
			// Test if the function correctly removes the number from the corner notes of all relevant cells when the number is present in cells belonging to the same row, column, and block.
			val grid = SudokuGrid()
				.withReplacedCell(
					row = 0,
					col = 1,
					cellData = SudokuCellData(0, 1, cornerNotes = persistentSetOf(1, 2)),
				) // Same row
				.withReplacedCell(
					row = 1,
					col = 0,
					cellData = SudokuCellData(1, 0, cornerNotes = persistentSetOf(1, 3)),
				) // Same col
				.withReplacedCell(
					row = 1,
					col = 1,
					cellData = SudokuCellData(1, 1, cornerNotes = persistentSetOf(1, 4)),
				) // Same block
				.withReplacedCell(
					row = 8,
					col = 8,
					cellData = SudokuCellData(8, 8, cornerNotes = persistentSetOf(1, 5)),
				) // Unrelated

			val result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid

			assertEquals(persistentSetOf(2), result.getCellAt(0, 1).cornerNotes)
			assertEquals(persistentSetOf(3), result.getCellAt(1, 0).cornerNotes)
			assertEquals(persistentSetOf(4), result.getCellAt(1, 1).cornerNotes)
			assertEquals(persistentSetOf(1, 5), result.getCellAt(8, 8).cornerNotes)
		}

	@Test
	fun `Test with an empty SudokuGrid`() = runTest {
		// Test the behavior of the function when an empty SudokuGrid is provided.
		// This is an edge case to ensure no crashes or unexpected behavior.
		val grid = SudokuGrid()
		val result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid
		assertEquals(grid, result)
	}

	@Test
	fun `Test with a SudokuGrid where all relevant cells have the number in their corner notes`() =
		runTest {
			// Test the scenario where all cells in the same row, column, and block (that are eligible for note clearing) have the target number in their corner notes.
			var grid = SudokuGrid()
			for (i in 0 until 9) {
				if (i != 4) {
					grid = grid.withReplacedCell(
						row = 4,
						col = i,
						cellData = SudokuCellData(4, i, cornerNotes = persistentSetOf(5)),
					) // row
				}
				if (i != 4) {
					grid = grid.withReplacedCell(
						row = i,
						col = 4,
						cellData = SudokuCellData(i, 4, cornerNotes = persistentSetOf(5)),
					) // col
				}
			}
			for (r in 3..5) {
				for (c in 3..5) {
					if (r != 4 || c != 4) {
						grid = grid.withReplacedCell(
							row = r,
							col = c,
							cellData = SudokuCellData(r, c, cornerNotes = persistentSetOf(5)),
						) // block
					}
				}
			}

			val result = autoClearNotesUseCase(grid, 4, 4, 5).resultingGrid

			for (i in 0 until 9) {
				if (i != 4) assertTrue(result.getCellAt(4, i).cornerNotes.isEmpty())
				if (i != 4) assertTrue(result.getCellAt(i, 4).cornerNotes.isEmpty())
			}
			for (r in 3..5) {
				for (c in 3..5) {
					if (r != 4 || c != 4) assertTrue(result.getCellAt(r, c).cornerNotes.isEmpty())
				}
			}
		}

	@Test
	fun `Test with a SudokuGrid where no cells have the number in their corner notes`() = runTest {
		// Test the scenario where no cells in the SudokuGrid have the target number in their corner notes.
		val grid = SudokuGrid()
			.withReplacedCell(0, 1, SudokuCellData(0, 1, cornerNotes = persistentSetOf(2, 3)))
			.withReplacedCell(1, 0, SudokuCellData(1, 0, cornerNotes = persistentSetOf(2, 3)))
			.withReplacedCell(1, 1, SudokuCellData(1, 1, cornerNotes = persistentSetOf(2, 3)))

		val result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid
		assertEquals(grid, result)
	}

	@Test
	fun `Test with various grid sizes`() = runTest {
		// Test the function with different valid Sudoku grid sizes (e.g., 4x4, 9x9, 16x16) to ensure it works correctly for all supported configurations.
		// 4x4 grid
		val grid4 = SudokuGrid(4)
			.withReplacedCell(0, 1, SudokuCellData(0, 1, cornerNotes = persistentSetOf(1, 2)))
			.withReplacedCell(1, 0, SudokuCellData(1, 0, cornerNotes = persistentSetOf(1, 3)))
		val result4 = autoClearNotesUseCase(grid4, 0, 0, 1).resultingGrid
		assertEquals(persistentSetOf(2), result4.getCellAt(0, 1).cornerNotes)
		assertEquals(persistentSetOf(3), result4.getCellAt(1, 0).cornerNotes)

		// 16x16 grid
		val grid16 = SudokuGrid(16)
			.withReplacedCell(0, 1, SudokuCellData(0, 1, cornerNotes = persistentSetOf(1, 2)))
			.withReplacedCell(1, 0, SudokuCellData(1, 0, cornerNotes = persistentSetOf(1, 3)))
		val result16 = autoClearNotesUseCase(grid16, 0, 0, 1).resultingGrid
		assertEquals(persistentSetOf(2), result16.getCellAt(0, 1).cornerNotes)
		assertEquals(persistentSetOf(3), result16.getCellAt(1, 0).cornerNotes)
	}

	@Test
	fun `Test with row and column at boundary conditions  0 and gridSize 1 `() = runTest {
		// Test the function when the input row and column are at the boundaries of the grid (e.g., first row/column, last row/column).
		val gridSize = 9
		// Top-left corner
		var grid = SudokuGrid(gridSize).withReplacedCell(
			0,
			1,
			SudokuCellData(0, 1, cornerNotes = persistentSetOf(1, 2)),
		)
		var result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid
		assertEquals(persistentSetOf(2), result.getCellAt(0, 1).cornerNotes)

		// Bottom-right corner
		grid = SudokuGrid(gridSize).withReplacedCell(
			row = gridSize - 1,
			col = gridSize - 2,
			cellData = SudokuCellData(
				gridSize - 1,
				gridSize - 2,
				cornerNotes = persistentSetOf(1, 2),
			),
		)
		result = autoClearNotesUseCase(grid, gridSize - 1, gridSize - 1, 1).resultingGrid
		assertEquals(
			persistentSetOf(2),
			result.getCellAt(gridSize - 1, gridSize - 2).cornerNotes,
		)
	}

	@Test
	fun `Test with a number that is the only note in a cell`() = runTest {
		// Test if the function correctly clears the corner notes when the target number is the only note present in a cell's corner notes, resulting in empty corner notes.
		val grid =
			SudokuGrid().withReplacedCell(
				row = 0,
				col = 1,
				cellData = SudokuCellData(0, 1, cornerNotes = persistentSetOf(1)),
			)
		val result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid
		assertTrue(result.getCellAt(0, 1).cornerNotes.isEmpty())
	}

	@Test
	fun `Test with a number that is one of multiple notes in a cell`() = runTest {
		// Test if the function correctly removes only the target number from the corner notes when multiple notes are present in a cell.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 1,
			cellData = SudokuCellData(0, 1, cornerNotes = persistentSetOf(1, 2, 3)),
		)
		val (result) = autoClearNotesUseCase(grid, 0, 0, 1)
		assertEquals(persistentSetOf(2, 3), result.getCellAt(0, 1).cornerNotes)
	}

	@Test
	fun `Test correct handling of cellChanges list`() = runTest {
		// Verify that the cellChanges list is populated correctly with the old and new cell states for each modification made.
		val grid = SudokuGrid()
			.withReplacedCell(0, 1, SudokuCellData(0, 1, cornerNotes = persistentSetOf(1, 2)))
			.withReplacedCell(1, 0, SudokuCellData(1, 0, cornerNotes = persistentSetOf(1, 3)))

		val (result, changes) = autoClearNotesUseCase(grid, 0, 0, 1)

		assertEquals(persistentSetOf(2), result.getCellAt(0, 1).cornerNotes)
		assertEquals(persistentSetOf(3), result.getCellAt(1, 0).cornerNotes)

		assertEquals(2, changes.size)

		val change1 = changes.find { it.oldCell.row == 1 && it.oldCell.col == 0 }
		val change2 = changes.find { it.oldCell.row == 0 && it.oldCell.col == 1 }

		assertTrue(change1 != null)
		assertEquals(persistentSetOf(1, 3), change1!!.oldCell.cornerNotes)
		assertEquals(persistentSetOf(3), change1.newCell.cornerNotes)

		assertTrue(change2 != null)
		assertEquals(persistentSetOf(1, 2), change2!!.oldCell.cornerNotes)
		assertEquals(persistentSetOf(2), change2.newCell.cornerNotes)
	}

	@Test
	fun `Test immutability of input SudokuGrid`() = runTest {
		// Ensure that the original SudokuGrid object passed as input is not modified, and a new SudokuGrid object is returned with the changes.
		val originalGrid =
			SudokuGrid().withReplacedCell(
				row = 0,
				col = 1,
				cellData = SudokuCellData(0, 1, cornerNotes = persistentSetOf(1, 2)),
			)
		val originalGridCopy = originalGrid.copy()

		val result = autoClearNotesUseCase(originalGrid, 0, 0, 1).resultingGrid

		assertNotSame(originalGrid, result)
		assertEquals(originalGridCopy, originalGrid) // Original grid should be unchanged
	}

	@Test
	fun `Test block clearing logic with different subgrid sizes`() = runTest {
		// Specifically test the block clearing logic with varying subgrid sizes (e.g., 2x2 for a 4x4 grid, 3x3 for a 9x9 grid) to ensure the block identification is correct.
		// 4x4 grid, 2x2 subgrid
		val grid4 = SudokuGrid(4)
			.withReplacedCell(
				row = 1,
				col = 1,
				cellData = SudokuCellData(1, 1, cornerNotes = persistentSetOf(1, 2)),
			) // Same block
			.withReplacedCell(
				row = 2,
				col = 2,
				cellData = SudokuCellData(2, 2, cornerNotes = persistentSetOf(1, 3)),
			) // Different block
		val result4 = autoClearNotesUseCase(grid4, 0, 0, 1).resultingGrid
		assertEquals(persistentSetOf(2), result4.getCellAt(1, 1).cornerNotes)
		assertEquals(persistentSetOf(1, 3), result4.getCellAt(2, 2).cornerNotes)
	}

	@Test
	fun `Test block clearing exclusion of cells with non zero numbers`() = runTest {
		// Verify that cells within the same block that already have a non-zero number are correctly excluded from note clearing, even if their corner notes contain the target number.
		val grid = SudokuGrid()
			.withReplacedCell(
				row = 1,
				col = 1,
				cellData = SudokuCellData(1, 1, 5, cornerNotes = persistentSetOf(1, 2)),
			)

		val result = autoClearNotesUseCase(grid, 0, 0, 1).resultingGrid

		assertEquals(persistentSetOf(1, 2), result.getCellAt(1, 1).cornerNotes)
	}
}
