package io.github.mgrablo.sudokucore.solver

object HintExplanationFactory {
	fun createStrategyFor(hintType: HintType): HintExplanationStrategy = when (hintType) {
		is HintType.NakedSingle -> NakedSingleExplanation()
		is HintType.HiddenSingle -> HiddenSingleExplanation()
		is HintType.PointingCandidate -> PointingCandidateExplanation()
		is HintType.ClaimingCandidate -> ClaimingCandidateExplanation()
	}
}
