package io.github.mgrablo.sudokuslayer.domain.game.usecases.game

import io.github.mgrablo.sudokuslayer.domain.core.Game
import io.github.mgrablo.sudokuslayer.domain.core.GameRepository

class SaveGameUseCase(private val gameRepository: GameRepository) {
	suspend operator fun invoke(game: Game) {
		gameRepository.saveGame(game)
	}
}
