package io.github.mgrablo.sudokuslayer.domain.creator

import io.github.mgrablo.sudokucore.generator.ClassicSudokuGenerator
import io.github.mgrablo.sudokuslayer.domain.core.Game
import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import io.github.mgrablo.sudokuslayer.domain.core.OperationRepository
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.domain.core.toCellsToRemove
import kotlinx.collections.immutable.persistentListOf
import kotlin.random.Random

class CreateNewGameUseCase(private val operationRepository: OperationRepository) {
	suspend operator fun invoke(
		gridSize: SudokuGridSize,
		difficulty: GameDifficulty,
		seed: Long? = null,
	): Game {
		operationRepository.clearOperations()
		val generator = ClassicSudokuGenerator(gridSize.toIntSize())
		val cellsToRemove = difficulty.toCellsToRemove(gridSize, seed)
		val (sudokuGrid, solution) = generator.createSudoku(cellsToRemove, seed ?: Random.nextLong())

		return Game(
			grid = sudokuGrid,
			solution = solution,
			difficulty = difficulty,
			elapsedTime = 0,
			hintsUsed = 0,
			hintLogs = persistentListOf(),
		)
	}
}
