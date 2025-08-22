package com.example.domain.game.usecases.input

import com.example.domain.core.CellChange
import com.example.domain.core.Operation
import com.example.domain.core.OperationRepository

class RecordUndoOperationUseCase(private val operationRepository: OperationRepository) {
	suspend operator fun invoke(changes: List<CellChange>) {
		val newOperation = Operation(
			id = operationRepository.getUndoOperations().size.toLong(),
			changes = changes,
		)
		operationRepository.addUndoOperation(newOperation)
		operationRepository.clearRedoOperations()
	}
}
