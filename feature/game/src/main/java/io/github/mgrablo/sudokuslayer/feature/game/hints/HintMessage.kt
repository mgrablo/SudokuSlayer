package io.github.mgrablo.sudokuslayer.feature.game.hints

import android.content.Context
import android.text.Annotation
import android.text.Spanned
import android.text.SpannedString
import android.text.TextUtils
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import io.github.mgrablo.sudokuslayer.feature.game.theme.LocalHintLogsColors

@JvmInline
@Immutable
internal value class HintMessage(val annotatedString: AnnotatedString)

internal fun getHintMessage(
	context: Context,
	@StringRes id: Int,
	vararg args: Any,
): HintMessage {
	val spanned = context.resources.getText(id) as Spanned
	val textWithValues = TextUtils.expandTemplate(spanned, *args.map { it.toString() }.toTypedArray())
	val spannedText = SpannedString.valueOf(textWithValues)
	val annotated = buildAnnotatedString {
		append(spannedText.toString())

		spannedText.getSpans(0, spannedText.length, Annotation::class.java)
			.forEach { annotation ->
				addStringAnnotation(
					tag = annotation.key,
					annotation = annotation.value,
					start = spannedText.getSpanStart(annotation),
					end = spannedText.getSpanEnd(annotation),
				)
			}
	}
	return HintMessage(annotated)
}

@Composable
internal fun HintMessage.applyStyles(): AnnotatedString {
	val colors = LocalHintLogsColors.current
	return buildAnnotatedString {
		append(annotatedString)

		annotatedString.getStringAnnotations(tag = "type", start = 0, end = annotatedString.length)
			.forEach { range ->
				val style = when(range.item) {
					"digit" -> SpanStyle(color = colors.valueColor, fontWeight = FontWeight.Bold)
					"cell" -> SpanStyle(color = colors.cellCoordinateColor, fontWeight = FontWeight.Bold)
					"technique" -> SpanStyle(color = colors.techniqueNameColor, fontWeight = FontWeight.Bold)
					"row", "column", "block" -> SpanStyle(color = colors.scopeReferenceColor)
					else -> SpanStyle()
				}

				addStyle(style, range.start, range.end)
			}
	}
}
