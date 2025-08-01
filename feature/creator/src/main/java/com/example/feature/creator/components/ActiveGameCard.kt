package com.example.feature.creator.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.toLocalizedString
import com.example.sudokuslayer.feature.creator.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun ActiveGameCard(
	isExpanded: Boolean,
	difficulty: GameDifficulty,
	gridSize: SudokuGridSize,
	elapsedTime: Long,
	completed: Boolean,
	onContinueClick: () -> Unit,
	onToggle: () -> Unit,
	modifier: Modifier = Modifier,
) {
	ElevatedCard(
		onClick = onToggle,
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant,
		),
		modifier = modifier,
	) {
		SharedTransitionLayout {
			AnimatedContent(
				targetState = isExpanded,
				label = "ActiveGameCardTransition",
			) { targetState ->
				val isAnimating = transition.currentState != transition.targetState
				if (targetState) {
					ExpandedContent(
						difficulty = difficulty,
						gridSize = gridSize,
						elapsedTime = elapsedTime,
						completed = completed,
						onContinueClick = { if (!isAnimating) onContinueClick() },
						onCollapseClick = onToggle,
						modifier = Modifier,
						sharedTransitionScope = this@SharedTransitionLayout,
						animatedVisibilityScope = this@AnimatedContent,
					)
				} else {
					CollapsedContent(
						onExpandClick = onToggle,
						onContinueClick = { if (!isAnimating) onContinueClick() },
						completed = completed,
						modifier = Modifier,
						sharedTransitionScope = this@SharedTransitionLayout,
						animatedVisibilityScope = this@AnimatedContent,
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CollapsedContent(
	onExpandClick: () -> Unit,
	onContinueClick: () -> Unit,
	completed: Boolean,
	sharedTransitionScope: SharedTransitionScope,
	animatedVisibilityScope: AnimatedVisibilityScope,
	modifier: Modifier = Modifier,
) {
	val painter = if (completed) {
		painterResource(R.drawable.trophy_24px)
	} else {
		painterResource(R.drawable.play_arrow)
	}
	val title = if (completed) {
		stringResource(R.string.last_game)
	} else {
		stringResource(
			R.string.continue_playing,
		)
	}

	with(sharedTransitionScope) {
		Column(
			modifier = modifier.padding(LocalPadding.current.big),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
			) {
				with(sharedTransitionScope) {
					Text(
						text = title,
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						modifier = Modifier
							.sharedElement(
								rememberSharedContentState("title"),
								animatedVisibilityScope = animatedVisibilityScope,
							),
					)
				}
				Spacer(Modifier.weight(1f))
				FilledIconButton(
					onClick = onContinueClick,
					colors = IconButtonDefaults.filledIconButtonColors(
						containerColor = MaterialTheme.colorScheme.secondary,
					),
					modifier = Modifier
						.sharedBounds(
							rememberSharedContentState("continue_button"),
							animatedVisibilityScope = animatedVisibilityScope,
							resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
						),
				) {
					Icon(
						painter = painter,
						contentDescription = if (completed) {
							stringResource(R.string.content_desc_view_board)
						} else {
							stringResource(R.string.content_desc_continue)
						},
					)
				}
				IconButton(
					onClick = onExpandClick,
					modifier = Modifier
						.sharedBounds(
							rememberSharedContentState("toggle_button"),
							animatedVisibilityScope = animatedVisibilityScope,
						),
				) {
					Icon(
						Icons.Default.KeyboardArrowDown,
						contentDescription = stringResource(R.string.content_desc_expand),
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun ExpandedContent(
	difficulty: GameDifficulty,
	gridSize: SudokuGridSize,
	elapsedTime: Long,
	completed: Boolean,
	onContinueClick: () -> Unit,
	onCollapseClick: () -> Unit,
	sharedTransitionScope: SharedTransitionScope,
	animatedVisibilityScope: AnimatedVisibilityScope,
	modifier: Modifier = Modifier,
) {
	val formattedTime = rememberFormattedTime(elapsedTime.toFloat())
	val title =
		if (completed) stringResource(R.string.last_game) else stringResource(R.string.continue_playing)

	with(sharedTransitionScope) {
		Column(
			modifier = modifier.padding(LocalPadding.current.big),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = title,
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					modifier = Modifier
						.sharedElement(
							rememberSharedContentState("title"),
							animatedVisibilityScope = animatedVisibilityScope,
						),
				)
				Spacer(Modifier.weight(1f))
				if (completed) {
					IconButton(onClick = onContinueClick) {
						Icon(
							painter = painterResource(R.drawable.trophy_24px),
							contentDescription = stringResource(R.string.content_desc_puzzle_completed),
							tint = MaterialTheme.colorScheme.primary,
						)
					}
				}
				IconButton(
					onClick = onCollapseClick,
					modifier = Modifier
						.sharedBounds(
							rememberSharedContentState("toggle_button"),
							animatedVisibilityScope = animatedVisibilityScope,
						),
				) {
					Icon(
						Icons.Default.KeyboardArrowUp,
						contentDescription = stringResource(R.string.content_desc_collapse),
					)
				}
			}
			Spacer(Modifier.height(LocalPadding.current.small))
			DataRow(stringResource(R.string.active_difficulty), difficulty.toLocalizedString())
			DataRow(stringResource(R.string.active_size), gridSize.toLocalizedString())
			DataRow(stringResource(R.string.time), formattedTime)
			Spacer(Modifier.height(LocalPadding.current.big))
			Button(
				onClick = onContinueClick,
				shapes = ButtonShapes(
					shape = ButtonDefaults.squareShape,
					pressedShape = ButtonDefaults.shape,
				),
				modifier = Modifier
					.fillMaxWidth()
					.sharedBounds(
						rememberSharedContentState("continue_button"),
						animatedVisibilityScope = animatedVisibilityScope,
					),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.secondary,
					contentColor = MaterialTheme.colorScheme.onSecondary,
				),
			) {
				Text(
					text = if (completed) {
						stringResource(R.string.view_board)
					} else {
						stringResource(R.string.continue_button)
					},
					fontWeight = FontWeight.Medium,
				)
			}
		}
	}
}

@Composable
private fun DataRow(
	label: String,
	value: String,
	modifier: Modifier = Modifier,
	labelTextStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
		color = MaterialTheme.colorScheme.onSurfaceVariant,
	),
	valueTextStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
		fontWeight = FontWeight.Bold,
		color = MaterialTheme.colorScheme.onSurface,
	),
) {
	Row(modifier) {
		Text(label, style = labelTextStyle)
		Spacer(Modifier.weight(1f))
		Text(value, style = valueTextStyle)
	}
}

@PreviewLightDark
@Composable
private fun CollapsibleActiveGameCardPreviewExpanded() {
	SudokuSlayerTheme {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			ActiveGameCard(
				isExpanded = true,
				difficulty = GameDifficulty.Easy,
				gridSize = SudokuGridSize.NINE,
				elapsedTime = 120000L,
				completed = false,
				onContinueClick = {},
				onToggle = {},
			)
			ActiveGameCard(
				isExpanded = true,
				difficulty = GameDifficulty.Easy,
				gridSize = SudokuGridSize.NINE,
				elapsedTime = 120000L,
				completed = true,
				onContinueClick = {},
				onToggle = {},
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun CollapsibleActiveGameCardPreviewCollapsed() {
	SudokuSlayerTheme {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			ActiveGameCard(
				isExpanded = false,
				difficulty = GameDifficulty.Hard,
				gridSize = SudokuGridSize.FOUR,
				elapsedTime = 3600000L,
				completed = false,
				onContinueClick = {},
				onToggle = {},
			)
			ActiveGameCard(
				isExpanded = false,
				difficulty = GameDifficulty.Hard,
				gridSize = SudokuGridSize.FOUR,
				elapsedTime = 3600000L,
				completed = true,
				onContinueClick = {},
				onToggle = {},
			)
		}
	}
}
