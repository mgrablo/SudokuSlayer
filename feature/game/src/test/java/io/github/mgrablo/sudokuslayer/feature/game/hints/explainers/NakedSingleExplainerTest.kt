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
class NakedSingleExplainerTest {
	@Test
	fun `explains should replace placeholders with actual values`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.NakedSingle(row = 0, col = 1, number = 5)

		val result = NakedSingleExplainer.explain(context, hint)
		val instructionText = result.instruction.annotatedString.text
		val descriptionText = result.description.annotatedString.text
		val actionText = result.actionText.annotatedString.text
		val hintTypeNameText = result.hintTypeName.annotatedString.text

		assertTrue(instructionText.contains("[1, 2]"))
		assertTrue(descriptionText.contains("5"))
		assertTrue(actionText.contains("[1, 2]"))

		assertFalse(instructionText.contains("^1"))
		assertFalse(descriptionText.contains("^1"))
		assertFalse(actionText.contains("^1"))
		assertFalse(actionText.contains("^2"))
		assertFalse(hintTypeNameText.contains("^1"))
	}

	@Test
	fun `explains should use 1-based coordinates`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.NakedSingle(row = 3, col = 4, number = 7)

		val result = NakedSingleExplainer.explain(context, hint)

		assertEquals("Inspect cell [4, 5]!", result.instruction.annotatedString.text)
		assertTrue(result.actionText.annotatedString.text.contains("[4, 5]"))
	}

	@Test
	fun `explains should annotate coordinates in instruction`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.NakedSingle(row = 2, col = 3, number = 9)

		val result = NakedSingleExplainer.explain(context, hint)
		val instruction = result.instruction.annotatedString

		assertTrue(
			instruction.containsStringAnnotation(
				tag = "type",
				value = "cell",
			),
		)
	}

	@Test
	fun `explains should annotate digit in description`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.NakedSingle(row = 2, col = 3, number = 9)

		val result = NakedSingleExplainer.explain(context, hint)
		val description = result.description.annotatedString

		assertTrue(
			description.containsStringAnnotation(
				tag = "type",
				value = "digit",
			),
		)
	}

	@Test
	fun `explains should annotate coordinates and digit in action text`() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val hint = Hint.NakedSingle(row = 5, col = 6, number = 3)

		val result = NakedSingleExplainer.explain(context, hint)
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
		val hint = Hint.NakedSingle(row = 5, col = 6, number = 3)

		val result = NakedSingleExplainer.explain(context, hint)
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
