package io.github.mgrablo.sudokuslayer.feature.game.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokucore.hints.HintExplanationPart
import io.github.mgrablo.sudokucore.hints.HintExplanationStep
import io.github.mgrablo.sudokucore.hints.ScopeType
import io.github.mgrablo.sudokuslayer.feature.game.theme.HintLogsColors
import kotlinx.collections.immutable.PersistentList

/**
 * Converts a HintExplanationStep to an AnnotatedString with proper styling for each part.
 * This function can be reused across different components that need to display structured hint explanations.
 */
internal fun buildAnnotatedStringFromExplanationParts(
	parts: List<HintExplanationPart>,
	colors: HintLogsColors,
): AnnotatedString = buildAnnotatedString {
	parts.forEach { part ->
		when (part) {
			is HintExplanationPart.Text -> {
				append(part.content)
			}

			is HintExplanationPart.CellCoordinate -> {
				withStyle(
					SpanStyle(
						color = colors.cellCoordinateColor,
						fontWeight = FontWeight.Bold,
					),
				) {
					append("[${part.row}, ${part.col}]")
				}
			}

			is HintExplanationPart.CellCoordinatesGroup -> {
				withStyle(
					SpanStyle(
						color = colors.cellCoordinateColor,
						fontWeight = FontWeight.Bold,
					),
				) {
					part.cells.forEachIndexed { index, (row, col) ->
						if (index > 0) append(", ")
						append("[$row, $col]")
					}
				}
			}

			is HintExplanationPart.Value -> {
				withStyle(
					SpanStyle(
						color = colors.valueColor,
						fontWeight = FontWeight.Bold,
					),
				) {
					append("${part.value}")
				}
			}

			is HintExplanationPart.TechniqueName -> {
				withStyle(
					SpanStyle(
						color = colors.techniqueNameColor,
						fontWeight = FontWeight.Bold,
					),
				) {
					append(part.name)
				}
			}

			is HintExplanationPart.ScopeReference -> {
				withStyle(
					SpanStyle(
						color = colors.scopeReferenceColor,
						fontWeight = FontWeight.Medium,
					),
				) {
					val scopeType = when (part.type) {
						ScopeType.ROW -> "row"
						ScopeType.COLUMN -> "column"
						ScopeType.BLOCK -> "block"
						ScopeType.BLOCK_PART -> "block part"
					}
					if (part.index != null) {
						append("$scopeType ${part.index}")
					} else {
						append(scopeType)
					}
				}
			}

			is HintExplanationPart.ValueGroup -> {
				withStyle(
					SpanStyle(
						color = colors.valueGroupColor,
						fontWeight = FontWeight.Bold,
					),
				) {
					append("{${part.values.joinToString(", ")}}")
				}
			}
		}
	}
}

/**
 * A component that displays structured hint explanations.
 * This component handles the rendering of the different parts of a hint explanation,
 * including styling and localization.
 */
@Composable
internal fun StructuredHintExplanation(
	steps: PersistentList<HintExplanationStep>,
	colors: HintLogsColors,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
	) {
		steps.forEach { step ->
			val annotatedText = buildAnnotatedStringFromExplanationParts(step.parts, colors)

			Text(
				text = annotatedText,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(vertical = 4.dp),
			)
		}
	}
}
