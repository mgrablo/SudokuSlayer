package io.github.mgrablo.sudokuslayer.domain.creator

import io.github.mgrablo.sudokuslayer.domain.core.Game
import io.github.mgrablo.sudokuslayer.domain.core.GameRepository
import kotlinx.coroutines.flow.Flow

class GetSavedGameUseCase(private val gameRepository: GameRepository) {
	operator fun invoke(): Flow<Game> = gameRepository.getGame()
}
