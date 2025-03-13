package com.example.data.game.models

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.solver.Hint
import kotlinx.collections.immutable.PersistentList

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

data class HintLog(
	val hint: Hint,
	val isUserGuessed: Boolean,
	val isRevealed: Boolean,
	val explanation: PersistentList<String>,
)
