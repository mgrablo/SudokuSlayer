package com.example.domain.game.models

import com.example.sudoku.model.SudokuGrid

data class Game(
	val grid: SudokuGrid,
	val difficulty: GameDifficulty,
	val elapsedTime: Long,
	val hintsUsed: Int,
	val hintLogs: List<HintLog>,
)

enum class GameDifficulty {
	Easy,
	Medium,
	Hard,
	Expert,
}
