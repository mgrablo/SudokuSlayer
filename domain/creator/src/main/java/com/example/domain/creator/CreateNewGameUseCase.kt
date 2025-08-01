package com.example.domain.creator

import com.example.domain.core.Game
import com.example.domain.core.GameDifficulty
import com.example.domain.core.OperationRepository
import com.example.domain.core.SudokuGridSize
import com.example.domain.core.toCellsToRemove
import com.example.sudoku.generator.ClassicSudokuGenerator
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
		val sudokuGrid = generator.createSudoku(cellsToRemove, seed ?: Random.nextLong())

		return Game(
			grid = sudokuGrid,
			difficulty = difficulty,
			elapsedTime = 0,
			hintsUsed = 0,
			hintLogs = persistentListOf(),
		)
	}
}
