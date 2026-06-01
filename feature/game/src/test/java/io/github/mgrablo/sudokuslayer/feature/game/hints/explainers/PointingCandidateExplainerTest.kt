package io.github.mgrablo.sudokuslayer.feature.game.hints.explainers

import android.content.Context
import androidx.compose.ui.text.AnnotatedString
import androidx.test.core.app.ApplicationProvider
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokuslayer.feature.game.hints.formatCellCoordinate
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import tech.apter.junit.jupiter.robolectric.RobolectricExtension
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(RobolectricExtension::class)
class PointingCandidateExplainerTest {
	@Test
	fun `explains should replace placeholders with actual values for row`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = createHint(
			lineHouseType = Hint.HouseType.Row(2),
			blockHouseType = Hint.HouseType.Block(1),
			number = 5,
			enforcingCells = persistentSetOf(
				SudokuCellData(row = 0, col = 1),
				SudokuCellData(row = 0, col = 2),
			),
			affectedCells = persistentSetOf(
				SudokuCellData(row = 0, col = 4),
				SudokuCellData(row = 0, col = 6),
			),
		)

		val result = PointingCandidateExplainer.explain(context, hint)
		val instructionText = result.instruction.annotatedString.text
		val descriptionText = result.description.annotatedString.text
		val actionText = result.actionText.annotatedString.text
		val hintTypeNameText = result.hintTypeName.annotatedString.text

		assertTrue(instructionText.contains("block 2"))
		assertTrue(descriptionText.contains("block 2"))
		assertTrue(descriptionText.contains("row 3"))
		assertTrue(descriptionText.contains("5"))
		assertTrue(actionText.contains(formatCellCoordinate(0, 4)))
		assertTrue(actionText.contains(formatCellCoordinate(0, 6)))

		assertFalse(instructionText.contains("^1"))
		assertFalse(descriptionText.contains("^1"))
		assertFalse(descriptionText.contains("^2"))
		assertFalse(descriptionText.contains("^3"))
		assertFalse(actionText.contains("^1"))
		assertFalse(hintTypeNameText.contains("^1"))
	}

	@Test
	fun `explains should replace placeholders with actual values for column`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = createHint(
			lineHouseType = Hint.HouseType.Column(3),
			blockHouseType = Hint.HouseType.Block(4),
			number = 7,
			enforcingCells = persistentSetOf(
				SudokuCellData(row = 1, col = 3),
				SudokuCellData(row = 2, col = 3),
			),
			affectedCells = persistentSetOf(
				SudokuCellData(row = 4, col = 3),
			),
		)

		val result = PointingCandidateExplainer.explain(context, hint)
		val instructionText = result.instruction.annotatedString.text
		val descriptionText = result.description.annotatedString.text
		val actionText = result.actionText.annotatedString.text

		assertTrue(instructionText.contains("block 5"))
		assertTrue(descriptionText.contains("block 5"))
		assertTrue(descriptionText.contains("column 4"))
		assertTrue(descriptionText.contains("7"))
		assertTrue(actionText.contains(formatCellCoordinate(4, 3)))
	}

	@Test
	fun `explains should annotate block in instruction`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = createHint(
			lineHouseType = Hint.HouseType.Row(2),
			blockHouseType = Hint.HouseType.Block(1),
			number = 5,
			enforcingCells = persistentSetOf(SudokuCellData(row = 0, col = 1)),
			affectedCells = persistentSetOf(SudokuCellData(row = 0, col = 4)),
		)

		val result = PointingCandidateExplainer.explain(context, hint)
		val instruction = result.instruction.annotatedString

		assertTrue(
			instruction.containsStringAnnotation(
				tag = "type",
				value = "block",
			),
		)
	}

	@Test
	fun `explains should annotate block row and digit in description for row`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = createHint(
			lineHouseType = Hint.HouseType.Row(2),
			blockHouseType = Hint.HouseType.Block(1),
			number = 5,
			enforcingCells = persistentSetOf(SudokuCellData(row = 0, col = 1)),
			affectedCells = persistentSetOf(SudokuCellData(row = 0, col = 4)),
		)

		val result = PointingCandidateExplainer.explain(context, hint)
		val description = result.description.annotatedString

		assertTrue(description.containsStringAnnotation(tag = "type", value = "block"))
		assertTrue(description.containsStringAnnotation(tag = "type", value = "digit"))
		assertTrue(description.containsStringAnnotation(tag = "type", value = "row"))
	}

	@Test
	fun `explains should annotate block column and digit in description for column`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = createHint(
			lineHouseType = Hint.HouseType.Column(3),
			blockHouseType = Hint.HouseType.Block(4),
			number = 7,
			enforcingCells = persistentSetOf(SudokuCellData(row = 1, col = 3)),
			affectedCells = persistentSetOf(SudokuCellData(row = 4, col = 3)),
		)

		val result = PointingCandidateExplainer.explain(context, hint)
		val description = result.description.annotatedString

		assertTrue(description.containsStringAnnotation(tag = "type", value = "block"))
		assertTrue(description.containsStringAnnotation(tag = "type", value = "digit"))
		assertTrue(description.containsStringAnnotation(tag = "type", value = "column"))
	}

	@Test
	fun `explains should annotate cell in action text`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = createHint(
			lineHouseType = Hint.HouseType.Row(2),
			blockHouseType = Hint.HouseType.Block(1),
			number = 5,
			enforcingCells = persistentSetOf(SudokuCellData(row = 0, col = 1)),
			affectedCells = persistentSetOf(SudokuCellData(row = 0, col = 4)),
		)

		val result = PointingCandidateExplainer.explain(context, hint)
		val actionText = result.actionText.annotatedString

		assertTrue(actionText.containsStringAnnotation(tag = "type", value = "cell"))
	}

	@Test
	fun `explains should annotate technique in hintTypeName`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = createHint(
			lineHouseType = Hint.HouseType.Row(2),
			blockHouseType = Hint.HouseType.Block(1),
			number = 5,
			enforcingCells = persistentSetOf(SudokuCellData(row = 0, col = 1)),
			affectedCells = persistentSetOf(SudokuCellData(row = 0, col = 4)),
		)

		val result = PointingCandidateExplainer.explain(context, hint)
		val hintTypeName = result.hintTypeName.annotatedString

		assertTrue(hintTypeName.containsStringAnnotation(tag = "type", value = "technique"))
	}

	private fun createHint(
		lineHouseType: Hint.HouseType,
		blockHouseType: Hint.HouseType.Block,
		number: Int,
		enforcingCells: PersistentSet<SudokuCellData>,
		affectedCells: PersistentSet<SudokuCellData>,
	): Hint.PointingCandidate = Hint.PointingCandidate(
		lineHouseType = lineHouseType,
		blockHouseType = blockHouseType,
		number = number,
		enforcingCells = enforcingCells,
		affectedCells = affectedCells,
	)

	private fun AnnotatedString.containsStringAnnotation(tag: String, value: String): Boolean =
		this.getStringAnnotations(tag = tag, start = 0, end = this.length)
			.any { it.item == value }
}
