package io.github.mgrablo.sudokuslayer.domain.core

import io.github.mgrablo.sudokucore.hints.Hint

/**
 * Represents a hint provided to the user, including its explanation.
 *
 * @property id Unique identifier for the hint
 * @property hint The actual Sudoku hint object
 * @property isUserGuessed Whether the user guessed the hint without revealing it
 * @property isRevealed Whether the hint has been revealed to the user
 */
data class HintLog(
	val id: Int,
	val hint: Hint,
	val isUserGuessed: Boolean,
	val isRevealed: Boolean,
)
