package com.example.domain.game.usecases

import com.example.domain.game.repositories.OperationRepository
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

class UndoOperationUseCase(
	private val operationRepository: OperationRepository,
	private val inputNumberUseCase: InputNumberUseCase,
) {
	private val mutex = Mutex()
	private val isProcessing = AtomicBoolean(false)

	suspend operator fun invoke(grid: SudokuGrid): SudokuGrid {
		try {
			if (!isProcessing.compareAndSet(false, true)) {
				return grid
			}

			mutex.withLock {
				val lastOperation =
					operationRepository
						.getUndoOperations()
						.lastOrNull()
				if (lastOperation == null) {
					return grid
				}

				operationRepository.addRedoOperation(lastOperation)
				val undoId = operationRepository.findUndoOperation(lastOperation.id)
				if (undoId != null) {
					operationRepository.removeUndoOperation(undoId.id)
				}

				val oldCell = lastOperation.oldCell
				val newCell = lastOperation.cell
				val symmetricDifference =
					(oldCell.cornerNotes - newCell.cornerNotes) union
						(newCell.cornerNotes - oldCell.cornerNotes)
				val isNote = symmetricDifference.isNotEmpty()
				return inputNumberUseCase(
					sudokuGrid = grid,
					number = if (isNote) symmetricDifference.first() else oldCell.number,
					row = oldCell.row,
					column = oldCell.col,
					isNote = isNote,
					isHint = oldCell.attributes.contains(CellAttributes.HINT_REVEALED),
				)
			}
		} finally {
			isProcessing.set(false)
		}
	}
}
