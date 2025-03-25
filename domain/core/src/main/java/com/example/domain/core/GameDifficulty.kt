package com.example.domain.core

enum class GameDifficulty {
	Easy,
	Medium,
	Hard,
	Expert,
	;

	companion object {
		fun fromInt(difficulty: Int): GameDifficulty = when (difficulty) {
			0 -> Easy
			1 -> Medium
			2 -> Hard
			3 -> Expert
			else -> throw IllegalArgumentException("Invalid difficulty level")
		}
	}
}
