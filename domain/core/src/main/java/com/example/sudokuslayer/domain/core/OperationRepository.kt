package com.example.sudokuslayer.domain.core

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

data class Operation(val id: Long, val changes: List<CellChange>) {
	constructor(id: Long, vararg changes: CellChange) : this(id, changes.toList())
}

/**
 * Represents a change to a Sudoku cell.
 * The first element is the old value, and the second element is the new value.
 * @property oldCell The old value of the cell.
 * @property newCell The new value of the cell.
 */
data class CellChange(val oldCell: SudokuCellData, val newCell: SudokuCellData)

infix fun SudokuCellData.changedTo(newCell: SudokuCellData) = CellChange(this, newCell)
