package com.example.data.game

import com.example.data.core.proto.ProtoStorageFactory
import com.example.data.game.mappers.ProtoCellMapper
import com.example.data.game.mappers.ProtoGameMapper
import com.example.data.game.mappers.ProtoGridMapper
import com.example.data.game.models.Game
import com.example.data.game.models.GameDifficulty
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProtoGameRepository(
	protoStorageFactory: ProtoStorageFactory,
	serializer: ProtoGameSerializer,
	private val gameMapper: ProtoGameMapper = ProtoGameMapper(),
	private val gridMapper: ProtoGridMapper = ProtoGridMapper(),
	private val cellMapper: ProtoCellMapper = ProtoCellMapper(),
) {
	private val protoStorage =
		protoStorageFactory.createProtoStorage(
			filename = "game.pb",
			serializer = serializer,
		)

	fun getGame(): Flow<Game> = protoStorage.getData().map { gameMapper(it) }

	suspend fun saveGame(game: Game) {
		protoStorage.updateData {
			gameMapper.toProtoGame(game)
		}
	}

	suspend fun updateGrid(grid: SudokuGrid) {
		protoStorage.updateData { protoGame ->
			protoGame
				.toBuilder()
				.setGrid(
					gridMapper.toProtoGrid(grid)
				).build()
		}
	}

	suspend fun updateElapsedTime(elapsedTime: Long) {
		protoStorage.updateData {
			it
				.toBuilder()
				.setElapsedTime(elapsedTime)
				.build()
		}
	}

	suspend fun updateHintsUsed(hintsUsed: Int) {
		protoStorage.updateData {
			it
				.toBuilder()
				.setHintsUsed(hintsUsed)
				.build()
		}
	}

	suspend fun updateCell(
		row: Int,
		column: Int,
		cellData: SudokuCellData,
	) {
		protoStorage.updateData { protoGame ->
			protoGame
				.toBuilder()
				.setGrid(
					protoGame.grid
						.toBuilder()
						.setCell(
							row * protoGame.grid.gridSize + column,
							cellMapper.toProtoCell(cellData),
						).build(),
				).build()
		}
	}

	suspend fun updateDifficulty(difficulty: GameDifficulty) {
		protoStorage.updateData {
			it
				.toBuilder()
				.setDifficulty(
					gameMapper.mapToProtoDifficulty(difficulty),
				).build()
		}
	}
}
