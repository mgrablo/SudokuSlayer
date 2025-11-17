package com.example.sudokuslayer.domain.core

import com.example.sudoku.model.SolutionGrid
import com.example.sudoku.model.SudokuGrid
import kotlinx.collections.immutable.PersistentList

data class Game(
	val grid: SudokuGrid,
	val solution: SolutionGrid,
	val difficulty: GameDifficulty,
	val elapsedTime: Long,
	val hintsUsed: Int,
	val hintLogs: PersistentList<HintLog>,
	val completed: Boolean = false,
)
