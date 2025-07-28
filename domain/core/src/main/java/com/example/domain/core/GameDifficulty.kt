package com.example.domain.core

import kotlin.random.Random

enum class GameDifficulty {
	Easy,
	Medium,
	Hard,
	Expert,
}

fun GameDifficulty.toCellsToRemove(gridSize: SudokuGridSize): Int = when (this) {
	GameDifficulty.Easy -> when (gridSize) {
		SudokuGridSize.FOUR -> Random.nextInt(2, 4)
		SudokuGridSize.NINE -> Random.nextInt(30, 40)
		SudokuGridSize.SIXTEEN -> Random.nextInt(100, 120)
	}
	GameDifficulty.Medium -> when (gridSize) {
		SudokuGridSize.FOUR -> Random.nextInt(4, 6)
		SudokuGridSize.NINE -> Random.nextInt(41, 50)
		SudokuGridSize.SIXTEEN -> Random.nextInt(121, 140)
	}
	GameDifficulty.Hard -> when (gridSize) {
		SudokuGridSize.FOUR -> Random.nextInt(6, 8)
		SudokuGridSize.NINE -> Random.nextInt(51, 60)
		SudokuGridSize.SIXTEEN -> Random.nextInt(141, 160)
	}
	GameDifficulty.Expert -> when (gridSize) {
		SudokuGridSize.FOUR -> Random.nextInt(8, 10)
		SudokuGridSize.NINE -> Random.nextInt(61, 64)
		SudokuGridSize.SIXTEEN -> Random.nextInt(161, 180)
	}
}
