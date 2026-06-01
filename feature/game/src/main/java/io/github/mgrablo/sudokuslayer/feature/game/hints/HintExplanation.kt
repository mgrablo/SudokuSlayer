package io.github.mgrablo.sudokuslayer.feature.game.hints

import android.content.Context
import androidx.compose.runtime.Immutable
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokuslayer.feature.game.hints.explainers.ClaimingCandidateExplainer
import io.github.mgrablo.sudokuslayer.feature.game.hints.explainers.HiddenSingleExplainer
import io.github.mgrablo.sudokuslayer.feature.game.hints.explainers.NakedSingleExplainer
import io.github.mgrablo.sudokuslayer.feature.game.hints.explainers.PointingCandidateExplainer

/**
 * UI representation of an explanation for a hint.
 *
 * @property instruction A short text telling users where to look at ("Look at cell [1, 2]", "Inspect block 1").
 * @property description Explanation of why this digit should be placed / why this digit should be eliminated from candidates.
 * @property actionText Text describing what cells were updated when the user clicks "reveal hint".
 * @property hintTypeName Simple name of the hint type ("Hidden Single", "Pointing Candidate").
 */
@Immutable
internal data class HintExplanation(
	val instruction: HintMessage,
	val description: HintMessage,
	val actionText: HintMessage,
	val hintTypeName: HintMessage,
)

internal fun Hint.toExplanation(context: Context) = when (this) {
	is Hint.NakedSingle -> NakedSingleExplainer.explain(context, this)
	is Hint.HiddenSingle -> HiddenSingleExplainer.explain(context, this)
	is Hint.ClaimingCandidate -> ClaimingCandidateExplainer.explain(context, this)
	is Hint.PointingCandidate -> PointingCandidateExplainer.explain(context, this)
}
