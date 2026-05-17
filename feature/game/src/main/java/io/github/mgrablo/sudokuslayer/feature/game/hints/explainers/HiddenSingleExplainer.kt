package io.github.mgrablo.sudokuslayer.feature.game.hints.explainers

import android.content.Context
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokuslayer.feature.game.R
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintExplainer
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintExplanation
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintMessage
import io.github.mgrablo.sudokuslayer.feature.game.hints.getHintMessage

internal object HiddenSingleExplainer : HintExplainer<Hint.HiddenSingle> {
	override fun explain(context: Context, hint: Hint.HiddenSingle): HintExplanation {
		val instruction = getInstruction(hint.groupType, context)
		val description = getDescription(hint, context)
		val actionText = getActionText(hint, context)
		val hintTypeName = getHintTypeName(context)

		return HintExplanation(
			instruction = instruction,
			description = description,
			actionText = actionText,
			hintTypeName = hintTypeName,
		)
	}

	private fun getInstruction(group: Hint.GroupType, context: Context): HintMessage {
		val instructionId = when (group) {
			is Hint.GroupType.Row -> R.string.hint_hidden_single_instruction_row
			is Hint.GroupType.Column -> R.string.hint_hidden_single_instruction_column
			is Hint.GroupType.Block -> R.string.hint_hidden_single_instruction_block
		}
		return getHintMessage(context, instructionId, group.id + 1)
	}

	private fun getDescription(hint: Hint.HiddenSingle, context: Context): HintMessage {
		val descriptionId = when (hint.groupType) {
			is Hint.GroupType.Row -> R.string.hint_hidden_single_description_row
			is Hint.GroupType.Column -> R.string.hint_hidden_single_description_column
			is Hint.GroupType.Block -> R.string.hint_hidden_single_description_block
		}
		return getHintMessage(
			context,
			descriptionId,
			hint.number,
			hint.groupType.id + 1,
			"[${hint.row + 1}, ${hint.col + 1}]",
		)
	}

	private fun getActionText(hint: Hint.HiddenSingle, context: Context): HintMessage {
		val actionTextId = R.string.hint_hidden_single_action
		return getHintMessage(context, actionTextId, hint.number, "[${hint.row + 1}, ${hint.col + 1}]")
	}

	private fun getHintTypeName(context: Context): HintMessage =
		getHintMessage(context, R.string.hint_hidden_single_name)
}
