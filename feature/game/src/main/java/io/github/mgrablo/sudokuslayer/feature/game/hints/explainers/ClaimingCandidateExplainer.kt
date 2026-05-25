package io.github.mgrablo.sudokuslayer.feature.game.hints.explainers

import android.content.Context
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokuslayer.feature.game.R
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintExplainer
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintExplanation
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintMessage
import io.github.mgrablo.sudokuslayer.feature.game.hints.getHintMessage

internal object ClaimingCandidateExplainer : HintExplainer<Hint.ClaimingCandidate> {
	override fun explain(context: Context, hint: Hint.ClaimingCandidate): HintExplanation {
		val instruction = getInstruction(hint.houseType, context)
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

	private fun getInstruction(houseType: Hint.HouseType, context: Context): HintMessage {
		val instructionId = when (houseType) {
			is Hint.HouseType.Row -> R.string.hint_claiming_candidate_instruction_row

			is Hint.HouseType.Column -> R.string.hint_claiming_candidate_instruction_column

			else -> throw IllegalArgumentException(
				"houseType must be either HouseType.Row or HouseType.Column",
			)
		}
		return getHintMessage(context, instructionId, houseType.id + 1)
	}

	private fun getDescription(hint: Hint.ClaimingCandidate, context: Context): HintMessage {
		val descriptionId = when (hint.houseType) {
			is Hint.HouseType.Row -> R.string.hint_claiming_candidate_description_row

			is Hint.HouseType.Column -> R.string.hint_claiming_candidate_description_column

			else -> throw IllegalArgumentException(
				"houseType must be either HouseType.Row or HouseType.Column",
			)
		}
		val lineHouseId = hint.lineHouseType.id + 1
		val enforcingCells = hint.enforcingCells.joinToString { "[${it.row + 1}, ${it.col + 1}]" }
		return getHintMessage(
			context,
			descriptionId,
			lineHouseId,
			hint.number,
			enforcingCells,
			hint.blockHouseType.id + 1,
		)
	}

	private fun getActionText(hint: Hint.ClaimingCandidate, context: Context): HintMessage {
		val actionTextId = R.string.hint_claiming_candidate_action
		val affectedCells = hint.affectedCells.joinToString { "[${it.row + 1}, ${it.col + 1}]" }
		return getHintMessage(context, actionTextId, affectedCells)
	}

	private fun getHintTypeName(context: Context): HintMessage =
		getHintMessage(context, R.string.hint_claiming_candidate_name)
}
