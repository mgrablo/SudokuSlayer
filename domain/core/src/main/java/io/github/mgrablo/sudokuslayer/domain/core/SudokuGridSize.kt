package io.github.mgrablo.sudokuslayer.domain.core

enum class SudokuGridSize {
	FOUR,
	NINE,
	SIXTEEN,
	;

	override fun toString(): String = when (this) {
		FOUR -> "4x4"
		NINE -> "9x9"
		SIXTEEN -> "16x16"
	}

	fun toIntSize() = when (this) {
		FOUR -> 4
		NINE -> 9
		SIXTEEN -> 16
	}

	companion object {
		fun fromIndex(index: Int): SudokuGridSize = when (index) {
			0 -> FOUR
			1 -> NINE
			2 -> SIXTEEN
			else -> throw IllegalArgumentException("Invalid index number: $index")
		}

		fun fromIntSize(size: Int) = when (size) {
			4 -> FOUR
			9 -> NINE
			16 -> SIXTEEN
			else -> throw IllegalArgumentException("Invalid grid size: $size")
		}
	}
}
