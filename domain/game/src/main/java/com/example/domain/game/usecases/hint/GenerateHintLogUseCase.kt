package com.example.domain.game.usecases.hint

import com.example.domain.core.HintLog
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintExplanationFactory
import kotlinx.collections.immutable.toPersistentList

class GenerateHintLogUseCase {
	operator fun invoke(id: Int, hint: Hint, grid: SudokuGrid): HintLog {
		val explanationStrategy =
			hint.explanationStrategy ?: HintExplanationFactory.createStrategyFor(hint.type)
		val explanationSteps =
			explanationStrategy
				.generateHintExplanationSteps(
					grid = grid,
					hint = hint,
				).toPersistentList()
		return HintLog(
			id = id,
			hint = hint,
			isUserGuessed = false,
			isRevealed = false,
			explanation = explanationSteps,
		)
	}
}
