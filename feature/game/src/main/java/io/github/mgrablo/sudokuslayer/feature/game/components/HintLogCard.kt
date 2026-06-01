package io.github.mgrablo.sudokuslayer.feature.game.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokuslayer.feature.game.R
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintExplanation
import io.github.mgrablo.sudokuslayer.feature.game.hints.HintMessage
import io.github.mgrablo.sudokuslayer.feature.game.hints.applyStyles
import io.github.mgrablo.sudokuslayer.feature.game.hints.getHintMessage
import io.github.mgrablo.sudokuslayer.feature.game.theme.SudokuGameTheme
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.extendedColorScheme

internal data class HintLogCardState(
	val hintExplanation: HintExplanation,
	val isRevealed: Boolean,
	val isUserGuessed: Boolean,
	val isExpanded: Boolean,
)

@Composable
internal fun HintLogCard(
	state: HintLogCardState,
	interactionSource: MutableInteractionSource,
	onExplainClick: () -> Unit,
	onExpandToggle: () -> Unit,
	onHighlightCellClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val (explanation, isRevealed, isUserGuessed, isExpanded) = state

	Card(
		modifier
			.fillMaxWidth()
			.clip(shape = MaterialTheme.shapes.medium)
			.clickable(
				interactionSource = interactionSource,
				indication = ripple(),
				onClick = {
					if (isRevealed || isUserGuessed) {
						onExpandToggle()
					}
				},
			),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
		),
	) {
		Column(
			Modifier
				.fillMaxWidth()
				.padding(horizontal = LocalPadding.current.normal),
		) {
			Header(
				text = explanation.instruction,
				isExpanded = isExpanded,
				isRevealed = isRevealed,
				isUserGuessed = isUserGuessed,
				onHighlightCellClick = onHighlightCellClick,
				onExpandToggle = onExpandToggle,
				onExplainClick = onExplainClick,
			)
			Content(
				explanation = explanation,
				isExpanded = isExpanded,
				onExpandToggle = onExpandToggle,
			)
		}
	}
}

@Composable
private fun Header(
	text: HintMessage,
	isExpanded: Boolean,
	isRevealed: Boolean,
	isUserGuessed: Boolean,
	onHighlightCellClick: () -> Unit,
	onExpandToggle: () -> Unit,
	onExplainClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier.padding(vertical = LocalPadding.current.tiny),
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = text.applyStyles(),
			maxLines = 1,
			modifier = Modifier.weight(1f),
		)
		Row {
			IconButton(onClick = onHighlightCellClick) {
				if (isUserGuessed) {
					Icon(
						imageVector = Icons.Filled.CheckCircle,
						contentDescription = stringResource(R.string.content_desc_correctly_guessed),
						tint = MaterialTheme.extendedColorScheme.yellow.color,
					)
				} else {
					Icon(
						painter = painterResource(id = R.drawable.visibility),
						contentDescription = stringResource(R.string.content_desc_highlight_cells),
						tint = MaterialTheme.colorScheme.secondary,
					)
				}
			}
			if (isRevealed) {
				AnimatedContent(targetState = !isExpanded) { notExpanded ->
					if (notExpanded) {
						IconButton(onClick = onExpandToggle) {
							Icon(
								imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
								contentDescription = stringResource(R.string.content_desc_explain_hint),
								tint = MaterialTheme.colorScheme.primary,
							)
						}
					}
				}
			} else {
				IconButton(onClick = onExplainClick) {
					Icon(
						painter = painterResource(id = R.drawable.lightbulb),
						contentDescription = stringResource(R.string.content_desc_explain_hint),
						tint = MaterialTheme.colorScheme.primary,
					)
				}
			}
		}
	}
}

@Composable
private fun Content(
	explanation: HintExplanation,
	isExpanded: Boolean,
	onExpandToggle: () -> Unit,
	modifier: Modifier = Modifier,
) {
	AnimatedVisibility(visible = isExpanded) {
		Column(modifier) {
			Column(
				modifier = Modifier.background(
					MaterialTheme.colorScheme.background,
					RoundedCornerShape(8.dp),
				).padding(LocalPadding.current.tiny)
					.fillMaxWidth(),
			) {
				Text(
					text = explanation.description.applyStyles(),
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.padding(bottom = 4.dp),
				)
				Text(
					text = explanation.actionText.applyStyles(),
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.padding(bottom = 4.dp),
				)
				Text(
					text = explanation.hintTypeName.applyStyles(),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
			IconButton(
				onClick = onExpandToggle,
				modifier = Modifier.align(Alignment.End),
			) {
				Icon(
					Icons.Default.KeyboardArrowDown,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun HintLogCardPreview(
	@PreviewParameter(HintLogCardStatePreviewParameterProvider::class) state: HintLogCardState,
) {
	val interactionSource = remember { MutableInteractionSource() }
	val context = LocalContext.current
	val explanation = HintExplanation(
		instruction = getHintMessage(context, R.string.hint_naked_single_instruction, "[1, 2]"),
		description = getHintMessage(context, R.string.hint_naked_single_description, 8),
		actionText = getHintMessage(context, R.string.hint_naked_single_action, 8, "[1, 2]"),
		hintTypeName = getHintMessage(context, R.string.hint_naked_single_name),
	)

	SudokuGameTheme {
		HintLogCard(
			state = state.copy(hintExplanation = explanation),
			onExplainClick = { },
			onExpandToggle = { },
			onHighlightCellClick = { },
			interactionSource = interactionSource,
			modifier = Modifier,
		)
	}
}

private class HintLogCardStatePreviewParameterProvider :
	PreviewParameterProvider<HintLogCardState> {
	private val emptyHintExplanation = HintExplanation(
		HintMessage(AnnotatedString("")),
		HintMessage(AnnotatedString("")),
		HintMessage(AnnotatedString("")),
		HintMessage(AnnotatedString("")),
	)
	private val parameters = listOf(
		HintLogCardState(
			hintExplanation = emptyHintExplanation,
			isRevealed = false,
			isUserGuessed = false,
			isExpanded = false,
		),
		HintLogCardState(
			hintExplanation = emptyHintExplanation,
			isRevealed = true,
			isUserGuessed = false,
			isExpanded = false,
		),
		HintLogCardState(
			hintExplanation = emptyHintExplanation,
			isRevealed = false,
			isUserGuessed = true,
			isExpanded = false,
		),
		HintLogCardState(
			hintExplanation = emptyHintExplanation,
			isRevealed = false,
			isUserGuessed = false,
			isExpanded = true,
		),
		HintLogCardState(
			hintExplanation = emptyHintExplanation,
			isRevealed = true,
			isUserGuessed = true,
			isExpanded = true,
		),
		HintLogCardState(
			hintExplanation = emptyHintExplanation,
			isRevealed = false,
			isUserGuessed = true,
			isExpanded = true,
		),
		HintLogCardState(
			hintExplanation = emptyHintExplanation,
			isRevealed = true,
			isUserGuessed = false,
			isExpanded = true,
		),
		HintLogCardState(
			hintExplanation = emptyHintExplanation,
			isRevealed = true,
			isUserGuessed = true,
			isExpanded = false,
		),
	)
	override val values: Sequence<HintLogCardState>
		get() = parameters.asSequence()
}
