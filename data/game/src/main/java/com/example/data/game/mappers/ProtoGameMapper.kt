package com.example.data.game.mappers

import com.example.domain.core.Game
import com.example.domain.core.GameDifficulty
import data.game.ProtoGame
import kotlinx.collections.immutable.toPersistentList

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
		hintLogs = hintLogsList.map { it.toHintLog() }.toPersistentList(),
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
		ProtoGame.Difficulty.UNSPECIFIED -> GameDifficulty.Easy
		ProtoGame.Difficulty.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized difficulty")
	}
