package io.github.mgrablo.sudokuslayer.feature.game.hints.explainers

import android.content.Context
import androidx.compose.ui.text.AnnotatedString
import androidx.test.core.app.ApplicationProvider
import io.github.mgrablo.sudokucore.hints.Hint
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import tech.apter.junit.jupiter.robolectric.RobolectricExtension
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(RobolectricExtension::class)
class HiddenSingleExplainerTest {
	@Test
	fun `explains should replace placeholders with actual values`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 0, col = 1, number = 5, groupType = Hint.GroupType.Row(0))

		val result = HiddenSingleExplainer.explain(context, hint)
		val instructionText = result.instruction.annotatedString.text
		val descriptionText = result.description.annotatedString.text
		val actionText = result.actionText.annotatedString.text
		val hintTypeNameText = result.hintTypeName.annotatedString.text

		assertTrue(instructionText.contains("row 1"))
		assertTrue(descriptionText.contains("5"))
		assertTrue(descriptionText.contains("row 1"))
		assertTrue(descriptionText.contains("[1, 2]"))
		assertTrue(actionText.contains("5"))
		assertTrue(actionText.contains("[1, 2]"))

		assertFalse(instructionText.contains("^1"))
		assertFalse(descriptionText.contains("^1"))
		assertFalse(descriptionText.contains("^2"))
		assertFalse(descriptionText.contains("^3"))
		assertFalse(actionText.contains("^1"))
		assertFalse(actionText.contains("^2"))
		assertFalse(hintTypeNameText.contains("^1"))
	}

	@Test
	fun `explains should use 1-based row and coordinates`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 3, col = 4, number = 7, groupType = Hint.GroupType.Row(2))

		val result = HiddenSingleExplainer.explain(context, hint)

		assertEquals("Look at row 3!", result.instruction.annotatedString.text)
		assertTrue(result.description.annotatedString.text.contains("row 3"))
		assertTrue(result.description.annotatedString.text.contains("[4, 5]"))
		assertTrue(result.actionText.annotatedString.text.contains("[4, 5]"))
	}

	@Test
	fun `explains should use 1-based column and coordinates`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 5, col = 6, number = 3, groupType = Hint.GroupType.Column(4))

		val result = HiddenSingleExplainer.explain(context, hint)

		assertEquals("Look at column 5!", result.instruction.annotatedString.text)
		assertTrue(result.description.annotatedString.text.contains("column 5"))
		assertTrue(result.description.annotatedString.text.contains("[6, 7]"))
		assertTrue(result.actionText.annotatedString.text.contains("[6, 7]"))
	}

	@Test
	fun `explains should use 1-based block and coordinates`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 7, col = 8, number = 9, groupType = Hint.GroupType.Block(6))

		val result = HiddenSingleExplainer.explain(context, hint)

		assertEquals("Look at block 7!", result.instruction.annotatedString.text)
		assertTrue(result.description.annotatedString.text.contains("block 7"))
		assertTrue(result.description.annotatedString.text.contains("[8, 9]"))
		assertTrue(result.actionText.annotatedString.text.contains("[8, 9]"))
	}

	@Test
	fun `explains should annotate row in instruction`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 2, col = 3, number = 9, groupType = Hint.GroupType.Row(1))

		val result = HiddenSingleExplainer.explain(context, hint)
		val instruction = result.instruction.annotatedString

		assertTrue(
			instruction.containsStringAnnotation(
				tag = "type",
				value = "row",
			),
		)
	}

	@Test
	fun `explains should annotate column in instruction`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 2, col = 3, number = 9, groupType = Hint.GroupType.Column(1))

		val result = HiddenSingleExplainer.explain(context, hint)
		val instruction = result.instruction.annotatedString

		assertTrue(
			instruction.containsStringAnnotation(
				tag = "type",
				value = "column",
			),
		)
	}

	@Test
	fun `explains should annotate block in instruction`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 2, col = 3, number = 9, groupType = Hint.GroupType.Block(1))

		val result = HiddenSingleExplainer.explain(context, hint)
		val instruction = result.instruction.annotatedString

		assertTrue(
			instruction.containsStringAnnotation(
				tag = "type",
				value = "block",
			),
		)
	}

	@Test
	fun `explains should annotate digit group and coordinates in description`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 2, col = 3, number = 9, groupType = Hint.GroupType.Row(1))

		val result = HiddenSingleExplainer.explain(context, hint)
		val description = result.description.annotatedString

		assertTrue(
			description.containsStringAnnotation(
				tag = "type",
				value = "digit",
			),
		)
		assertTrue(
			description.containsStringAnnotation(
				tag = "type",
				value = "row",
			),
		)
		assertTrue(
			description.containsStringAnnotation(
				tag = "type",
				value = "cell",
			),
		)
	}

	@Test
	fun `explains should annotate coordinates and digit in action text`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 5, col = 6, number = 3, groupType = Hint.GroupType.Block(4))

		val result = HiddenSingleExplainer.explain(context, hint)
		val actionText = result.actionText.annotatedString

		assertTrue(
			actionText.containsStringAnnotation(
				tag = "type",
				value = "cell",
			),
		)
		assertTrue(
			actionText.containsStringAnnotation(
				tag = "type",
				value = "digit",
			),
		)
	}

	@Test
	fun `explains should annotate technique in hintTypeName`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.HiddenSingle(row = 5, col = 6, number = 3, groupType = Hint.GroupType.Block(4))

		val result = HiddenSingleExplainer.explain(context, hint)
		val hintTypeName = result.hintTypeName.annotatedString

		assertTrue(
			hintTypeName.containsStringAnnotation(
				tag = "type",
				value = "technique",
			),
		)
	}

	private fun AnnotatedString.containsStringAnnotation(tag: String, value: String): Boolean =
		this.getStringAnnotations(tag = tag, start = 0, end = this.length)
			.any { it.item == value }
}
