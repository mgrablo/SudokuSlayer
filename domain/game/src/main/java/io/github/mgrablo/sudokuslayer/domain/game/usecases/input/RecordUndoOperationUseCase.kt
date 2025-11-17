package io.github.mgrablo.sudokuslayer.domain.game.usecases.input

import io.github.mgrablo.sudokuslayer.domain.core.CellChange
import io.github.mgrablo.sudokuslayer.domain.core.Operation
import io.github.mgrablo.sudokuslayer.domain.core.OperationRepository

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
