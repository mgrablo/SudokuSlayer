package com.example.sudokuslayer.domain.game.usecases.game

import com.example.sudoku.model.clearGrid
import com.example.sudoku.model.clearMatchingNumberHighlight
import com.example.sudoku.model.clearRowColumnHighlight
import com.example.sudoku.model.clearRuleBreakingCells
import com.example.sudokuslayer.domain.core.Game
import com.example.sudokuslayer.domain.core.OperationRepository
import kotlinx.collections.immutable.persistentListOf

class ResetGameUseCase(private val operationRepository: OperationRepository) {
	suspend operator fun invoke(game: Game): Game {
		operationRepository.clearOperations()
		val updatedGrid =
			game.grid
				.clearGrid()
				.clearMatchingNumberHighlight()
				.clearRowColumnHighlight()
				.clearRuleBreakingCells()

		return game.copy(
			grid = updatedGrid,
			difficulty = game.difficulty,
			elapsedTime = 0,
			hintsUsed = 0,
			hintLogs = persistentListOf(),
		)
	}
}
