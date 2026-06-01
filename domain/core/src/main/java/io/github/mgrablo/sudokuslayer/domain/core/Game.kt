package io.github.mgrablo.sudokuslayer.domain.core

import androidx.compose.runtime.Stable
import io.github.mgrablo.sudokucore.model.SolutionGrid
import io.github.mgrablo.sudokucore.model.SudokuGrid
import kotlinx.collections.immutable.PersistentList

@Stable
data class Game(
	val grid: SudokuGrid,
	val solution: SolutionGrid,
	val difficulty: GameDifficulty,
	val elapsedTime: Long,
	val hintsUsed: Int,
	val hintLogs: PersistentList<HintLog>,
	val completed: Boolean = false,
)
