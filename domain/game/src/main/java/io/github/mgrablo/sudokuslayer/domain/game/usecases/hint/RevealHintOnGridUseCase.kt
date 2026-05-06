package io.github.mgrablo.sudokuslayer.domain.game.usecases.hint

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokuslayer.domain.game.GameUpdate
import io.github.mgrablo.sudokuslayer.domain.game.usecases.game.CalculateGridChangesUseCase

class RevealHintOnGridUseCase(
	private val calculateGridChangesUseCase: CalculateGridChangesUseCase,
) {
	suspend operator fun invoke(hint: Hint, grid: SudokuGrid): GameUpdate = when (hint.type) {
		is HintType.PointingCandidate,
		is HintType.ClaimingCandidate,
		-> applyNoteHints(hint, grid)

		is HintType.HiddenSingle,
		is HintType.NakedSingle,
		-> placeHintDigit(hint, grid)
	}

	private suspend fun placeHintDigit(hint: Hint, grid: SudokuGrid): GameUpdate =
		calculateGridChangesUseCase(
			initialGrid = grid,
			number = hint.value,
			row = hint.row,
			column = hint.col,
			isNote = false,
			isHint = true,
		)

	private suspend fun applyNoteHints(hint: Hint, grid: SudokuGrid): GameUpdate {
		val enforcingCells = hint.enforcingCells
		return enforcingCells.fold(GameUpdate(grid, emptyList())) { acc, cell ->
			calculateGridChangesUseCase(
				initialGrid = acc.resultingGrid,
				number = hint.value,
				row = cell.row,
				column = cell.col,
				isNote = true,
				isHint = true,
			)
		}
	}
}
