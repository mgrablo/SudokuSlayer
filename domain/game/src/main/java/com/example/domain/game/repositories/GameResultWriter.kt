package com.example.domain.game.repositories

import com.example.domain.core.GameResult

interface GameResultWriter {
	suspend fun saveGameResult(gameResult: GameResult)
}
