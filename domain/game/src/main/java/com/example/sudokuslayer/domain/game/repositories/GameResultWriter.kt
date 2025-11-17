package com.example.sudokuslayer.domain.game.repositories

import com.example.sudokuslayer.domain.core.GameResult

interface GameResultWriter {
	suspend fun saveGameResult(gameResult: GameResult)
}
