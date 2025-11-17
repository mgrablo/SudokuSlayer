package io.github.mgrablo.sudokuslayer.domain.game.usecases

import io.github.mgrablo.sudokucore.model.CellAttributes
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokuslayer.domain.core.Operation
import io.github.mgrablo.sudokuslayer.domain.core.OperationRepository
import io.github.mgrablo.sudokuslayer.domain.core.changedTo
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.InputNumberUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.RedoOperationUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals.HighlightMatchingNumbersUseCase
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class RedoOperationUseCaseTest {

	private lateinit var operationRepository: OperationRepository
	private lateinit var inputNumberUseCase: InputNumberUseCase
	private lateinit var highlightMatchingNumbersUseCase: HighlightMatchingNumbersUseCase
	private lateinit var redoOperationUseCase: RedoOperationUseCase

	private val initialGrid = SudokuGrid()
	private val updatedGrid = SudokuGrid().withValue(0, 0, 1)

	@BeforeEach
	fun setUp() {
		operationRepository = mockk()
		inputNumberUseCase = mockk()
		highlightMatchingNumbersUseCase = mockk()
		redoOperationUseCase = spyk(
			RedoOperationUseCase(
				operationRepository,
				inputNumberUseCase,
				highlightMatchingNumbersUseCase,
			),
		)

		coJustRun { operationRepository.addUndoOperation(any()) }
		coEvery { operationRepository.findRedoOperation(any()) } returns mockk { every { id } returns 1L }
		coJustRun { operationRepository.removeRedoOperation(any()) }
		coEvery { highlightMatchingNumbersUseCase(any(), any()) } returns updatedGrid
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
	fun `RedoOperationUseCase processing flag already set`() = runTest {
		// Test that if `isProcessing` is already true, the original grid is returned immediately.
		val isProcessingField = redoOperationUseCase.javaClass.getDeclaredField("isProcessing")
		isProcessingField.isAccessible = true
		(isProcessingField.get(redoOperationUseCase) as AtomicBoolean).set(true)

		val result = redoOperationUseCase(initialGrid)

		assertEquals(initialGrid, result)
		coVerify(exactly = 0) { operationRepository.getRedoOperations() }
	}

	@Test
	fun `RedoOperationUseCase no redo operations available`() = runTest {
		// Test that if `operationRepository.getRedoOperations()` returns an empty list, the original grid is returned.
		coEvery { operationRepository.getRedoOperations() } returns emptyList()

		val result = redoOperationUseCase(initialGrid)

		assertEquals(initialGrid, result)
		coVerify { operationRepository.getRedoOperations() }
		confirmVerified(operationRepository)
	}

	@Test
	fun `RedoOperationUseCase successful redo of a number input`() = runTest {
		// Test a successful redo operation where the last operation was a number input (isNote is false).
		val oldCell = SudokuCellData(0, 0, 0)
		val newCell = SudokuCellData(0, 0, 5)
		val lastOperation = Operation(1, oldCell changedTo newCell)
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)

		val result = redoOperationUseCase(initialGrid)

		assertEquals(updatedGrid, result)
		coVerify {
			inputNumberUseCase(
				sudokuGrid = initialGrid,
				number = 5,
				row = 0,
				column = 0,
				isNote = false,
				isHint = false,
			)
		}
	}

	@Test
	fun `RedoOperationUseCase successful redo of a single note input`() = runTest {
		// Test a successful redo operation where the last operation was adding a single note.
		val oldCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1))
		val newCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1, 2))
		val lastOperation = Operation(1, oldCell changedTo newCell)
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)

		val result = redoOperationUseCase(initialGrid)

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
	fun `RedoOperationUseCase successful redo of notes input over number restores notes`() = runTest {
		// Test that when a note is placed on a cell with number, redoing brings the notes back.
		val oldCell = SudokuCellData(0, 0, 5, cornerNotes = persistentSetOf())
		val newCell = SudokuCellData(0, 0, 0, cornerNotes = persistentSetOf(1, 2))
		val lastOperation = Operation(1, oldCell changedTo newCell)
		val gridAfterNote1 = SudokuGrid().withValue(0, 0, 1) // Dummy grid
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)
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

		val redoUseCase = RedoOperationUseCase(
			operationRepository,
			inputNumberUseCase,
			highlightMatchingNumbersUseCase,
		)
		val result = redoUseCase(initialGrid)

		assertEquals(updatedGrid, result)
		coVerify { inputNumberUseCase(initialGrid, 1, 0, 0, isNote = true, isHint = false) }
		coVerify { inputNumberUseCase(gridAfterNote1, 2, 0, 0, isNote = true, isHint = false) }
	}

	@Test
	fun `RedoOperationUseCase successful redo of a hint revealed cell`() = runTest {
		// Test that when redoing an operation on a cell that was hint-revealed, the `isHint` parameter is correctly passed as true.
		val oldCell =
			SudokuCellData(0, 0, 0, attributes = persistentSetOf(CellAttributes.HINT_REVEALED))
		val newCell =
			SudokuCellData(0, 0, 5, attributes = persistentSetOf(CellAttributes.HINT_REVEALED))
		val lastOperation = Operation(1, oldCell changedTo newCell)
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)

		redoOperationUseCase(initialGrid)

		coVerify {
			inputNumberUseCase(
				sudokuGrid = initialGrid,
				number = 5,
				row = 0,
				column = 0,
				isNote = false,
				isHint = true,
			)
		}
	}

	@Test
	fun `RedoOperationUseCase operation repository interactions verification`() = runTest {
		// Verify that `addUndoOperation`, `findRedoOperation`, and `removeRedoOperation` are called correctly.
		val oldCell = SudokuCellData(0, 0, 0)
		val newCell = SudokuCellData(0, 0, 5)
		val lastOperation = Operation(1, oldCell changedTo newCell)
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)
		coEvery { operationRepository.findRedoOperation(lastOperation.id) } returns lastOperation

		redoOperationUseCase(initialGrid)

		coVerify { operationRepository.addUndoOperation(lastOperation) }
		coVerify { operationRepository.findRedoOperation(lastOperation.id) }
		coVerify { operationRepository.removeRedoOperation(lastOperation.id) }
	}

	@Test
	fun `RedoOperationUseCase last operation not found for removal`() = runTest {
		// Test that if `findRedoOperation` returns null, `removeRedoOperation` is not called.
		val oldCell = SudokuCellData(0, 0, 0)
		val newCell = SudokuCellData(0, 0, 5)
		val lastOperation = Operation(1, oldCell changedTo newCell)
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)
		coEvery { operationRepository.findRedoOperation(lastOperation.id) } returns null

		redoOperationUseCase(initialGrid)

		coVerify { operationRepository.addUndoOperation(lastOperation) }
		coVerify { operationRepository.findRedoOperation(lastOperation.id) }
		coVerify(exactly = 0) { operationRepository.removeRedoOperation(any()) }
	}

	@Test
	fun `RedoOperationUseCase mutex locking behavior`() = runTest {
		// Test that the critical section of the code is properly protected by the mutex.
		val oldCell = SudokuCellData(0, 0, 0)
		val newCell = SudokuCellData(0, 0, 5)
		val lastOperation = Operation(1, oldCell changedTo newCell)
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)

		redoOperationUseCase(initialGrid)

		val mutex =
			redoOperationUseCase.javaClass.getDeclaredField("mutex").apply { isAccessible = true }
				.get(redoOperationUseCase) as Mutex
		assertFalse(mutex.isLocked)
	}

	@Test
	fun `RedoOperationUseCase isProcessing flag reset on exception`() = runTest {
		// Test that the `isProcessing` flag is reset to false even if an exception occurs.
		val testException = RuntimeException("Test Exception")
		coEvery { operationRepository.getRedoOperations() } throws testException

		try {
			redoOperationUseCase(initialGrid)
		} catch (e: RuntimeException) {
			assertEquals(testException, e)
		}

		val isProcessingField = redoOperationUseCase.javaClass.getDeclaredField("isProcessing")
		isProcessingField.isAccessible = true
		val isProcessing = isProcessingField.get(redoOperationUseCase) as AtomicBoolean
		assertFalse(isProcessing.get())
	}

	private suspend fun testExceptionHandling(setup: () -> Unit) {
		setup()
		try {
			redoOperationUseCase(initialGrid)
		} catch (e: Exception) {
			// Expected
		}
		val isProcessingField = redoOperationUseCase.javaClass.getDeclaredField("isProcessing")
		isProcessingField.isAccessible = true
		assertFalse((isProcessingField.get(redoOperationUseCase) as AtomicBoolean).get())
	}

	@Test
	fun `RedoOperationUseCase exception in getRedoOperations`() = runTest {
		testExceptionHandling {
			coEvery { operationRepository.getRedoOperations() } throws RuntimeException("DB error")
		}
	}

	@Test
	fun `RedoOperationUseCase exception in addUndoOperation`() = runTest {
		val lastOperation = Operation(1, SudokuCellData(0, 0, 5) changedTo SudokuCellData(0, 0, 0))
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)
		testExceptionHandling {
			coEvery { operationRepository.addUndoOperation(any()) } throws RuntimeException("DB error")
		}
	}

	@Test
	fun `RedoOperationUseCase exception in findRedoOperation`() = runTest {
		val lastOperation = Operation(1, SudokuCellData(0, 0, 5) changedTo SudokuCellData(0, 0, 0))
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)
		testExceptionHandling {
			coEvery { operationRepository.findRedoOperation(any()) } throws RuntimeException("DB error")
		}
	}

	@Test
	fun `RedoOperationUseCase exception in removeRedoOperation`() = runTest {
		val lastOperation = Operation(1, SudokuCellData(0, 0, 5) changedTo SudokuCellData(0, 0, 0))
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)
		testExceptionHandling {
			coEvery { operationRepository.removeRedoOperation(any()) } throws RuntimeException("DB error")
		}
	}

	@Test
	fun `RedoOperationUseCase exception in inputNumberUseCase`() = runTest {
		val lastOperation = Operation(1, SudokuCellData(0, 0, 5) changedTo SudokuCellData(0, 0, 0))
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)
		testExceptionHandling {
			coEvery {
				inputNumberUseCase(any(), any(), any(), any(), any(), any())
			} throws RuntimeException("Input error")
		}
	}

	@Test
	fun `RedoOperationUseCase highlight matching numbers`() = runTest {
		/**
		 * Test that `highlightMatchingNumbersUseCase` is called after a successful redo operation.
		 * This ensures that after a number is redone, the UI correctly highlights matching numbers.
		 */
		val lastOperation = Operation(1, SudokuCellData(0, 0, 5) changedTo SudokuCellData(0, 0, 0))
		coEvery { operationRepository.getRedoOperations() } returns listOf(lastOperation)
		redoOperationUseCase(initialGrid)

		coVerify(exactly = 1) { highlightMatchingNumbersUseCase(any(), 0) }
	}
}
