package com.example.domain.creator

import com.example.domain.core.Game
import com.example.domain.core.GameRepository

class SaveGameUseCase(
	private val gameRepository: GameRepository,
) {
	suspend operator fun invoke(game: Game) {
		gameRepository.saveGame(game)
	}
}
