package io.github.mgrablo.sudokuslayer.feature.game.hints

/**
 * UI representation of an explanation for a hint.
 *
 * @property instruction A short text telling users where to look at ("Look at cell [1, 2]", "Inspect block 1").
 * @property description Explanation of why this digit should be placed / why this digit should be eliminated from candidates.
 * @property actionText Text describing what cells were updated when the user clicks "reveal hint".
 * @property hintTypeName Simple name of the hint type ("Hidden Single", "Pointing Candidate").
 */
internal data class HintExplanation(
	val instruction: HintMessage,
	val description: HintMessage,
	val actionText: HintMessage,
	val hintTypeName: HintMessage,
)
