package io.github.mgrablo.sudokuslayer.domain.game.usecases.game

import io.github.mgrablo.sudokuslayer.domain.core.Game
import io.github.mgrablo.sudokuslayer.domain.core.GameRepository
import kotlinx.coroutines.flow.Flow

class GetGameUseCase(private val gameRepository: GameRepository) {
	operator fun invoke(): Flow<Game> = gameRepository.getGame()
}
