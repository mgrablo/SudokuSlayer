package com.example.domain.creator

import com.example.domain.core.GameRepository
import kotlinx.coroutines.flow.Flow

class HasActiveGameUseCase(private val gameRepository: GameRepository) {
	operator fun invoke(): Flow<Boolean> = gameRepository.hasActiveGame()
}
