package com.example.domain.game.usecases

import com.example.domain.game.usecases.game.FindMistakesUseCase
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SolutionGrid
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import com.example.sudokuslayer.domain.core.Game
import com.example.sudokuslayer.domain.core.GameDifficulty
import com.example.sudokuslayer.domain.core.HintLog
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FindMistakesUseCaseTest {
	@Test
	fun returnsEmptyListWhenNoMistakes() {
		val gridSize = 2
		val cells = persistentListOf(
			SudokuCellData(0, 0, 1, attributes = persistentSetOf(CellAttributes.GENERATED)),
			SudokuCellData(0, 1, 2, attributes = persistentSetOf(CellAttributes.GENERATED)),
			SudokuCellData(1, 0, 2, attributes = persistentSetOf(CellAttributes.GENERATED)),
			SudokuCellData(1, 1, 1, attributes = persistentSetOf(CellAttributes.GENERATED)),
		)
		val grid = SudokuGrid(gridSize, data = cells)
		val solution = SolutionGrid(intArrayOf(1, 2, 2, 1), gridSize)
		val game = Game(
			grid = grid,
			solution = solution,
			difficulty = GameDifficulty.Easy,
			elapsedTime = 0L,
			hintsUsed = 0,
			hintLogs = persistentListOf(),
			completed = false,
		)
		val result = FindMistakesUseCase().invoke(game)
		assertEquals(emptyList<Pair<Int, Int>>(), result)
	}

	@Test
	fun returnsMistakeForNonGeneratedCellWithWrongNumber() {
		val gridSize = 2
		val cells = persistentListOf(
			SudokuCellData(0, 0, 1, attributes = persistentSetOf(CellAttributes.GENERATED)),
			SudokuCellData(0, 1, 2),
			SudokuCellData(1, 0, 2, attributes = persistentSetOf(CellAttributes.GENERATED)),
			SudokuCellData(1, 1, 1),
		)
		val grid = SudokuGrid(gridSize, data = cells)
		val solution = SolutionGrid(intArrayOf(1, 1, 2, 2), gridSize)
		val game = Game(
			grid = grid,
			solution = solution,
			difficulty = GameDifficulty.Easy,
			elapsedTime = 0L,
			hintsUsed = 0,
			hintLogs = persistentListOf(),
			completed = false,
		)
		val result = FindMistakesUseCase().invoke(game)
		assertEquals(listOf(0 to 1, 1 to 1), result)
	}

	@Test
	fun ignoresEmptyCells() {
		val gridSize = 2
		val cells = persistentListOf(
			SudokuCellData(0, 0, 0),
			SudokuCellData(0, 1, 2),
			SudokuCellData(1, 0, 2),
			SudokuCellData(1, 1, 0),
		)
		val grid = SudokuGrid(gridSize, data = cells)
		val solution = SolutionGrid(intArrayOf(1, 2, 2, 1), gridSize)
		val game = Game(
			grid = grid,
			solution = solution,
			difficulty = GameDifficulty.Easy,
			elapsedTime = 0L,
			hintsUsed = 0,
			hintLogs = persistentListOf<HintLog>(),
			completed = false,
		)
		val result = FindMistakesUseCase().invoke(game)
		assertEquals(emptyList<Pair<Int, Int>>(), result)
	}

	@Test
	fun returnsMultipleMistakes() {
		val gridSize = 2
		val cells = persistentListOf(
			SudokuCellData(0, 0, 1),
			SudokuCellData(0, 1, 2),
			SudokuCellData(1, 0, 2),
			SudokuCellData(1, 1, 1),
		)
		val grid = SudokuGrid(gridSize, data = cells)
		val solution = SolutionGrid(intArrayOf(2, 1, 1, 2), gridSize)
		val game = Game(
			grid = grid,
			solution = solution,
			difficulty = GameDifficulty.Easy,
			elapsedTime = 0L,
			hintsUsed = 0,
			hintLogs = persistentListOf(),
			completed = false,
		)
		val result = FindMistakesUseCase().invoke(game)
		assertEquals(listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1), result)
	}
}
