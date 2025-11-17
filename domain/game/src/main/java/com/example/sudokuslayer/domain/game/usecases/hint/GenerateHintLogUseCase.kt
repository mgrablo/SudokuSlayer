package com.example.sudokuslayer.domain.game.usecases.hint

import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintExplanationFactory
import com.example.sudoku.solver.HintStringProvider
import com.example.sudokuslayer.domain.core.HintLog
import kotlinx.collections.immutable.toPersistentList

class GenerateHintLogUseCase {
	operator fun invoke(
		id: Int,
		hint: Hint,
		grid: SudokuGrid,
		stringProvider: HintStringProvider = HintStringProvider.DEFAULT,
	): HintLog {
		val explanationStrategy =
			hint.explanationStrategy ?: HintExplanationFactory.createStrategyFor(hint.type)
		val explanationSteps =
			explanationStrategy
				.generateStructuredHintExplanation(
					grid = grid,
					hint = hint,
					stringProvider = stringProvider,
				).toPersistentList()
		return HintLog(
			id = id,
			hint = hint,
			isUserGuessed = false,
			isRevealed = false,
			structuredExplanation = explanationSteps,
		)
	}
}
