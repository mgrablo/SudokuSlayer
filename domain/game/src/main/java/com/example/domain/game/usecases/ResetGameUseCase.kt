package com.example.domain.game.usecases

import com.example.domain.game.models.Game
import com.example.domain.game.repositories.OperationRepository
import com.example.sudoku.model.clearGrid
import com.example.sudoku.model.clearMatchingNumberHighlight
import com.example.sudoku.model.clearRowColumnHighlight
import com.example.sudoku.model.clearRuleBreakingCells
import kotlinx.collections.immutable.persistentListOf

class ResetGameUseCase(
	private val operationRepository: OperationRepository,
) {
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
