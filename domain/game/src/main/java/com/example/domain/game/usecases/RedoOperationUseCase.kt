package com.example.domain.game.usecases

import com.example.domain.game.repositories.OperationRepository
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

class RedoOperationUseCase(
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
				val oldCell = lastOperation.oldCell
				val newCell = lastOperation.cell
				val symmetricDifference =
					(oldCell.cornerNotes - newCell.cornerNotes) union
						(newCell.cornerNotes - oldCell.cornerNotes)

				val isNote = symmetricDifference.isNotEmpty() && newCell.number == 0
				return if (isNote) {
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
					updatedGrid
				} else {
					inputNumberUseCase(
						sudokuGrid = grid,
						number = newCell.number,
						row = newCell.row,
						column = newCell.col,
						isNote = false,
						isHint = newCell.attributes.contains(CellAttributes.HINT_REVEALED),
					)
				}
			}
		} finally {
			isProcessing.set(false)
		}
	}
}
