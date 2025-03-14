package com.example.domain.game.repositories

import kotlinx.coroutines.flow.Flow

interface OperationRepository {
	fun getRedoOperations(): Flow<List<Operation>>

	fun getUndoOperations(): Flow<List<Operation>>

	suspend fun addRedoOperation(operation: Operation)

	suspend fun addUndoOperation(operation: Operation)

	suspend fun removeRedoOperation(operation: Operation)

	suspend fun removeUndoOperation(operation: Operation)

	suspend fun clearOperations()

	suspend fun clearRedoOperations()

	suspend fun clearUndoOperations()
}

data class Operation(
	val id: Long,
	val cellRow: Int,
	val cellColumn: Int,
	val value: Int,
)
