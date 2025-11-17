package io.github.mgrablo.sudokuslayer.data.game.mappers

import data.game.ProtoGame
import io.github.mgrablo.sudokuslayer.domain.core.Game
import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import kotlinx.collections.immutable.toPersistentList

fun Game.toProtoGame(): ProtoGame = ProtoGame
	.newBuilder()
	.setGrid(grid.toProtoGrid())
	.setSolution(solution.toProtoSolutionGrid())
	.setDifficulty(difficulty.toProtoDifficulty())
	.setHintsUsed(hintsUsed)
	.setElapsedTime(elapsedTime)
	.setCompleted(completed)
	.addAllHintLogs(hintLogs.map { it.toProtoHintLog() })
	.build()

fun ProtoGame.toGame(): Game = Game(
	grid = grid.toSudokuGrid(),
	difficulty = difficulty.toGameDifficulty(),
	elapsedTime = elapsedTime,
	hintsUsed = hintsUsed,
	hintLogs = hintLogsList.map { it.toHintLog() }.toPersistentList(),
	completed = completed,
	solution = solution.toSolutionGrid(),
)

fun GameDifficulty.toProtoDifficulty(): ProtoGame.Difficulty = when (this) {
	GameDifficulty.Easy -> ProtoGame.Difficulty.EASY
	GameDifficulty.Medium -> ProtoGame.Difficulty.MEDIUM
	GameDifficulty.Hard -> ProtoGame.Difficulty.HARD
	GameDifficulty.Expert -> ProtoGame.Difficulty.EXPERT
}

fun ProtoGame.Difficulty.toGameDifficulty(): GameDifficulty = when (this) {
	ProtoGame.Difficulty.EASY -> GameDifficulty.Easy
	ProtoGame.Difficulty.MEDIUM -> GameDifficulty.Medium
	ProtoGame.Difficulty.HARD -> GameDifficulty.Hard
	ProtoGame.Difficulty.EXPERT -> GameDifficulty.Expert
	ProtoGame.Difficulty.UNSPECIFIED -> GameDifficulty.Easy
	ProtoGame.Difficulty.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized difficulty")
}
