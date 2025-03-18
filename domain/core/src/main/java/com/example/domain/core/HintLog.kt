package com.example.domain.core

import com.example.sudoku.solver.Hint
import kotlinx.collections.immutable.PersistentList

data class HintLog(
	val id: Int,
	val hint: Hint,
	val isUserGuessed: Boolean,
	val isRevealed: Boolean,
	val explanation: PersistentList<String>,
)
