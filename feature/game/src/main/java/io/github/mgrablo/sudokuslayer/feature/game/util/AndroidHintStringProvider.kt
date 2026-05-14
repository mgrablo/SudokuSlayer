package io.github.mgrablo.sudokuslayer.feature.game.util

import android.content.Context
import io.github.mgrablo.sudokucore.hints.HintStringKey
import io.github.mgrablo.sudokucore.hints.HintStringProvider
import io.github.mgrablo.sudokuslayer.feature.game.R

/**
 * Android implementation of HintStringProvider that retrieves strings from Android resources.
 */
class AndroidHintStringProvider(private val context: Context) : HintStringProvider {
	override fun getString(key: HintStringKey, vararg formatArgs: Any): String {
		val resourceId = getResourceIdForKey(key)
		return if (formatArgs.isEmpty()) {
			context.getString(resourceId)
		} else {
			context.getString(resourceId, *formatArgs)
		}
	}

	private fun getResourceIdForKey(key: HintStringKey): Int = when (key) {
		// Scope parts
		HintStringKey.ROW -> R.string.hint_row

		HintStringKey.COLUMN -> R.string.hint_column

		// Technique names
		HintStringKey.TECHNIQUE_NAKED_SINGLE -> R.string.hint_technique_naked_single

		HintStringKey.TECHNIQUE_HIDDEN_SINGLE -> R.string.hint_technique_hidden_single

		HintStringKey.TECHNIQUE_POINTING_CANDIDATE -> R.string.hint_technique_pointing_candidate

		HintStringKey.TECHNIQUE_CLAIMING_CANDIDATE -> R.string.hint_technique_claiming_candidate

		// Naked Single
		HintStringKey.NAKED_SINGLE_STEP_1 -> R.string.hint_naked_single_step_1

		HintStringKey.NAKED_SINGLE_STEP_2 -> R.string.hint_naked_single_step_2

		HintStringKey.NAKED_SINGLE_STEP_3 -> R.string.hint_naked_single_step_3

		// Hidden Single
		HintStringKey.HIDDEN_SINGLE_STEP_1 -> R.string.hint_hidden_single_step_1

		HintStringKey.HIDDEN_SINGLE_STEP_2 -> R.string.hint_hidden_single_step_2

		HintStringKey.HIDDEN_SINGLE_STEP_3 -> R.string.hint_hidden_single_step_3

		// Claiming Candidate
		HintStringKey.CLAIMING_CANDIDATE_STEP_1 -> R.string.hint_claiming_candidate_step_1

		HintStringKey.CLAIMING_CANDIDATE_STEP_2 -> R.string.hint_claiming_candidate_step_2

		HintStringKey.CLAIMING_CANDIDATE_STEP_3 -> R.string.hint_claiming_candidate_step_3

		HintStringKey.CLAIMING_CANDIDATE_STEP_4 -> R.string.hint_claiming_candidate_step_4

		// Pointing Candidate
		HintStringKey.POINTING_CANDIDATE_STEP_1 -> R.string.hint_pointing_candidate_step_1

		HintStringKey.POINTING_CANDIDATE_STEP_2 -> R.string.hint_pointing_candidate_step_2

		HintStringKey.POINTING_CANDIDATE_STEP_3 -> R.string.hint_pointing_candidate_step_3

		HintStringKey.POINTING_CANDIDATE_STEP_4 -> R.string.hint_pointing_candidate_step_4
	}
}
