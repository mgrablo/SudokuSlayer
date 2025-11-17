package io.github.mgrablo.sudokuslayer.domain.game.repositories

import io.github.mgrablo.sudokuslayer.domain.core.GameResult

interface GameResultWriter {
	suspend fun saveGameResult(gameResult: GameResult)
}
