package com.example.domain.game.usecases

import com.example.domain.game.usecases.input.InputNumberUseCase
import com.example.domain.game.usecases.visuals.MarkRuleBreakingCellsUseCase
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InputNumberUseCaseTest {
	private lateinit var markRuleBreakingCellsUseCase: MarkRuleBreakingCellsUseCase
	private lateinit var inputNumberUseCase: InputNumberUseCase

	@BeforeEach
	fun setup() {
		markRuleBreakingCellsUseCase = mockk(relaxed = true) {
			coEvery { this@mockk.invoke(any()) } answers { firstArg() }
		}
		inputNumberUseCase = InputNumberUseCase(markRuleBreakingCellsUseCase)
	}

	@Test
	fun `Input number in a generated cell`() = runTest {
		// Verify that attempting to input a number into a cell marked as GENERATED
		// does not modify the SudokuGrid and returns the original grid.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 1,
				attributes = persistentSetOf(
					CellAttributes.GENERATED,
				),
			),
		)
		val updateGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 5,
			row = 0,
			column = 0,
			isNote = false,
			isHint = false,
		)
		assert(updateGrid.getCellAt(0, 0).number == 1)
		assert(grid == updateGrid)
	}

	@Test
	fun `Clear cell by inputting zero`() = runTest {
		// Test that inputting '0' as the number clears the cell's number and corner notes.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 1,
			),
		)
		val updateGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 0,
			row = 0,
			column = 0,
			isNote = false,
			isHint = false,
		)
		assert(updateGrid.getCellAt(0, 0).number == 0)
		assert(updateGrid.getCellAt(0, 0).cornerNotes.isEmpty())
	}

	@Test
	fun `Add a new corner note`() = runTest {
		// When isNote is true and the number is not already in cornerNotes,
		// verify that the number is added to the cell's cornerNotes and the cell's number is set to 0.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 1,
				cornerNotes = persistentSetOf(),
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 2,
			row = 0,
			column = 0,
			isNote = true,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf(2))
		assert(updatedGrid.getCellAt(0, 0).number == 0)
	}

	@Test
	fun `Remove an existing corner note`() = runTest {
		// When isNote is true and the number is already in cornerNotes,
		// verify that the number is removed from the cell's cornerNotes and the cell's number is set to 0.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 0,
				cornerNotes = persistentSetOf(2),
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 2,
			row = 0,
			column = 0,
			isNote = true,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf<Int>())
		assert(updatedGrid.getCellAt(0, 0).number == 0)
	}

	@Test
	fun `Input a new number  not a note `() = runTest {
		// When isNote is false and the input number is different from the cell's current number,
		// verify that the cell's number is updated to the input number and cornerNotes are cleared.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 3,
				cornerNotes = persistentSetOf(1, 2, 3),
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 1,
			row = 0,
			column = 0,
			isNote = false,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).number == 1)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf<Int>())
	}

	@Test
	fun `Clear cell by inputting existing number  not a note `() = runTest {
		// When isNote is false and the input number is the same as the cell's current number,
		// verify that the cell's number is set to 0 and cornerNotes are cleared.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 1,
				cornerNotes = persistentSetOf(1, 2, 3),
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 1,
			row = 0,
			column = 0,
			isNote = false,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).number == 0)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf<Int>())
	}

	@Test
	fun `Input number as a hint`() = runTest {
		// When isHint is true and isNote is false,
		// verify that the cell's number is updated and the HINT_REVEALED attribute is added.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 0,
				cornerNotes = persistentSetOf(1, 2, 3),
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 1,
			row = 0,
			column = 0,
			isNote = false,
			isHint = true,
		)
		assert(updatedGrid.getCellAt(0, 0).number == 1)
		assert(
			updatedGrid.getCellAt(
				0,
				0,
			).attributes == persistentSetOf(CellAttributes.HINT_REVEALED),
		)
	}

	@Test
	fun `Input number not as a hint`() = runTest {
		// When isHint is false and isNote is false,
		// verify that the cell's number is updated and the HINT_REVEALED attribute is not added (or removed if present).
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 0,
				cornerNotes = persistentSetOf(1, 2, 3),
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 1,
			row = 0,
			column = 0,
			isNote = false,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).number == 1)
		assert(updatedGrid.getCellAt(0, 0).attributes == persistentSetOf<CellAttributes>())
	}

	@Test
	fun `Corner notes are sorted after adding a new note`() = runTest {
		// Verify that when a new corner note is added, the resulting cornerNotes set is sorted.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 0,
				cornerNotes = persistentSetOf(2, 5),
			),
		)
		var updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 4,
			row = 0,
			column = 0,
			isNote = true,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf(2, 4, 5))
		updatedGrid = inputNumberUseCase(
			sudokuGrid = updatedGrid,
			number = 8,
			row = 0,
			column = 0,
			isNote = true,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf(2, 4, 5, 8))
		updatedGrid = inputNumberUseCase(
			sudokuGrid = updatedGrid,
			number = 1,
			row = 0,
			column = 0,
			isNote = true,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf(1, 2, 4, 5, 8))
	}

	@Test
	fun `Rule breaking cells are cleared and marked after input`() = runTest {
		// After any valid input (not a generated cell),
		// ensure that `markRuleBreakingCellsUseCase` is called on the SudokuGrid.
		val grid =
			SudokuGrid().withReplacedCell(
				row = 0,
				col = 0,
				cellData = SudokuCellData(
					row = 0,
					col = 0,
					number = 1,
				),
			)

		val updatedGrid =
			inputNumberUseCase(
				sudokuGrid = grid,
				number = 1,
				row = 1,
				column = 0,
				isNote = false,
				isHint = false,
			)

		assert(updatedGrid.getCellAt(1, 0).number == 1)
		coVerify(exactly = 1) { markRuleBreakingCellsUseCase(any()) }
	}

	@Test
	fun `Edge case  Invalid row or column indices  out of bounds `() = runTest {
		// Verify that providing row or column indices outside the valid grid dimensions
		// (e.g., -1, or grid.size) is handled gracefully (e.g., throws an exception, as `getCellAt` likely would).
		assertThrows<IllegalArgumentException> {
			inputNumberUseCase(
				sudokuGrid = SudokuGrid(),
				number = 1,
				row = -1,
				column = 0,
				isNote = false,
				isHint = false,
			)
		}
	}

	@Test
	fun `Inputting a note when the cell already has a number`() = runTest {
		// When isNote is true, and the cell currently has a number (not 0),
		// verify that the existing number is set to 0 and the note is added/removed as expected.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 1,
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 2,
			row = 0,
			column = 0,
			isNote = true,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).number == 0)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf(2))
	}

	@Test
	fun `Inputting a number when the cell has existing notes`() = runTest {
		// When isNote is false and a number is inputted,
		// verify that any existing corner notes in the cell are cleared.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 0,
				cornerNotes = persistentSetOf(1, 2, 3),
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 1,
			row = 0,
			column = 0,
			isNote = false,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).number == 1)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf<Int>())
	}

	@Test
	fun `Inputting zero as a note`() = runTest {
		// When number is 0 and isNote is true, verify that the cell's number is set to 0 and corner notes are cleared
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 0,
				cornerNotes = persistentSetOf(1, 2, 3),
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 0,
			row = 0,
			column = 0,
			isNote = true,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).number == 0)
		assert(updatedGrid.getCellAt(0, 0).cornerNotes == persistentSetOf<Int>())
	}

	@Test
	fun `Cell already has HINT REVEALED attribute and isHint is false`() = runTest {
		// If a cell has HINT_REVEALED and a regular number is input (isHint=false),
		// ensure the HINT_REVEALED attribute is removed.
		val grid = SudokuGrid().withReplacedCell(
			row = 0,
			col = 0,
			cellData = SudokuCellData(
				row = 0,
				col = 0,
				number = 1,
				attributes = persistentSetOf(CellAttributes.HINT_REVEALED),
			),
		)
		val updatedGrid = inputNumberUseCase(
			sudokuGrid = grid,
			number = 2,
			row = 0,
			column = 0,
			isNote = false,
			isHint = false,
		)
		assert(updatedGrid.getCellAt(0, 0).number == 2)
		assert(updatedGrid.getCellAt(0, 0).attributes == persistentSetOf<Int>())
	}
}
