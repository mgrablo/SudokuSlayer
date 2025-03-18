package com.example.domain.core

import com.example.sudoku.model.SudokuGrid
import kotlinx.collections.immutable.PersistentList

data class Game(
	val grid: SudokuGrid,
	val difficulty: GameDifficulty,
	val elapsedTime: Long,
	val hintsUsed: Int,
	val hintLogs: PersistentList<HintLog>,
)
