package com.example.feature.game.util

import android.content.Context
import com.example.sudoku.solver.HintStringKey
import com.example.sudoku.solver.HintStringProvider
import com.example.sudokuslayer.feature.game.R

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
		// General hint parts
		HintStringKey.FOCUS_ON_CELL -> R.string.hint_focus_on_cell
		HintStringKey.FOCUS_ON_BLOCK -> R.string.hint_focus_on_block
		HintStringKey.THEREFORE -> R.string.hint_therefore
		HintStringKey.MUST_CONTAIN -> R.string.hint_must_contain
		HintStringKey.THE_CELL_AT -> R.string.hint_the_cell_at
		HintStringKey.IN -> R.string.hint_in
		HintStringKey.AFFECTED_CELLS_ARE -> R.string.hint_affected_cells_are
		HintStringKey.NO_OTHER_EMPTY_CELLS -> R.string.hint_no_other_empty_cells
		HintStringKey.CAN_ONLY_BE_PLACED_IN -> R.string.hint_can_only_be_placed_in
		HintStringKey.CANNOT_BE_PLACED_IN -> R.string.hint_cannot_be_placed_in
		HintStringKey.ONLY_POSSIBLE_CANDIDATE -> R.string.hint_only_possible_candidate
		HintStringKey.FOLLOWING_CELLS -> R.string.hint_following_cells
		HintStringKey.SINCE -> R.string.hint_since
		HintStringKey.THUS_REMOVE_FROM -> R.string.hint_thus_remove_from
		HintStringKey.THEY_ARE_BLOCKED_BY -> R.string.hint_they_are_blocked_by
		HintStringKey.CAN_ONLY_APPEAR_IN_THE -> R.string.hint_can_only_appear_in_the
		HintStringKey.CANNOT_APPEAR_IN_THE -> R.string.hint_cannot_appear_in_the
		HintStringKey.SINCE_CONFINED_TO -> R.string.hint_since_confined_to

		// Scope parts
		HintStringKey.ROW -> R.string.hint_row
		HintStringKey.COLUMN -> R.string.hint_column
		HintStringKey.BLOCK -> R.string.hint_block
		HintStringKey.BLOCK_PART -> R.string.hint_block_part

		// Position descriptions
		HintStringKey.TOP -> R.string.hint_top
		HintStringKey.MIDDLE -> R.string.hint_middle
		HintStringKey.BOTTOM -> R.string.hint_bottom
		HintStringKey.LEFT -> R.string.hint_left
		HintStringKey.CENTER -> R.string.hint_center
		HintStringKey.RIGHT -> R.string.hint_right

		// Other hints context
		HintStringKey.SAME_ROW -> R.string.hint_same_row
		HintStringKey.SAME_COLUMN -> R.string.hint_same_column
		HintStringKey.SAME_BLOCK -> R.string.hint_same_block

		// Technique names
		HintStringKey.TECHNIQUE_NAKED_SINGLE -> R.string.hint_technique_naked_single
		HintStringKey.TECHNIQUE_HIDDEN_SINGLE -> R.string.hint_technique_hidden_single
		HintStringKey.TECHNIQUE_POINTING_CANDIDATE -> R.string.hint_technique_pointing_candidate
		HintStringKey.TECHNIQUE_CLAIMING_CANDIDATE -> R.string.hint_technique_claiming_candidate
	}
}
