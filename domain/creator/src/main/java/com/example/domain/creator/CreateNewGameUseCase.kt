package com.example.domain.creator

import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import kotlin.random.Random

class CreateNewGameUseCase {
	operator fun invoke() {
	}

	private fun calculateCellsToRemove(
		gridSize: SudokuGridSize,
		difficulty: GameDifficulty,
	): Int =
		when (gridSize) {
			SudokuGridSize.FOUR ->
				when (difficulty) {
					GameDifficulty.Easy -> Random.nextInt(2, 4)
					GameDifficulty.Medium -> Random.nextInt(4, 6)
					GameDifficulty.Hard -> Random.nextInt(6, 8)
					GameDifficulty.Expert -> Random.nextInt(8, 10)
				}

			SudokuGridSize.NINE ->
				when (difficulty) {
					GameDifficulty.Easy -> Random.nextInt(30, 40)
					GameDifficulty.Medium -> Random.nextInt(41, 50)
					GameDifficulty.Hard -> Random.nextInt(51, 60)
					GameDifficulty.Expert -> Random.nextInt(61, 64)
				}

			SudokuGridSize.SIXTEEN ->
				when (difficulty) {
					GameDifficulty.Easy -> Random.nextInt(100, 120)
					GameDifficulty.Medium -> Random.nextInt(121, 140)
					GameDifficulty.Hard -> Random.nextInt(141, 160)
					GameDifficulty.Expert -> Random.nextInt(161, 180)
				}
		}
}
