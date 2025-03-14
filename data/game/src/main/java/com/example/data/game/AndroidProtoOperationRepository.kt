package com.example.data.game

import com.example.data.core.proto.ProtoStorageFactory
import com.example.data.game.mappers.toOperation
import com.example.data.game.mappers.toProtoOperation
import com.example.domain.game.repositories.Operation
import com.example.domain.game.repositories.OperationRepository
import kotlinx.coroutines.flow.Flow
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

	override fun getRedoOperations(): Flow<List<Operation>> = protoStorage.getData().map { it.redoOperationsList.map { it.toOperation() } }

	override fun getUndoOperations(): Flow<List<Operation>> = protoStorage.getData().map { it.undoOperationsList.map { it.toOperation() } }

	override suspend fun addRedoOperation(operation: Operation) {
		protoStorage.updateData {
			it
				.toBuilder()
				.addRedoOperations(operation.toProtoOperation())
				.build()
		}
	}

	override suspend fun addUndoOperation(operation: Operation) {
		protoStorage.updateData {
			it
				.toBuilder()
				.addUndoOperations(operation.toProtoOperation())
				.build()
		}
	}

	override suspend fun removeRedoOperation(operation: Operation) {
		protoStorage.updateData {
			it
				.toBuilder()
				.removeRedoOperations(
					it.redoOperationsList.indexOfFirst { it.id == operation.id },
				).build()
		}
	}

	override suspend fun removeUndoOperation(operation: Operation) {
		protoStorage.updateData {
			it
				.toBuilder()
				.removeUndoOperations(
					it.undoOperationsList.indexOfFirst { it.id == operation.id },
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
}
