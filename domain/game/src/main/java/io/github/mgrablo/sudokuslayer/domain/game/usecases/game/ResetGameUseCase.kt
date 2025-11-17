package io.github.mgrablo.sudokuslayer.domain.game.usecases.game

import io.github.mgrablo.sudokucore.model.clearGrid
import io.github.mgrablo.sudokucore.model.clearMatchingNumberHighlight
import io.github.mgrablo.sudokucore.model.clearRowColumnHighlight
import io.github.mgrablo.sudokucore.model.clearRuleBreakingCells
import io.github.mgrablo.sudokuslayer.domain.core.Game
import io.github.mgrablo.sudokuslayer.domain.core.OperationRepository
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
