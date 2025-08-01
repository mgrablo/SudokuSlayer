package com.example.domain.core

import kotlin.random.Random

enum class GameDifficulty {
	Easy,
	Medium,
	Hard,
	Expert,
}

fun GameDifficulty.toCellsToRemove(gridSize: SudokuGridSize, seed: Long? = null): Int {
	val random = seed?.let { Random(it) } ?: Random.Default
	return when (this) {
		GameDifficulty.Easy -> when (gridSize) {
			SudokuGridSize.FOUR -> random.nextInt(2, 4)
			SudokuGridSize.NINE -> random.nextInt(30, 40)
			SudokuGridSize.SIXTEEN -> random.nextInt(100, 120)
		}

		GameDifficulty.Medium -> when (gridSize) {
			SudokuGridSize.FOUR -> random.nextInt(4, 6)
			SudokuGridSize.NINE -> random.nextInt(41, 50)
			SudokuGridSize.SIXTEEN -> random.nextInt(121, 140)
		}

		GameDifficulty.Hard -> when (gridSize) {
			SudokuGridSize.FOUR -> random.nextInt(6, 8)
			SudokuGridSize.NINE -> random.nextInt(51, 60)
			SudokuGridSize.SIXTEEN -> random.nextInt(141, 160)
		}

		GameDifficulty.Expert -> when (gridSize) {
			SudokuGridSize.FOUR -> random.nextInt(8, 10)
			SudokuGridSize.NINE -> random.nextInt(61, 64)
			SudokuGridSize.SIXTEEN -> random.nextInt(161, 180)
		}
	}
}
