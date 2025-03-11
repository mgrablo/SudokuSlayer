package com.example.data.game.mappers

import com.example.data.game.models.Game
import com.example.data.game.models.GameDifficulty
import data.game.ProtoGame

class ProtoGameMapper(
	private val protoGridMapper: ProtoGridMapper = ProtoGridMapper(),
) : Mapper<ProtoGame, Game> {
	override fun invoke(input: ProtoGame): Game =
		Game(
			grid = protoGridMapper(input.grid),
			difficulty = mapToDomainDifficulty(input.difficulty),
			elapsedTime = input.elapsedTime,
			hintsUsed = input.hintsUsed,
		)

	fun toProtoGame(input: Game): ProtoGame =
		ProtoGame
			.newBuilder()
			.setGrid(protoGridMapper.toProtoGrid(input.grid))
			.setDifficulty(mapToProtoDifficulty(input.difficulty))
			.setElapsedTime(input.elapsedTime)
			.setHintsUsed(input.hintsUsed)
			.build()

	fun mapToDomainDifficulty(input: ProtoGame.Difficulty): GameDifficulty =
		when (input) {
			ProtoGame.Difficulty.EASY -> GameDifficulty.Easy
			ProtoGame.Difficulty.MEDIUM -> GameDifficulty.Medium
			ProtoGame.Difficulty.HARD -> GameDifficulty.Hard
			ProtoGame.Difficulty.EXPERT -> GameDifficulty.Expert
			ProtoGame.Difficulty.UNSPECIFIED -> GameDifficulty.Easy
			ProtoGame.Difficulty.UNRECOGNIZED -> GameDifficulty.Easy
		}

	fun mapToProtoDifficulty(input: GameDifficulty): ProtoGame.Difficulty =
		when (input) {
			GameDifficulty.Easy -> ProtoGame.Difficulty.EASY
			GameDifficulty.Medium -> ProtoGame.Difficulty.MEDIUM
			GameDifficulty.Hard -> ProtoGame.Difficulty.HARD
			GameDifficulty.Expert -> ProtoGame.Difficulty.EXPERT
		}
}
