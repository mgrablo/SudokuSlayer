package com.example.domain.creator

import com.example.domain.core.Game
import com.example.domain.core.GameRepository
import kotlinx.coroutines.flow.firstOrNull

class GetSavedGameUseCase(private val gameRepository: GameRepository) {
	suspend operator fun invoke(): Game? {
		val hasSavedGame = gameRepository.hasActiveGame().firstOrNull() ?: false
		if (!hasSavedGame) {
			return null
		}
		val savedGame = gameRepository.getGame().firstOrNull()
		if (savedGame?.grid?.gridSize == 0) {
			return null
		}
		return savedGame
	}
}
