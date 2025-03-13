package com.example.domain.game.models

enum class SudokuGridSize {
	FOUR,
	NINE,
	SIXTEEN,
	;

	override fun toString(): String =
		when (this) {
			FOUR -> "4x4"
			NINE -> "9x9"
			SIXTEEN -> "16x16"
		}

	companion object {
		fun fromInt(size: Int): SudokuGridSize =
			when (size) {
				0 -> FOUR
				1 -> NINE
				2 -> SIXTEEN
				else -> throw IllegalArgumentException("Invalid grid size: $size")
			}
	}
}
