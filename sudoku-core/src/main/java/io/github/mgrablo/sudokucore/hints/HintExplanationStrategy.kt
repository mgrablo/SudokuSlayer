package io.github.mgrablo.sudokucore.hints

import androidx.compose.runtime.Stable
import io.github.mgrablo.sudokucore.model.SudokuGrid

@Stable
interface HintExplanationStrategy {
	fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider = HintStringProvider.DEFAULT,
	): List<HintExplanationStep>
}
