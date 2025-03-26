package com.example.domain.game.repositories

import com.example.sudoku.model.SudokuCellData
import kotlinx.coroutines.flow.Flow

interface OperationRepository {
	fun getRedoOperationsFlow(): Flow<List<Operation>>

	suspend fun getRedoOperations(): List<Operation>

	fun getUndoOperationsFlow(): Flow<List<Operation>>

	suspend fun getUndoOperations(): List<Operation>

	suspend fun addRedoOperation(operation: Operation)

	suspend fun addUndoOperation(operation: Operation)

	suspend fun removeRedoOperation(id: Long)

	suspend fun removeUndoOperation(id: Long)

	suspend fun clearOperations()

	suspend fun clearRedoOperations()

	suspend fun clearUndoOperations()

	suspend fun findRedoOperation(id: Long): Operation?

	suspend fun findUndoOperation(id: Long): Operation?
}

data class Operation(val id: Long, val cell: SudokuCellData, val oldCell: SudokuCellData)
