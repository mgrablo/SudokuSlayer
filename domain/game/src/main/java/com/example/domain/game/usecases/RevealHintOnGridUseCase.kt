package com.example.domain.game.usecases

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintType

class RevealHintOnGridUseCase(private val inputNumberUseCase: InputNumberUseCase) {
	suspend operator fun invoke(hint: Hint, grid: SudokuGrid): SudokuGrid = when (hint.type) {
		is HintType.PointingCandidate -> revealPointingCandidate(hint, grid)

		is HintType.ClaimingCandidate -> revealClaimingCandidate(hint, grid)

		is HintType.HiddenSingle, is HintType.NakedSingle ->
			inputNumberUseCase(
				sudokuGrid = grid,
				number = hint.value,
				row = hint.row,
				column = hint.col,
				isNote = false,
				isHint = true,
			)
	}

	private suspend fun revealPointingCandidate(hint: Hint, grid: SudokuGrid): SudokuGrid {
		val otherCells = hint.enforcingCells
		var updatedGrid = grid
		otherCells.forEach { cell ->
			updatedGrid =
				inputNumberUseCase(
					sudokuGrid = updatedGrid,
					number = hint.value,
					row = cell.row,
					column = cell.col,
					isNote = true,
					isHint = true,
				)
			println(cell)
		}
		return updatedGrid
	}

	private suspend fun revealClaimingCandidate(hint: Hint, grid: SudokuGrid): SudokuGrid {
		val otherCells = hint.enforcingCells
		var updatedGrid = grid
		otherCells.forEach { cell ->
			updatedGrid = inputNumberUseCase(
				sudokuGrid = updatedGrid,
				number = hint.value,
				row = cell.row,
				column = cell.col,
				isNote = true,
				isHint = true,
			)
		}
		return updatedGrid
	}
}
