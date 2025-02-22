package com.example.sudokuslayer.presentation.screen.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.R
import com.example.sudokuslayer.createAnnotatedString
import com.example.sudokuslayer.presentation.ui.theme.catppuccinPalette

@Composable
fun HintStepCard(
	title: String,
	cardContent: List<String>,
	onExplainClick: () -> Unit,
	onExpandToggle: () -> Unit,
	isRevealed: Boolean = false,
	isUserGuessed: Boolean = false,
	isExpanded: Boolean = false,
	interactionSource: MutableInteractionSource,
	modifier: Modifier = Modifier,
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
				containerColor = MaterialTheme.colorScheme.surface,
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
												color = MaterialTheme.catppuccinPalette.Subtext1,
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
				if (isRevealed || isUserGuessed) {
					IconButton(
						onClick = {
							onExpandToggle()
						},
					) {
						if (isExpanded) {
							Icon(
								imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
								contentDescription = "Collapse",
							)
						} else {
							Icon(
								imageVector = Icons.Default.KeyboardArrowDown,
								contentDescription = "Expand",
							)
						}
					}
				} else {
					IconButton(
						onClick = { onExplainClick() },
					) {
						Icon(
							painter = painterResource(id = R.drawable.lightbulb),
							contentDescription = "Explain",
						)
					}
				}
			}

			AnimatedVisibility(isExpanded) {
				HorizontalDivider(
					color = MaterialTheme.colorScheme.outlineVariant,
				)
				Column {
					Spacer(modifier = Modifier.height(8.dp))
					cardContent.forEach {
						Text(
							text =
								buildAnnotatedString {
									withStyle(
										SpanStyle(
											fontSize = MaterialTheme.typography.bodyMedium.fontSize,
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
														color = MaterialTheme.catppuccinPalette.Subtext1,
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
