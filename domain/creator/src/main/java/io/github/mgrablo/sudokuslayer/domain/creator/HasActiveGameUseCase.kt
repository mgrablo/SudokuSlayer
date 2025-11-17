package io.github.mgrablo.sudokuslayer.domain.creator

import io.github.mgrablo.sudokuslayer.domain.core.GameRepository
import kotlinx.coroutines.flow.Flow

class HasActiveGameUseCase(private val gameRepository: GameRepository) {
	operator fun invoke(): Flow<Boolean> = gameRepository.hasActiveGame()
}
