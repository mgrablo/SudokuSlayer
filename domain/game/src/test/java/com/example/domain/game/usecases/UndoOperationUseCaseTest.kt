package com.example.domain.game.usecases

import com.example.domain.core.Operation
import com.example.domain.core.OperationRepository
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class UndoOperationUseCaseTest {

	private lateinit var operationRepository: OperationRepository
	private lateinit var inputNumberUseCase: InputNumberUseCase
	private lateinit var undoOperationUseCase: UndoOperationUseCase

	private val initialGrid = SudokuGrid()
	private val updatedGrid = SudokuGrid().withValue(0, 0, 1)

	@BeforeEach
	fun setUp() {
		operationRepository = mockk()
		inputNumberUseCase = mockk()
		undoOperationUseCase = spyk(UndoOperationUseCase(operationRepository, inputNumberUseCase))

		coJustRun { operationRepository.addRedoOperation(any()) }
		coEvery { operationRepository.findUndoOperation(any()) } returns mockk { every { id } returns 1L }
		coJustRun { operationRepository.removeUndoOperation(any()) }
		coEvery {
			inputNumberUseCase.invoke(
				sudokuGrid = any(),
				number = any(),
				row = any(),
				column = any(),
				isNote = any(),
				isHint = any(),
			)
		} returns updatedGrid
	}

	@Test
	fun `UndoOperationUseCase processing flag already set`() = runTest {
		// Test that if `isProcessing` is already true, the original grid is returned immediately.
		val isProcessingField = undoOperationUseCase.javaClass.getDeclaredField("isProcessing")
		isProcessingField.isAccessible = true
		(isProcessingField.get(undoOperationUseCase) as AtomicBoolean).set(true)

		val result = undoOperationUseCase(initialGrid)

		assertEquals(initialGrid, result)
		coVerify(exactly = 0) { operationRepository.getUndoOperations() }
	}

	@Test
	fun `UndoOperationUseCase no undo operations available`() = runTest {
		// Test that if `operationRepository.getUndoOperations()` returns an empty list, the original grid is returned.
		coEvery { operationRepository.getUndoOperations() } returns emptyList()

		val result = undoOperationUseCase(initialGrid)

		assertEquals(initialGrid, result)
		coVerify { operationRepository.getUndoOperations() }
		confirmVerified(operationRepository)
	}

	@Test
	fun `UndoOperationUseCase successful undo of a number input`() = runTest {
		// Test a successful undo operation where the last operation was a number input (isNote is false).
		// Verify `inputNumberUseCase` is called with correct parameters and the updated grid is returned.
		val oldCell = SudokuCellData(0, 0, 0)
		val newCell = SudokuCellData(0, 0, 5)
		val lastOperation = Operation(1, newCell, oldCell)
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)

		val result = undoOperationUseCase(initialGrid)

		assertEquals(updatedGrid, result)
		coVerify {
			inputNumberUseCase(
				sudokuGrid = initialGrid,
				number = 0,
				row = 0,
				column = 0,
				isNote = false,
				isHint = false,
			)
		}
	}

	@Test
	fun `UndoOperationUseCase successful undo of a single note input`() = runTest {
		// Test a successful undo operation where the last operation was adding a single note.
		// Verify `inputNumberUseCase` is called once with correct parameters and the updated grid is returned.
		val oldCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1))
		val newCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1, 2))
		val lastOperation = Operation(1, newCell, oldCell)
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)

		val result = undoOperationUseCase(initialGrid)

		assertEquals(updatedGrid, result)
		coVerify {
			inputNumberUseCase(
				sudokuGrid = initialGrid,
				number = 2,
				row = 0,
				column = 0,
				isNote = true,
				isHint = false,
			)
		}
	}

	@Test
	fun `UndoOperationUseCase successful undo of multiple notes input`() = runTest {
		// Test a successful undo operation where the last operation involved multiple corner notes changes.
		// Verify `inputNumberUseCase` is called for each differing note and the final updated grid is returned.
		val oldCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1))
		val newCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1, 2, 3))
		val lastOperation = Operation(1, newCell, oldCell)
		val gridAfterNote2 = SudokuGrid().withValue(0, 0, 2)
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)
		coEvery { inputNumberUseCase(any(), 2, any(), any(), true, any()) } returns gridAfterNote2
		coEvery { inputNumberUseCase(any(), 3, any(), any(), true, any()) } returns updatedGrid

		val result = undoOperationUseCase(initialGrid)

		assertEquals(updatedGrid, result)
		coVerify { inputNumberUseCase(initialGrid, 2, 0, 0, isNote = true, isHint = false) }
		coVerify { inputNumberUseCase(gridAfterNote2, 3, 0, 0, isNote = true, isHint = false) }
	}

	@Test
	fun `UndoOperationUseCase successful undo of number input over notes restores notes`() = runTest {
		// Test that when a number is placed on a cell with notes, undoing brings the notes back.
		val oldCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1, 2))
		val newCell = SudokuCellData(0, 0, 5, cornerNotes = persistentSetOf())
		val lastOperation = Operation(1, newCell, oldCell)
		val gridAfterNote1 = SudokuGrid().withValue(0, 0, 1) // Dummy grid
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)
		coEvery {
			inputNumberUseCase(
				any(),
				1,
				any(),
				any(),
				true,
				any(),
			)
		} returns gridAfterNote1
		coEvery { inputNumberUseCase(any(), 2, any(), any(), true, any()) } returns updatedGrid

		val result = undoOperationUseCase(initialGrid)

		assertEquals(updatedGrid, result)
		coVerify { inputNumberUseCase(initialGrid, 1, 0, 0, isNote = true, isHint = false) }
		coVerify { inputNumberUseCase(gridAfterNote1, 2, 0, 0, isNote = true, isHint = false) }
	}

	@Test
	fun `UndoOperationUseCase successful undo of a hint revealed cell`() = runTest {
		// Test that when undoing an operation on a cell that was hint-revealed, the `isHint` parameter is correctly passed as true to `inputNumberUseCase`.
		val oldCell =
			SudokuCellData(0, 0, 0, attributes = persistentSetOf(CellAttributes.HINT_REVEALED))
		val newCell =
			SudokuCellData(0, 0, 5, attributes = persistentSetOf(CellAttributes.HINT_REVEALED))
		val lastOperation = Operation(1, newCell, oldCell)
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)

		undoOperationUseCase(initialGrid)

		coVerify {
			inputNumberUseCase(
				sudokuGrid = initialGrid,
				number = 0,
				row = 0,
				column = 0,
				isNote = false,
				isHint = true,
			)
		}
	}

	@Test
	fun `UndoOperationUseCase successful undo of a non hint revealed cell`() = runTest {
		// Test that when undoing an operation on a cell that was not hint-revealed, the `isHint` parameter is correctly passed as false to `inputNumberUseCase`.
		val oldCell = SudokuCellData(0, 0, 0)
		val newCell = SudokuCellData(0, 0, 5)
		val lastOperation = Operation(1, newCell, oldCell)
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)

		undoOperationUseCase(initialGrid)

		coVerify {
			inputNumberUseCase(
				sudokuGrid = initialGrid,
				number = 0,
				row = 0,
				column = 0,
				isNote = false,
				isHint = false,
			)
		}
	}

	@Test
	fun `UndoOperationUseCase operation repository interactions verification`() = runTest {
		// Verify that `operationRepository.addRedoOperation` and `operationRepository.removeUndoOperation` are called with the correct arguments during a successful undo.
		val oldCell = SudokuCellData(0, 0, 0)
		val newCell = SudokuCellData(0, 0, 5)
		val lastOperation = Operation(1, newCell, oldCell)
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)
		coEvery { operationRepository.findUndoOperation(lastOperation.id) } returns lastOperation

		undoOperationUseCase(initialGrid)

		coVerify { operationRepository.addRedoOperation(lastOperation) }
		coVerify { operationRepository.removeUndoOperation(lastOperation.id) }
	}

	@Test
	fun `UndoOperationUseCase mutex locking behavior`() = runTest {
		// Test that the critical section of the code is properly protected by the mutex
		val oldCell = SudokuCellData(0, 0, 0)
		val newCell = SudokuCellData(0, 0, 5)
		val lastOperation = Operation(1, newCell, oldCell)
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)

		undoOperationUseCase(initialGrid)

		val mutex =
			undoOperationUseCase.javaClass.getDeclaredField("mutex").apply { isAccessible = true }
				.get(undoOperationUseCase) as kotlinx.coroutines.sync.Mutex
		assertFalse(mutex.isLocked)
	}

	@Test
	fun `UndoOperationUseCase isProcessing flag reset on exception`() = runTest {
		// Test that the `isProcessing` flag is reset to false in the `finally` block even if an exception occurs within the `try` block.
		val testException = RuntimeException("Test Exception")
		coEvery { operationRepository.getUndoOperations() } throws testException

		try {
			undoOperationUseCase(initialGrid)
		} catch (e: RuntimeException) {
			assertEquals(testException, e)
		}

		val isProcessingField = undoOperationUseCase.javaClass.getDeclaredField("isProcessing")
		isProcessingField.isAccessible = true
		val isProcessing = isProcessingField.get(undoOperationUseCase) as AtomicBoolean
		assertFalse(isProcessing.get())
	}

	private suspend fun testExceptionHandling(setup: () -> Unit) {
		setup()
		try {
			undoOperationUseCase(initialGrid)
		} catch (e: Exception) {
			// Expected
		}
		val isProcessingField = undoOperationUseCase.javaClass.getDeclaredField("isProcessing")
		isProcessingField.isAccessible = true
		assertFalse((isProcessingField.get(undoOperationUseCase) as AtomicBoolean).get())
	}

	@Test
	fun `UndoOperationUseCase exception in getUndoOperations`() = runTest {
		// Test how the use case handles an exception thrown by `operationRepository.getUndoOperations()`.
		// Ensure `isProcessing` is reset.
		testExceptionHandling {
			coEvery { operationRepository.getUndoOperations() } throws RuntimeException("DB error")
		}
	}

	@Test
	fun `UndoOperationUseCase exception in addRedoOperation`() = runTest {
		// Test how the use case handles an exception thrown by `operationRepository.addRedoOperation()`.
		// Ensure `isProcessing` is reset.
		val lastOperation = Operation(1, SudokuCellData(0, 0, 5), SudokuCellData(0, 0, 0))
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)
		testExceptionHandling {
			coEvery { operationRepository.addRedoOperation(any()) } throws RuntimeException("DB error")
		}
	}

	@Test
	fun `UndoOperationUseCase exception in findUndoOperation`() = runTest {
		// Test how the use case handles an exception thrown by `operationRepository.findUndoOperation()`.
		// Ensure `isProcessing` is reset.
		val lastOperation = Operation(1, SudokuCellData(0, 0, 5), SudokuCellData(0, 0, 0))
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)
		testExceptionHandling {
			coEvery { operationRepository.findUndoOperation(any()) } throws RuntimeException("DB error")
		}
	}

	@Test
	fun `UndoOperationUseCase exception in removeUndoOperation`() = runTest {
		// Test how the use case handles an exception thrown by `operationRepository.removeUndoOperation()`.
		// Ensure `isProcessing` is reset.
		val lastOperation = Operation(1, SudokuCellData(0, 0, 5), SudokuCellData(0, 0, 0))
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)
		testExceptionHandling {
			coEvery { operationRepository.removeUndoOperation(any()) } throws RuntimeException("DB error")
		}
	}

	@Test
	fun `UndoOperationUseCase exception in inputNumberUseCase note input `() = runTest {
		// Test how the use case handles an exception thrown by `inputNumberUseCase` when `isNote` is true.
		// Ensure `isProcessing` is reset.
		val oldCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1))
		val newCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1, 2))
		val lastOperation = Operation(1, newCell, oldCell)
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)
		testExceptionHandling {
			coEvery {
				inputNumberUseCase(
					sudokuGrid = any(),
					number = 2,
					row = 0,
					column = 0,
					isNote = true,
					isHint = false,
				)
			} throws RuntimeException("Input error")
		}
	}

	@Test
	fun `UndoOperationUseCase exception in inputNumberUseCase number input `() = runTest {
		// Test how the use case handles an exception thrown by `inputNumberUseCase` when `isNote` is false.
		// Ensure `isProcessing` is reset.
		val lastOperation = Operation(1, SudokuCellData(0, 0, 5), SudokuCellData(0, 0, 0))
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)
		testExceptionHandling {
			coEvery {
				inputNumberUseCase(
					any(),
					0,
					0,
					0,
					isNote = false,
					isHint = false,
				)
			} throws RuntimeException("Input error")
		}
	}

	@Test
	fun `UndoOperationUseCase lastOperation oldCell and newCell have identical corner notes`() =
		runTest {
			// Test the case where `symmetricDifference` is empty because oldCell and newCell corner notes are the same.
			// This should trigger the `else` branch for number input.
			val oldCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1))
			val newCell = SudokuCellData(0, 0, 5, cornerNotes = persistentSetOf(1))
			val lastOperation = Operation(1, newCell, oldCell)
			coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)

			undoOperationUseCase(initialGrid)

			coVerify { inputNumberUseCase(initialGrid, 0, 0, 0, isNote = false, isHint = false) }
		}

	@Test
	fun `UndoOperationUseCase lastOperation oldCell number is not 0 when symmetricDifference is not empty`() =
		runTest {
			// Test the case where `symmetricDifference` is not empty, but `oldCell.number` is not 0.
			// This should also trigger the `else` branch for number input, effectively undoing a number placement that might have also involved notes.
			val oldCell = SudokuCellData(0, 0, 9, cornerNotes = persistentSetOf(1))
			val newCell = SudokuCellData(0, 0, 9, cornerNotes = persistentSetOf(1, 2))
			val lastOperation = Operation(1, newCell, oldCell)
			coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)

			undoOperationUseCase(initialGrid)

			coVerify {
				inputNumberUseCase(
					sudokuGrid = initialGrid,
					number = 9,
					row = 0,
					column = 0,
					isNote = false,
					isHint = false,
				)
			}
		}

	@Test
	fun `UndoOperationUseCase with empty symmetric difference and oldCell number is 0`() = runTest {
		// Test the scenario where symmetricDifference is empty (no note changes) and oldCell.number is 0.
		// This implies undoing an empty cell to an empty cell, should fall into the else branch and input 0.
		val oldCell = SudokuCellData(0, 0, 0)
		val newCell = SudokuCellData(0, 0, 5)
		val lastOperation = Operation(1, newCell, oldCell)
		coEvery { operationRepository.getUndoOperations() } returns listOf(lastOperation)

		undoOperationUseCase(initialGrid)

		coVerify { inputNumberUseCase(initialGrid, 0, 0, 0, isNote = false, isHint = false) }
	}
}
