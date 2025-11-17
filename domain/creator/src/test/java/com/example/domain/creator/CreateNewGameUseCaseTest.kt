package com.example.domain.creator

import com.example.sudoku.generator.ClassicSudokuGenerator
import com.example.sudoku.model.SolutionGrid
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.SudokuPuzzle
import com.example.sudokuslayer.domain.core.GameDifficulty
import com.example.sudokuslayer.domain.core.OperationRepository
import com.example.sudokuslayer.domain.core.SudokuGridSize
import com.example.sudokuslayer.domain.core.toCellsToRemove
import com.example.sudokuslayer.domain.creator.CreateNewGameUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class CreateNewGameUseCaseTest {

	private lateinit var useCase: CreateNewGameUseCase
	private lateinit var operationRepository: OperationRepository

	@BeforeEach
	fun setUp() {
		operationRepository = mockk(relaxed = true)
		coEvery { operationRepository.clearOperations() } returns Unit
		useCase = CreateNewGameUseCase(operationRepository)
	}

	@AfterEach
	fun tearDown() {
		unmockkAll()
	}

	@Test
	fun `Test game creation with a specific seed`() = runTest {
		// Verify that providing a specific seed results in a deterministic Sudoku grid generation.
		val seed = 12345L
		val game1 = useCase(SudokuGridSize.NINE, GameDifficulty.Easy, seed)
		val game2 = useCase(SudokuGridSize.NINE, GameDifficulty.Easy, seed)

		assertEquals(game1.grid, game2.grid)
	}

	@Test
	fun `Test game creation with a null seed`() = runTest {
		// Verify that providing a null seed results in a randomly generated Sudoku grid using Random.nextLong().
		val game1 = useCase(SudokuGridSize.NINE, GameDifficulty.Easy, null)
		val game2 = useCase(SudokuGridSize.NINE, GameDifficulty.Easy, null)

		assertNotEquals(game1.grid, game2.grid)
	}

	@ParameterizedTest
	@EnumSource(SudokuGridSize::class)
	fun `Test game creation for each SudokuGridSize`(gridSize: SudokuGridSize) = runTest {
		// Iterate through all possible SudokuGridSize enum values (e.g., FOUR, NINE, SIXTEEN) and ensure a game is created successfully for each.
		val game = useCase(gridSize, GameDifficulty.Easy, 1L)
		assertNotNull(game)
		assertEquals(gridSize.toIntSize(), game.grid.gridSize)
	}

	@ParameterizedTest
	@EnumSource(GameDifficulty::class)
	fun `Test game creation for each GameDifficulty`(difficulty: GameDifficulty) = runTest {
		// Iterate through all possible GameDifficulty enum values (e.g., Easy, Medium, Hard, Expert) and ensure a game is created successfully for each.
		val gridSize = SudokuGridSize.NINE
		val game = useCase(gridSize, difficulty, 1L)
		assertNotNull(game)
		assertEquals(difficulty, game.difficulty)
	}

	@Test
	fun `Test  operationRepository clearOperations    is called`() = runTest {
		// Verify that the `clearOperations` method on the `operationRepository` is invoked exactly once when the use case is executed.
		useCase(SudokuGridSize.NINE, GameDifficulty.Easy, 1L)
		coVerify(exactly = 1) { operationRepository.clearOperations() }
		confirmVerified(operationRepository)
	}

	@Test
	fun `Test  difficulty toCellsToRemove    is called with correct gridSize`() = runTest {
		// Verify that the `toCellsToRemove` method on the `difficulty` object is called with the correct `gridSize`.
		val gridSize = SudokuGridSize.NINE
		val seed = 1L

		mockkStatic("com.example.domain.core.GameDifficultyKt")
		every { GameDifficulty.Easy.toCellsToRemove(gridSize, seed) } returns 30

		useCase(gridSize, GameDifficulty.Easy, 1L)

		verify(exactly = 1) { GameDifficulty.Easy.toCellsToRemove(gridSize, seed) }
	}

	@Test
	fun `Test  generator createSudoku    is called with correct parameters  specific seed `() =
		runTest {
			// Verify that `createSudoku` on the `generator` is called with the correct `cellsToRemove` and the provided non-null `seed`.
			val gridSize = SudokuGridSize.NINE
			val difficulty = GameDifficulty.Easy
			val seed = 123L
			val cellsToRemove = difficulty.toCellsToRemove(gridSize, seed)
			val sudokuPuzzle = mockk<SudokuPuzzle>()
			val sudokuGrid = mockk<SudokuGrid>()
			val solutionGrid = mockk<SolutionGrid>()
			every { sudokuPuzzle.component1() } returns sudokuGrid
			every { sudokuPuzzle.component2() } returns solutionGrid

			mockkConstructor(ClassicSudokuGenerator::class)
			val cellsToRemoveSlot = slot<Int>()
			val seedSlot = slot<Long>()
			coEvery {
				anyConstructed<ClassicSudokuGenerator>().createSudoku(
					capture(cellsToRemoveSlot),
					capture(seedSlot),
				)
			} returns sudokuPuzzle

			useCase(gridSize, difficulty, seed)

			assertEquals(cellsToRemove, cellsToRemoveSlot.captured)
			assertEquals(seed, seedSlot.captured)
		}

	@Test
	fun `Test  generator createSudoku    is called with correct parameters  null seed `() = runTest {
		// Verify that `createSudoku` on the `generator` is called with the correct `cellsToRemove` and a Long value when the input `seed` is null.
		val gridSize = SudokuGridSize.NINE
		val difficulty = GameDifficulty.Easy
		val expectedCellsToRemove = 40
		val sudokuPuzzle = mockk<SudokuPuzzle>()
		val sudokuGrid = mockk<SudokuGrid>()
		val solutionGrid = mockk<SolutionGrid>()
		every { sudokuPuzzle.component1() } returns sudokuGrid
		every { sudokuPuzzle.component2() } returns solutionGrid

		mockkStatic("com.example.domain.core.GameDifficultyKt")
		every { difficulty.toCellsToRemove(gridSize, null) } returns expectedCellsToRemove

		mockkConstructor(ClassicSudokuGenerator::class)
		val cellsToRemoveSlot = slot<Int>()
		val seedSlot = slot<Long>()
		coEvery {
			anyConstructed<ClassicSudokuGenerator>().createSudoku(
				capture(cellsToRemoveSlot),
				capture(seedSlot),
			)
		} returns sudokuPuzzle

		useCase(gridSize, difficulty, null)

		assertEquals(expectedCellsToRemove, cellsToRemoveSlot.captured)
		assertTrue(seedSlot.isCaptured)
	}

	@Test
	fun `Test returned Game object properties are initialized correctly`() = runTest {
		// Verify that the returned `Game` object has its `grid` set to the `sudokuGrid` from the generator, `difficulty` set to the input difficulty,
		// `elapsedTime` initialized to 0, `hintsUsed` initialized to 0, and `hintLogs` initialized to an empty persistent list.
		val gridSize = SudokuGridSize.NINE
		val difficulty = GameDifficulty.Hard
		val seed = 1L

		val game = useCase(gridSize, difficulty, seed)

		assertNotNull(game.grid)
		assertEquals(difficulty, game.difficulty)
		assertEquals(0, game.elapsedTime)
		assertEquals(0, game.hintsUsed)
		assertTrue(game.hintLogs.isEmpty())
	}
}
