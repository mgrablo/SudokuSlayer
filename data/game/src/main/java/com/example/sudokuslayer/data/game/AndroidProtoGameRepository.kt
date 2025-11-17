package com.example.sudokuslayer.data.game

import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import com.example.sudokuslayer.data.core.proto.ProtoStorage
import com.example.sudokuslayer.data.game.mappers.toGame
import com.example.sudokuslayer.data.game.mappers.toProtoCell
import com.example.sudokuslayer.data.game.mappers.toProtoDifficulty
import com.example.sudokuslayer.data.game.mappers.toProtoGame
import com.example.sudokuslayer.data.game.mappers.toProtoGrid
import com.example.sudokuslayer.data.game.mappers.toProtoHintLog
import com.example.sudokuslayer.domain.core.Game
import com.example.sudokuslayer.domain.core.GameDifficulty
import com.example.sudokuslayer.domain.core.GameRepository
import com.example.sudokuslayer.domain.core.HintLog
import data.game.ProtoGame
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

	override suspend fun updateGrid(sudokuGrid: SudokuGrid) {
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

	override suspend fun updateCell(row: Int, column: Int, cellData: SudokuCellData) {
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
