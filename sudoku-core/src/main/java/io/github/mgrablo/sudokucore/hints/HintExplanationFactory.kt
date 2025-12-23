package io.github.mgrablo.sudokucore.hints

import io.github.mgrablo.sudokucore.hints.strategies.HiddenSingleExplanation
import io.github.mgrablo.sudokucore.hints.strategies.NakedSingleExplanation

object HintExplanationFactory {
	fun createStrategyFor(hintType: HintType): HintExplanationStrategy = when (hintType) {
		is HintType.NakedSingle -> NakedSingleExplanation()
		is HintType.HiddenSingle -> HiddenSingleExplanation()
		is HintType.PointingCandidate -> PointingCandidateExplanation()
		is HintType.ClaimingCandidate -> ClaimingCandidateExplanation()
	}
}
