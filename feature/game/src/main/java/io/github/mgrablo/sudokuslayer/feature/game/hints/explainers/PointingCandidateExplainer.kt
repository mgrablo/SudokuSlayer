package io.github.mgrablo.sudokuslayer.feature.game.hints.explainers

import android.content.Context
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokuslayer.feature.game.R
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintExplainer
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintExplanation
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintMessage
import io.github.mgrablo.sudokuslayer.feature.game.hints.getHintMessage

internal object PointingCandidateExplainer : HintExplainer<Hint.PointingCandidate> {
	override fun explain(context: Context, hint: Hint.PointingCandidate): HintExplanation {
		val insstruction = getInstruction(context, hint)
		val description = getDescription(context, hint)
		val actionText = getActionText(context, hint)
		val hintTypeName = getHintTypeName(context)

		return HintExplanation(
			instruction = insstruction,
			description = description,
			actionText = actionText,
			hintTypeName = hintTypeName,
		)
	}

	private fun getInstruction(context: Context, hint: Hint.PointingCandidate): HintMessage {
		val instructionId = R.string.hint_pointing_candidate_instruction
		return getHintMessage(context, instructionId, hint.blockHouseType.id + 1)
	}

	private fun getDescription(context: Context, hint: Hint.PointingCandidate): HintMessage {
		val descriptionId = when (hint.lineHouseType) {
			is Hint.HouseType.Row -> R.string.hint_pointing_candidate_description_row
			is Hint.HouseType.Column -> R.string.hint_pointing_candidate_description_column
			else -> throw IllegalArgumentException("lineHouseType must be either Row or Column")
		}

		val lineHouseId = hint.lineHouseType.id + 1
		val blockHouseId = hint.blockHouseType.id + 1

		return getHintMessage(
			context,
			descriptionId,
			blockHouseId,
			hint.number,
			lineHouseId,
		)
	}

	private fun getActionText(context: Context, hint: Hint.PointingCandidate): HintMessage {
		val actionTextId = R.string.hint_pointing_candidate_action
		val affectedCells = hint.affectedCells.joinToString { "[${it.row + 1}, ${it.col + 1}]" }
		return getHintMessage(context, actionTextId, affectedCells)
	}

	private fun getHintTypeName(context: Context): HintMessage =
		getHintMessage(context, R.string.hint_pointing_candidate_name)
}
