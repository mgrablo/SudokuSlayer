package com.example.sudokuslayer.domain.creator

import com.example.sudokuslayer.domain.core.GameRepository
import kotlinx.coroutines.flow.Flow

class HasActiveGameUseCase(private val gameRepository: GameRepository) {
	operator fun invoke(): Flow<Boolean> = gameRepository.hasActiveGame()
}
