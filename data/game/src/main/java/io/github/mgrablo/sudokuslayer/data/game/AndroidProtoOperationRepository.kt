package io.github.mgrablo.sudokuslayer.data.game

import io.github.mgrablo.sudokuslayer.data.core.proto.ProtoStorageFactory
import io.github.mgrablo.sudokuslayer.data.game.mappers.toOperation
import io.github.mgrablo.sudokuslayer.data.game.mappers.toProtoOperation
import io.github.mgrablo.sudokuslayer.domain.core.Operation
import io.github.mgrablo.sudokuslayer.domain.core.OperationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class AndroidProtoOperationRepository(
	private val protoStorageFactory: ProtoStorageFactory,
	private val serializer: ProtoOperationHistorySerializer,
) : OperationRepository {
	private val protoStorage =
		protoStorageFactory.createProtoStorage(
			filename = "operations.pb",
			serializer = serializer,
		)

	override fun getRedoOperationsFlow(): Flow<List<Operation>> =
		protoStorage.getData().map { operationHistory ->
			operationHistory.redoOperationsList.map { it.toOperation() }
		}

	override fun getUndoOperationsFlow(): Flow<List<Operation>> =
		protoStorage.getData().map { operationHistory ->
			operationHistory.undoOperationsList.map { it.toOperation() }
		}

	override suspend fun getRedoOperations(): List<Operation> =
		getRedoOperationsFlow().firstOrNull() ?: emptyList()

	override suspend fun getUndoOperations(): List<Operation> =
		getUndoOperationsFlow().firstOrNull() ?: emptyList()

	override suspend fun addRedoOperation(operation: Operation) {
		require(operation.changes.all { it.oldCell.row == it.newCell.row })
		require(operation.changes.all { it.oldCell.col == it.newCell.col })

		protoStorage.updateData {
			it
				.toBuilder()
				.addRedoOperations(operation.toProtoOperation())
				.build()
		}
	}

	override suspend fun addUndoOperation(operation: Operation) {
		require(operation.changes.all { it.oldCell.row == it.newCell.row })
		require(operation.changes.all { it.oldCell.col == it.newCell.col })

		protoStorage.updateData {
			it
				.toBuilder()
				.addUndoOperations(operation.toProtoOperation())
				.build()
		}
	}

	override suspend fun removeRedoOperation(id: Long) {
		protoStorage.updateData { operationHistory ->
			operationHistory
				.toBuilder()
				.removeRedoOperations(
					operationHistory.redoOperationsList.indexOfFirst { it.id == id },
				).build()
		}
	}

	override suspend fun removeUndoOperation(id: Long) {
		protoStorage.updateData { operationHistory ->
			operationHistory
				.toBuilder()
				.removeUndoOperations(
					operationHistory.undoOperationsList.indexOfFirst { it.id == id },
				).build()
		}
	}

	override suspend fun clearOperations() {
		protoStorage.updateData {
			it
				.toBuilder()
				.clearRedoOperations()
				.clearUndoOperations()
				.build()
		}
	}

	override suspend fun clearRedoOperations() {
		protoStorage.updateData {
			it
				.toBuilder()
				.clearRedoOperations()
				.build()
		}
	}

	override suspend fun clearUndoOperations() {
		protoStorage.updateData {
			it
				.toBuilder()
				.clearUndoOperations()
				.build()
		}
	}

	override suspend fun findRedoOperation(id: Long): Operation? = getRedoOperations().firstOrNull {
		it.id == id
	}

	override suspend fun findUndoOperation(id: Long): Operation? = getUndoOperations().firstOrNull {
		it.id == id
	}
}
