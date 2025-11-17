package io.github.mgrablo.sudokuslayer.data.game

import data.game.ProtoGame
import io.github.mgrablo.sudokuslayer.data.core.proto.ProtoStorage
import io.github.mgrablo.sudokuslayer.data.game.mappers.toGame
import io.github.mgrablo.sudokuslayer.data.game.mappers.toProtoCell
import io.github.mgrablo.sudokuslayer.data.game.mappers.toProtoDifficulty
import io.github.mgrablo.sudokuslayer.data.game.mappers.toProtoGame
import io.github.mgrablo.sudokuslayer.data.game.mappers.toProtoGrid
import io.github.mgrablo.sudokuslayer.data.game.mappers.toProtoHintLog
import io.github.mgrablo.sudokuslayer.domain.core.Game
import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import io.github.mgrablo.sudokuslayer.domain.core.GameRepository
import io.github.mgrablo.sudokuslayer.domain.core.HintLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AndroidProtoGameRepository(private val protoStorage: ProtoStorage<ProtoGame>) :
	GameRepository {
	override fun getGame(): Flow<Game> = protoStorage.getData().map { it.toGame() }

	override fun hasActiveGame(): Flow<Boolean> = protoStorage.getData().map {
		it.grid.cellCount != 0
	}

	override suspend fun saveGame(game: Game) {
		protoStorage.updateData {
			game.toProtoGame()
		}
	}

	override suspend fun updateGrid(sudokuGrid: io.github.mgrablo.sudokucore.model.SudokuGrid) {
		protoStorage.updateData { protoGame ->
			protoGame
				.toBuilder()
				.setGrid(
					sudokuGrid.toProtoGrid(),
				).build()
		}
	}

	override suspend fun updateElapsedTime(elapsedTime: Long) {
		protoStorage.updateData {
			it
				.toBuilder()
				.setElapsedTime(elapsedTime)
				.build()
		}
	}

	override suspend fun updateHintsUsed(hintsUsed: Int) {
		protoStorage.updateData {
			it
				.toBuilder()
				.setHintsUsed(hintsUsed)
				.build()
		}
	}

	override suspend fun updateCell(
		row: Int,
		column: Int,
		cellData: io.github.mgrablo.sudokucore.model.SudokuCellData,
	) {
		protoStorage.updateData { protoGame ->
			protoGame
				.toBuilder()
				.setGrid(
					protoGame.grid
						.toBuilder()
						.setCell(
							row * protoGame.grid.gridSize + column,
							cellData.toProtoCell(),
						).build(),
				).build()
		}
	}

	override suspend fun addHintLog(hintLog: HintLog) {
		protoStorage.updateData { protoGame ->
			protoGame
				.toBuilder()
				.addHintLogs(
					hintLog.toProtoHintLog(),
				).build()
		}
	}

	override suspend fun updateHintLog(hintLog: HintLog) {
		protoStorage.updateData { protoGame ->
			protoGame
				.toBuilder()
				.setHintLogs(hintLog.id, hintLog.toProtoHintLog())
				.build()
		}
	}

	override suspend fun updateGameDifficulty(gameDifficulty: GameDifficulty) {
		protoStorage.updateData {
			it
				.toBuilder()
				.setDifficulty(
					gameDifficulty.toProtoDifficulty(),
				).build()
		}
	}

	override suspend fun clearActiveGame() {
		protoStorage.updateData {
			it.toBuilder().clear().build()
		}
	}
}
