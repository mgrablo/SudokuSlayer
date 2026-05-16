package io.github.mgrablo.sudokuslayer.domain.game.usecases.hint

import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintProvider
import io.github.mgrablo.sudokucore.hints.fillCandidates
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokuslayer.domain.core.Game
import kotlinx.collections.immutable.minus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProvideHintUseCase(private val hintProvider: HintProvider) {
	suspend operator fun invoke(game: Game): Hint? = withContext(Dispatchers.Default) {
		var grid = hintProvider.fillCandidates(game.grid.data)
		grid =
			removeCandidates(
				gridData = grid,
				hints = game.hintLogs.map { it.hint },
			)
		hintProvider.provideHint(data = grid)
	}

	private fun removeCandidates(
		gridData: List<SudokuCellData>,
		hints: List<Hint>,
	): List<SudokuCellData> {
		val filledCandidatesGrid = gridData.toMutableList()
		hints.forEach { hint ->
			when (hint) {
				// Hint that removes candidates from cells
				is Hint.EliminationHint -> {
					hint.affectedCells.forEach { affectedCell ->
						filledCandidatesGrid
							.indexOfFirst { it.row == affectedCell.row && it.col == affectedCell.col }
							.takeIf { it != -1 }
							?.let { index ->
								val cell = filledCandidatesGrid[index]
								filledCandidatesGrid[index] =
									cell.copy(
										candidates = cell.candidates - hint.number,
									)
							}
					}
				}

				// Hints that fill cells
				is Hint.ResolutionHint -> Unit
			}
		}
		return filledCandidatesGrid
	}
}
