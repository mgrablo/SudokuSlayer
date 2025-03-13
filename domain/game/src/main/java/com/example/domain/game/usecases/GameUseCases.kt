package com.example.domain.game.usecases

import com.example.domain.game.models.Game
import com.example.domain.game.repositories.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetGameUseCase(
	private val gameRepository: GameRepository,
) {
	operator fun invoke(): Flow<Game> = gameRepository.getGame()
}

class GetElapsedTimeUseCase(
	private val getGameUseCase: GetGameUseCase,
) {
	operator fun invoke(): Flow<Long> =
		getGameUseCase().map { game ->
			game.elapsedTime
		}
}

class SaveGameUseCase(
	private val gameRepository: GameRepository,
) {
	suspend operator fun invoke(game: Game) {
		gameRepository.saveGame(game)
	}
}
