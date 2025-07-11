package com.example.domain.game.usecases

import com.example.domain.core.OperationRepository
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

class UndoOperationUseCase(
	private val operationRepository: OperationRepository,
	private val inputNumberUseCase: InputNumberUseCase,
	private val highlightMatchingNumbersUseCase: HighlightMatchingNumbersUseCase,
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

				var updatedGrid = grid
				lastOperation.changes.forEach { change ->
					val oldCell = change.oldCell
					val newCell = change.newCell
					val symmetricDifference =
						(oldCell.cornerNotes - newCell.cornerNotes) union
							(newCell.cornerNotes - oldCell.cornerNotes)
					val isNote = symmetricDifference.isNotEmpty() && oldCell.number == 0
					if (isNote) {
						symmetricDifference.forEach { noteVaule ->
							updatedGrid = inputNumberUseCase(
								sudokuGrid = updatedGrid,
								number = noteVaule,
								row = oldCell.row,
								column = oldCell.col,
								isNote = true,
								isHint = oldCell.attributes.contains(element = CellAttributes.HINT_REVEALED),
							)
						}
					} else {
						updatedGrid = inputNumberUseCase(
							sudokuGrid = updatedGrid,
							number = oldCell.number,
							row = oldCell.row,
							column = oldCell.col,
							isNote = false,
							isHint = oldCell.attributes.contains(CellAttributes.HINT_REVEALED),
						)
						updatedGrid = highlightMatchingNumbersUseCase(updatedGrid, oldCell.number)
					}
				}

				return updatedGrid
			}
		} finally {
			isProcessing.set(false)
		}
	}
}
