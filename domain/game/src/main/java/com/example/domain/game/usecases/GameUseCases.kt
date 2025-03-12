package com.example.domain.game.usecases

import com.example.data.game.ProtoGameRepository
import com.example.data.game.models.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetGameUseCase(
	private val gameRepository: ProtoGameRepository,
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
	private val gameRepository: ProtoGameRepository,
) {
	suspend operator fun invoke(game: Game) {
		gameRepository.saveGame(game)
	}
}
