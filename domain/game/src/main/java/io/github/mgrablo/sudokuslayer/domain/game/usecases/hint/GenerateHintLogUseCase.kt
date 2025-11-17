package io.github.mgrablo.sudokuslayer.domain.game.usecases.hint

import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.solver.Hint
import io.github.mgrablo.sudokucore.solver.HintExplanationFactory
import io.github.mgrablo.sudokucore.solver.HintStringProvider
import io.github.mgrablo.sudokuslayer.domain.core.HintLog
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
