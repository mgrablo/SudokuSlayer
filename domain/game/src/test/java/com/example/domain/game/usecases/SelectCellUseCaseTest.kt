package com.example.domain.game.usecases

import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.addAttribute
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.collections.immutable.persistentSetOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SelectCellUseCaseTest {
	private lateinit var selectCellUseCase: SelectCellUseCase
	private lateinit var highlightRowAndColumnUseCase: HighlightRowAndColumnUseCase
	private lateinit var highlightMatchingNumbersUseCase: HighlightMatchingNumbersUseCase
	private lateinit var clearHighlightedRowAndColumnUseCase: ClearHighlightedRowAndColumnUseCase

	private val emptyGrid = SudokuGrid(9)

	@BeforeEach
	fun setUp() {
		highlightRowAndColumnUseCase = mockk(relaxed = true) {
			every { this@mockk.invoke(any(), any(), any()) } answers { firstArg() }
		}
		highlightMatchingNumbersUseCase = mockk(relaxed = true) {
			every { this@mockk.invoke(any(), any()) } answers { firstArg() }
		}
		clearHighlightedRowAndColumnUseCase = mockk(relaxed = true) {
			every { this@mockk.invoke(any()) } answers { firstArg() }
		}

		selectCellUseCase =
			SelectCellUseCase(
				highlightRowAndColumnUseCase,
				highlightMatchingNumbersUseCase,
				clearHighlightedRowAndColumnUseCase,
			)
	}

	@Test
	fun `SelectCellUseCase selectedCell is null`() {
		// Verify that when selectedCell is null, the SudokuGrid is returned with previously selected cells deselected and highlights cleared.
		val initialGrid =
			emptyGrid.withReplacedCell(
				0,
				0,
				emptyGrid.getCellAt(0, 0)
					.copy(attributes = persistentSetOf(CellAttributes.SELECTED)),
			)

		val resultGrid = selectCellUseCase(initialGrid, null)

		assert(!resultGrid.getCellAt(0, 0).attributes.contains(CellAttributes.SELECTED))
		verify { clearHighlightedRowAndColumnUseCase(any()) }
		verify(exactly = 0) { highlightRowAndColumnUseCase(any(), any(), any()) }
		verify(exactly = 1) { highlightMatchingNumbersUseCase(any(), any()) }
	}

	@Test
	fun `SelectCellUseCase selectedCell is not null  cell has no initial attributes`() {
		// Verify that when a valid cell is selected, it's marked as SELECTED, and row, column, and matching numbers are highlighted.
		// The selected cell initially has no attributes.
		val selectedCell = 1 to 2

		val resultGrid = selectCellUseCase(emptyGrid, selectedCell)

		assert(
			resultGrid.getCellAt(selectedCell.first, selectedCell.second).attributes.contains(
				CellAttributes.SELECTED,
			),
		)
		verify { highlightRowAndColumnUseCase(any(), selectedCell.first, selectedCell.second) }
	}

	@Test
	fun `SelectCellUseCase selectedCell is not null  cell has other attributes`() {
		// Verify that when a valid cell with pre-existing attributes is selected, the SELECTED attribute is added without removing existing ones,
		// and row, column, and matching numbers are highlighted.
		val selectedCell = 1 to 2
		val otherAttribute = CellAttributes.GENERATED
		val initialGrid =
			emptyGrid.withReplacedCell(
				selectedCell.first,
				selectedCell.second,
				emptyGrid.getCellAt(selectedCell.first, selectedCell.second)
					.copy(attributes = persistentSetOf(otherAttribute)),
			)

		val resultGrid = selectCellUseCase(initialGrid, selectedCell)

		assert(
			resultGrid.getCellAt(selectedCell.first, selectedCell.second).attributes.contains(
				CellAttributes.SELECTED,
			),
		)
		assert(
			resultGrid.getCellAt(selectedCell.first, selectedCell.second).attributes.contains(
				otherAttribute,
			),
		)
	}

	@Test
	fun `SelectCellUseCase selecting a new cell when another cell is already selected`() {
		// Verify that if a cell is already selected, selecting a new cell correctly deselects the old one, clears its highlights,
		// and selects and highlights the new cell and its related elements.
		val oldSelectedCell = 0 to 0
		val newSelectedCell = 1 to 1
		val initialGrid =
			emptyGrid.withReplacedCell(
				oldSelectedCell.first,
				oldSelectedCell.second,
				emptyGrid.getCellAt(oldSelectedCell.first, oldSelectedCell.second)
					.copy(attributes = persistentSetOf(CellAttributes.SELECTED)),
			)

		val resultGrid = selectCellUseCase(initialGrid, newSelectedCell)

		assert(
			!resultGrid.getCellAt(
				oldSelectedCell.first,
				oldSelectedCell.second,
			).attributes.contains(
				CellAttributes.SELECTED,
			),
		)
		assert(
			resultGrid.getCellAt(newSelectedCell.first, newSelectedCell.second).attributes.contains(
				CellAttributes.SELECTED,
			),
		)
		verify {
			highlightRowAndColumnUseCase(
				any(),
				newSelectedCell.first,
				newSelectedCell.second,
			)
		}
	}

	@Test
	fun `SelectCellUseCase selected cell has a number`() {
		// Verify that highlightMatchingNumbersUseCase is called with the correct number from the selected cell.
		val selectedCell = 3 to 4
		val number = 5
		val initialGrid = emptyGrid.withValue(selectedCell.first, selectedCell.second, number)

		selectCellUseCase(initialGrid, selectedCell)

		verify { highlightMatchingNumbersUseCase(any(), number) }
	}

	@Test
	fun `SelectCellUseCase selected cell is empty  number is null or 0 `() {
		// Verify that highlightMatchingNumbersUseCase is called with null or 0 if the selected cell is empty, and handles it gracefully (no numbers highlighted).
		val selectedCell = 2 to 2
		val cell = emptyGrid.getCellAt(selectedCell.first, selectedCell.second)
		assert(cell.number == 0)

		selectCellUseCase(emptyGrid, selectedCell)

		verify(exactly = 0) { highlightMatchingNumbersUseCase(any(), any()) }
	}

	@Test
	fun `SelectCellUseCase SudokuGrid has pre existing selected cells`() {
		// Verify that any cells in the input SudokuGrid that are already marked with CellAttributes.SELECTED are correctly deselected before processing the new selectedCell.
		val preSelectedCell = 4 to 4
		val newSelectedCell = 5 to 5
		val initialGrid =
			emptyGrid.withReplacedCell(
				preSelectedCell.first,
				preSelectedCell.second,
				emptyGrid.getCellAt(preSelectedCell.first, preSelectedCell.second)
					.copy(attributes = persistentSetOf(CellAttributes.SELECTED)),
			)

		val resultGrid = selectCellUseCase(initialGrid, newSelectedCell)

		assert(
			!resultGrid.getCellAt(
				preSelectedCell.first,
				preSelectedCell.second,
			).attributes.contains(
				CellAttributes.SELECTED,
			),
		)
		assert(
			resultGrid.getCellAt(
				newSelectedCell.first,
				newSelectedCell.second,
			).attributes.contains(CellAttributes.SELECTED),
		)
	}

	@Test
	fun `SelectCellUseCase interaction with highlightRowAndColumnUseCase`() {
		// Ensure highlightRowAndColumnUseCase is called with the correct SudokuGrid (after deselection and clearing) and the correct row/column from selectedCell.
		val selectedCell = 2 to 3
		selectCellUseCase(emptyGrid, selectedCell)

		verifyOrder {
			clearHighlightedRowAndColumnUseCase(any())
			highlightRowAndColumnUseCase(any(), selectedCell.first, selectedCell.second)
		}
	}

	@Test
	fun `SelectCellUseCase interaction with highlightMatchingNumbersUseCase`() {
		// Ensure highlightMatchingNumbersUseCase is called with the correct SudokuGrid (after selection and row/column highlighting) and the correct number from the selected cell.
		val selectedCell = 2 to 3
		val number = 7
		val initialGrid = emptyGrid.withValue(selectedCell.first, selectedCell.second, number)

		selectCellUseCase(initialGrid, selectedCell)

		verifyOrder {
			highlightRowAndColumnUseCase(any(), selectedCell.first, selectedCell.second)
			highlightMatchingNumbersUseCase(any(), number)
		}
	}

	@Test
	fun `SelectCellUseCase immutability of input SudokuGrid`() {
		// Confirm that the original sudoku object passed to the invoke method is not mutated, and a new instance is returned.
		val initialGrid = emptyGrid.withValue(0, 0, 5)
		val initialGridCopy = initialGrid.copy()

		val resultGrid = selectCellUseCase(initialGrid, 1 to 1)

		assert(resultGrid != initialGrid)
		assert(initialGrid == initialGridCopy) // No mutation
	}

	@Test
	fun `selecting an empty cell should not clear existing number highlights`() {
		// 1. Setup a grid with some highlighted numbers
		val gridWithHighlights =
			emptyGrid.addAttribute({ it.row == 0 }, CellAttributes.NUMBER_MATCH_HIGHLIGHTED)

		// 2. Select an empty cell
		val emptyCellToSelect = 1 to 1
		assert(
			gridWithHighlights.getCellAt(
				emptyCellToSelect.first,
				emptyCellToSelect.second,
			).number == 0,
		)

		selectCellUseCase(gridWithHighlights, emptyCellToSelect)

		// 3. Verify that clearHighlightedNumbersUseCase was NOT called
		verify(exactly = 0) { highlightMatchingNumbersUseCase(any(), null) }
	}

	@Test
	fun `SelectCellUseCase with an empty SudokuGrid  all cells empty `() {
		// Test behavior when a cell is selected in an entirely empty Sudoku grid.
		val selectedCell = 0 to 0
		val resultGrid = selectCellUseCase(emptyGrid, selectedCell)

		assert(
			resultGrid.getCellAt(
				selectedCell.first,
				selectedCell.second,
			).attributes.contains(CellAttributes.SELECTED),
		)

		verify { highlightRowAndColumnUseCase(any(), selectedCell.first, selectedCell.second) }
		verify(exactly = 0) { highlightMatchingNumbersUseCase(any(), any()) }
	}

	@Test
	fun `SelectCellUseCase with a fully solved SudokuGrid`() {
		// Test behavior when a cell is selected in a fully solved Sudoku grid, ensuring highlights work as expected.
		// Create a simple solved grid
		var solvedGrid: SudokuGrid = emptyGrid
		for (i in 0..8) {
			solvedGrid = solvedGrid.withValue(i, i, i + 1)
		}
		val selectedCell = 4 to 4
		val selectedNumber = solvedGrid.getCellAt(selectedCell.first, selectedCell.second).number

		val resultGrid = selectCellUseCase(solvedGrid, selectedCell)

		assert(
			resultGrid.getCellAt(
				selectedCell.first,
				selectedCell.second,
			).attributes.contains(CellAttributes.SELECTED),
		)

		verify { highlightRowAndColumnUseCase(any(), selectedCell.first, selectedCell.second) }
		verify { highlightMatchingNumbersUseCase(any(), selectedNumber) }
	}
}
