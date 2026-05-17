package io.github.mgrablo.sudokuslayer.feature.game.hints.explainers

import android.content.Context
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokuslayer.feature.game.R
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintExplainer
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintExplanation
import io.github.mgrablo.sudokuslayer.feature.game.hints.getHintMessage

internal object NakedSingleExplainer : HintExplainer<Hint.NakedSingle> {
	override fun explain(context: Context, hint: Hint.NakedSingle): HintExplanation {
		val (row, column, number) = hint
		val instruction =
			getHintMessage(context, R.string.hint_naked_single_instruction, hint.cellCoordinateString())
		val description =
			getHintMessage(context, R.string.hint_naked_single_description, number)
		val actionText =
			getHintMessage(
				context,
				R.string.hint_naked_single_action,
				number,
				hint.cellCoordinateString(),
			)
		val hintTypeName = getHintMessage(context, R.string.hint_naked_single_name)

		return HintExplanation(
			instruction = instruction,
			description = description,
			actionText = actionText,
			hintTypeName = hintTypeName,
		)
	}

	private fun Hint.NakedSingle.cellCoordinateString(): String = "[${row + 1}, ${col + 1}]"
}
