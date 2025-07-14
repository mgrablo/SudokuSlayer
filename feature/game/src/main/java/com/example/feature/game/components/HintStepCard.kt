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
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.feature.game.theme.LocalHintLogsColors
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.theme.extendedColorScheme
import com.example.sudoku.solver.HintExplanationPart
import com.example.sudoku.solver.HintExplanationStep
import com.example.sudokuslayer.feature.game.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun HintStepCard(
	title: HintExplanationStep,
	cardContent: PersistentList<HintExplanationStep>,
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
					buildAnnotatedStringFromExplanationParts(
						title.parts,
						LocalHintLogsColors.current,
					),
					maxLines = 1,
					modifier = Modifier.weight(1f),
				)
				Row {
					IconButton(onClick = onHighlightCellClick) {
						if (isUserGuessed) {
							Icon(
								imageVector = Icons.Filled.CheckCircle,
								contentDescription = stringResource(R.string.content_desc_highlight_cells),
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
			AnimatedVisibility(visible = isExpanded) {
				Column(
					modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
				) {
					// Use the new structured hint explanation component
					StructuredHintExplanation(
						steps = cardContent,
						colors = LocalHintLogsColors.current,
						modifier = Modifier.background(
							MaterialTheme.colorScheme.background,
							RoundedCornerShape(8.dp),
						).padding(LocalPadding.current.tiny),
					)
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
				title = HintExplanationStep(
					persistentListOf(
						HintExplanationPart.Text("Hidden Single in cell "),
						HintExplanationPart.CellCoordinate(3, 2),
						HintExplanationPart.Text("."),
					),
				),
				cardContent = persistentListOf(
					HintExplanationStep(
						persistentListOf(
							HintExplanationPart.Text("The cell "),
							HintExplanationPart.CellCoordinate(3, 2),
							HintExplanationPart.Text(" can only be "),
							HintExplanationPart.Value(5),
							HintExplanationPart.Text("."),
						),
					),
				),
				onExplainClick = {},
				onExpandToggle = {},
				onHighlightCellClick = { },
				interactionSource = interactionSource,
				isRevealed = true,
				isExpanded = true,
			)

			HintStepCard(
				title = HintExplanationStep(
					persistentListOf(
						HintExplanationPart.Text("Hidden Single in cell "),
						HintExplanationPart.CellCoordinate(3, 2),
						HintExplanationPart.Text("."),
					),
				),
				cardContent = persistentListOf(
					HintExplanationStep(
						persistentListOf(
							HintExplanationPart.Text("The cell "),
							HintExplanationPart.CellCoordinate(3, 2),
							HintExplanationPart.Text(" can only be "),
							HintExplanationPart.Value(5),
							HintExplanationPart.Text("."),
						),
					),
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
				title = HintExplanationStep(
					persistentListOf(
						HintExplanationPart.Text("Hidden Single in cell "),
						HintExplanationPart.CellCoordinate(3, 2),
						HintExplanationPart.Text("."),
					),
				),
				cardContent = persistentListOf(
					HintExplanationStep(
						persistentListOf(
							HintExplanationPart.Text("The cell "),
							HintExplanationPart.CellCoordinate(3, 2),
							HintExplanationPart.Text(" can only be "),
							HintExplanationPart.Value(5),
							HintExplanationPart.Text("."),
						),
					),
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
