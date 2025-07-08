package com.example.feature.game.components

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.feature.game.createAnnotatedString
import com.example.feature.uicore.theme.LocalHintSheetColors
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.theme.extendedColorScheme
import com.example.sudokuslayer.feature.game.R
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun HintStepCard(
	title: String,
	cardContent: PersistentList<String>,
	onExplainClick: () -> Unit,
	onExpandToggle: () -> Unit,
	onHighlightCellClick: () -> Unit,
	interactionSource: MutableInteractionSource,
	modifier: Modifier = Modifier,
	isRevealed: Boolean = false,
	isUserGuessed: Boolean = false,
	isExpanded: Boolean = false,
) {
	Card(
		modifier =
		modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
			.clickable(
				interactionSource = interactionSource,
				indication = ripple(),
				onClick = {
					if (isRevealed || isUserGuessed) {
						onExpandToggle()
					}
				},
			),
		colors =
		CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
		),
	) {
		Column(
			modifier =
			Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			Row(
				modifier = Modifier.padding(4.dp),
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text =
					buildAnnotatedString {
						withStyle(
							SpanStyle(
								color = MaterialTheme.colorScheme.onSurface,
								fontSize = MaterialTheme.typography.bodyMedium.fontSize,
							),
						) {
							append(
								createAnnotatedString(
									input = title,
									angleBracketStyle =
									SpanStyle(
										color = MaterialTheme.colorScheme.primary,
										fontWeight = FontWeight.Bold,
										fontSize = MaterialTheme.typography.bodyMedium.fontSize,
									),
									asteriskStyle =
									SpanStyle(
										color = LocalHintSheetColors.current.subtext,
										fontStyle = FontStyle.Italic,
										fontSize = MaterialTheme.typography.bodySmall.fontSize,
									),
								),
							)
						}
					},
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.weight(1f),
				)
				AnimatedContent(isUserGuessed) { guessed ->
					IconButton(
						onClick = onHighlightCellClick,
					) {
						if (guessed) {
							Icon(
								imageVector = Icons.Default.CheckCircle,
								contentDescription = stringResource(R.string.content_desc_correctly_guessed),
								tint = MaterialTheme.extendedColorScheme.peach.colorContainer,
							)
						} else {
							Icon(
								painter = painterResource(R.drawable.visibility),
								contentDescription = stringResource(R.string.content_desc_highlight_cells),
								tint = MaterialTheme.extendedColorScheme.peach.colorContainer,
							)
						}
					}
				}
				if (isRevealed || isUserGuessed) {
					IconButton(onClick = onExpandToggle) {
						if (isExpanded) {
							Icon(
								imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
								contentDescription = stringResource(R.string.content_desc_collapse),
							)
						} else {
							Icon(
								imageVector = Icons.Default.KeyboardArrowDown,
								contentDescription = stringResource(R.string.content_desc_expand),
							)
						}
					}
				} else {
					IconButton(onClick = onExplainClick) {
						Icon(
							painter = painterResource(id = R.drawable.lightbulb),
							contentDescription = stringResource(R.string.content_desc_explain_hint),
						)
					}
				}
			}
			AnimatedVisibility(isExpanded) {
				Surface(
					color = MaterialTheme.colorScheme.surface,
					shape = RoundedCornerShape(4.dp),
					modifier = Modifier.fillMaxWidth(),
				) {
					Column(
						modifier = Modifier.padding(LocalPadding.current.tiny),
					) {
						cardContent.forEach {
							Text(
								text =
								buildAnnotatedString {
									withStyle(
										SpanStyle(
											fontSize = MaterialTheme.typography.bodyMedium.fontSize,
											color = MaterialTheme.colorScheme.onSurface,
										),
									) {
										append("\u2022 ")
										append(
											createAnnotatedString(
												input = it,
												angleBracketStyle =
												SpanStyle(
													color = MaterialTheme.colorScheme.primary,
													fontWeight = FontWeight.Bold,
													fontSize = MaterialTheme.typography.bodyMedium.fontSize,
												),
												asteriskStyle =
												SpanStyle(
													color = LocalHintSheetColors.current.subtext,
													fontStyle = FontStyle.Italic,
													fontSize = MaterialTheme.typography.bodySmall.fontSize,
												),
											),
										)
									}
								},
								style = MaterialTheme.typography.bodySmall,
							)
						}
					}
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
internal fun HintStepCardPreview() {
	val interactionSource = remember { MutableInteractionSource() }
	SudokuSlayerTheme {
		Column(
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			HintStepCard(
				title = "Hidden Single in cell [3, 2]",
				cardContent = kotlinx.collections.immutable.persistentListOf(
					"The cell [3, 2] can only be <5>.",
					"Other candidates in [3, 2] are *eliminated*.",
				),
				onExplainClick = {},
				onExpandToggle = {},
				onHighlightCellClick = { },
				interactionSource = interactionSource,
				isRevealed = true,
				isExpanded = true,
			)

			HintStepCard(
				title = "Hidden Single in cell [3, 2]",
				cardContent = kotlinx.collections.immutable.persistentListOf(
					"The cell [3, 2] can only be <5>.",
					"Other candidates in [3, 2] are *eliminated*.",
				),
				onExplainClick = {},
				onExpandToggle = {},
				onHighlightCellClick = { },
				interactionSource = interactionSource,
				isRevealed = true,
				isExpanded = false,
				isUserGuessed = true,
			)
			HintStepCard(
				title = "Hidden Single in cell [3, 2]",
				cardContent = kotlinx.collections.immutable.persistentListOf(
					"The cell [3, 2] can only be <5>.",
					"Other candidates in [3, 2] are *eliminated*.",
				),
				onExplainClick = {},
				onExpandToggle = {},
				onHighlightCellClick = { },
				interactionSource = interactionSource,
				isRevealed = false,
				isExpanded = false,
				isUserGuessed = false,
			)
		}
	}
}
