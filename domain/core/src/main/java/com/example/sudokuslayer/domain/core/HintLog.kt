package com.example.sudokuslayer.domain.core

import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintExplanationStep
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * Represents a hint provided to the user, including its explanation.
 *
 * @property id Unique identifier for the hint
 * @property hint The actual Sudoku hint object
 * @property isUserGuessed Whether the user guessed the hint without revealing it
 * @property isRevealed Whether the hint has been revealed to the user
 * @property structuredExplanation New structured hint explanation steps that support better styling and localization
 */
data class HintLog(
	val id: Int,
	val hint: Hint,
	val isUserGuessed: Boolean,
	val isRevealed: Boolean,
	val structuredExplanation: PersistentList<HintExplanationStep> = persistentListOf(),
)
