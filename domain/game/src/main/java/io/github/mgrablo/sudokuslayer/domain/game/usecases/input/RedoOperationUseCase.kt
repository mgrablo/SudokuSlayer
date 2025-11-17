package io.github.mgrablo.sudokuslayer.domain.game.usecases.input

import io.github.mgrablo.sudokucore.model.CellAttributes
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokuslayer.domain.core.OperationRepository
import io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals.HighlightMatchingNumbersUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

class RedoOperationUseCase(
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
						.getRedoOperations()
						.reversed()
						.firstOrNull()
				if (lastOperation == null) {
					return grid
				}

				operationRepository.addUndoOperation(lastOperation)
				val redoId = operationRepository.findRedoOperation(lastOperation.id)
				if (redoId != null) {
					operationRepository.removeRedoOperation(redoId.id)
				}

				var updatedGrid = grid
				lastOperation.changes.forEach { change ->
					val oldCell = change.oldCell
					val newCell = change.newCell
					val symmetricDifference =
						(oldCell.cornerNotes - newCell.cornerNotes) union
							(newCell.cornerNotes - oldCell.cornerNotes)

					val isNote = symmetricDifference.isNotEmpty() && newCell.number == 0

					if (isNote) {
						symmetricDifference.forEach { noteVaule ->
							updatedGrid = inputNumberUseCase(
								sudokuGrid = updatedGrid,
								number = noteVaule,
								row = newCell.row,
								column = newCell.col,
								isNote = true,
								isHint = newCell.attributes.contains(element = CellAttributes.HINT_REVEALED),
							)
						}
					} else {
						updatedGrid = inputNumberUseCase(
							sudokuGrid = updatedGrid,
							number = newCell.number,
							row = newCell.row,
							column = newCell.col,
							isNote = false,
							isHint = newCell.attributes.contains(CellAttributes.HINT_REVEALED),
						)
						updatedGrid = highlightMatchingNumbersUseCase(updatedGrid, newCell.number)
					}
				}
				return updatedGrid
			}
		} finally {
			isProcessing.set(false)
		}
	}
}
