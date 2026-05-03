package io.github.mgrablo.sudokucore.hints.strategies

import io.github.mgrablo.sudokucore.hints.GroupType
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintExplanationPart
import io.github.mgrablo.sudokucore.hints.HintStringKey
import io.github.mgrablo.sudokucore.hints.HintStringProvider
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import kotlinx.collections.immutable.persistentSetOf
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Locked candidate explanation context tests")
class LockedCandidateExplanationContextTest {
	private val stringProvider =
		object : HintStringProvider {
			override fun getString(key: HintStringKey, vararg formatArgs: Any): String =
				when (key) {
					HintStringKey.POINTING_CANDIDATE_STEP_1 -> "Check {0}!"
					HintStringKey.POINTING_CANDIDATE_STEP_2 ->
						"In {0}, the number {1} points in a straight line. It must be in {2}."
					HintStringKey.POINTING_CANDIDATE_STEP_3 ->
						"Since {0} must be in this line, you can remove it from the notes in the rest of the {1}."
					HintStringKey.POINTING_CANDIDATE_STEP_4 -> "Update notes in: {0}."
					HintStringKey.CLAIMING_CANDIDATE_STEP_1 -> "Check {0}!"
					HintStringKey.CLAIMING_CANDIDATE_STEP_2 ->
						"In {0}, the number {1} is forced to be in one of these specific cells: {2}."
					HintStringKey.CLAIMING_CANDIDATE_STEP_3 ->
						"This means {0} is \"claimed\" by this {1}. You can erase {0} from the notes in the rest of the block."
					HintStringKey.CLAIMING_CANDIDATE_STEP_4 -> "Update notes in: {0}."
					HintStringKey.ROW -> "row"
					HintStringKey.COLUMN -> "column"
					else -> key.name
				}
		}

	@Test
	fun `pointing candidate step 3 uses column context when group is column`() {
		val hint =
			Hint(
				row = 4,
				col = 6,
				value = 3,
				type = HintType.PointingCandidate(GroupType.Column(6)),
				affectedCells =
					persistentSetOf(
						SudokuCellData(row = 4, col = 6),
						SudokuCellData(row = 7, col = 6),
					),
				enforcingCells =
					persistentSetOf(
						SudokuCellData(row = 0, col = 6),
						SudokuCellData(row = 1, col = 6),
					),
			)

		val explanation =
			PointingCandidateExplanation().generateStructuredHintExplanation(
				grid = SudokuGrid(),
				hint = hint,
				stringProvider = stringProvider,
			)

		val step3TextParts =
			explanation[2].parts
				.filterIsInstance<HintExplanationPart.Text>()
				.map { it.content }
		assertTrue(step3TextParts.contains("column"))
		assertFalse(step3TextParts.contains("row"))
	}

	@Test
	fun `claiming candidate step 3 uses row context when group is row`() {
		val hint =
			Hint(
				row = 0,
				col = 1,
				value = 4,
				type = HintType.ClaimingCandidate(GroupType.Row(0)),
				affectedCells = persistentSetOf(SudokuCellData(row = 2, col = 0)),
				enforcingCells =
					persistentSetOf(
						SudokuCellData(row = 0, col = 0),
						SudokuCellData(row = 0, col = 1),
					),
			)

		val explanation =
			ClaimingCandidateExplanation().generateStructuredHintExplanation(
				grid = SudokuGrid(),
				hint = hint,
				stringProvider = stringProvider,
			)

		val step3TextParts =
			explanation[2].parts
				.filterIsInstance<HintExplanationPart.Text>()
				.map { it.content }
		assertTrue(step3TextParts.contains("row"))
		assertFalse(step3TextParts.contains("column"))
	}
}


