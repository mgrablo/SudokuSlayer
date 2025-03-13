package com.example.data.game.mappers

import com.example.data.game.models.Game
import com.example.data.game.models.GameDifficulty
import data.game.ProtoGame

fun Game.toProtoGame(): ProtoGame =
	ProtoGame
		.newBuilder()
		.setGrid(grid.toProtoGrid())
		.setDifficulty(difficulty.toProtoDifficulty())
		.setHintsUsed(hintsUsed)
		.setElapsedTime(elapsedTime)
		.addAllHintLogs(hintLogs.map { it.toProtoHintLog() })
		.build()

fun ProtoGame.toGame(): Game =
	Game(
		grid = grid.toSudokuGrid(),
		difficulty = difficulty.toGameDifficulty(),
		elapsedTime = elapsedTime,
		hintsUsed = hintsUsed,
		hintLogs = hintLogsList.map { it.toHintLog() },
	)

fun GameDifficulty.toProtoDifficulty(): ProtoGame.Difficulty =
	when (this) {
		GameDifficulty.Easy -> ProtoGame.Difficulty.EASY
		GameDifficulty.Medium -> ProtoGame.Difficulty.MEDIUM
		GameDifficulty.Hard -> ProtoGame.Difficulty.HARD
		GameDifficulty.Expert -> ProtoGame.Difficulty.EXPERT
	}

fun ProtoGame.Difficulty.toGameDifficulty(): GameDifficulty =
	when (this) {
		ProtoGame.Difficulty.EASY -> GameDifficulty.Easy
		ProtoGame.Difficulty.MEDIUM -> GameDifficulty.Medium
		ProtoGame.Difficulty.HARD -> GameDifficulty.Hard
		ProtoGame.Difficulty.EXPERT -> GameDifficulty.Expert
		ProtoGame.Difficulty.UNSPECIFIED -> throw IllegalArgumentException("Unspecified difficulty")
		ProtoGame.Difficulty.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized difficulty")
	}
