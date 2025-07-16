package com.example.feature.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.DialogScope
import com.composables.core.DialogState
import com.composables.core.HorizontalSeparator
import com.composables.core.Icon
import com.composables.core.Scrim
import com.composables.core.rememberDialogState
import com.composeunstyled.LocalModalWindow
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.feature.game.theme.SudokuGameTheme
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.LocalSudokuTypography
import com.example.feature.uicore.theme.extendedColorScheme
import com.example.feature.uicore.toLocalizedString
import com.example.sudokuslayer.feature.game.R
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Position
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
internal fun VictoryDialog(
	dialogState: DialogState,
	timeSpent: Long,
	difficulty: GameDifficulty,
	gridSize: SudokuGridSize,
	hintsUsed: Int,
	isNewBest: Boolean,
	bestTime: Long?,
	onDismissRequest: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Dialog(
		state = dialogState,
		onDismiss = onDismissRequest,
	) {
		val view = LocalView.current
		val window = LocalModalWindow.current
		val insertsController = WindowCompat.getInsetsController(window, view)
		if (!view.isInEditMode) {
			insertsController.apply {
				hide(WindowInsetsCompat.Type.systemBars())
				systemBarsBehavior =
					WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
			}
		}
		Scrim()
		VictoryDialogContent(
			visible = dialogState.visible,
			timeSpent = timeSpent,
			difficulty = difficulty,
			gridSize = gridSize,
			hintsUsed = hintsUsed,
			bestTime = bestTime,
			isNewBest = isNewBest,
			modifier = modifier,
		)
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DialogScope.VictoryDialogContent(
	visible: Boolean,
	timeSpent: Long,
	difficulty: GameDifficulty,
	gridSize: SudokuGridSize,
	hintsUsed: Int,
	bestTime: Long?,
	isNewBest: Boolean,
	modifier: Modifier = Modifier,
) {
	val formattedTime = rememberFormattedTime(timeSpent.toFloat())
	val localizedDifficulty = difficulty.toLocalizedString()
	val localizedGridSize = gridSize.toLocalizedString()
	var isAnimating by remember(visible) { mutableStateOf(false) }

	var trophyPositionCaptured by remember { mutableStateOf(false) }
	var trophyPosition by remember { mutableStateOf(Offset.Zero) }
	var trophyVisible by remember { mutableStateOf(false) }

	val windowInfo = LocalWindowInfo.current

	val newBestConfettiPresets = remember(trophyPosition) {
		listOf(
			ConfettiParties.parade(
				position1 = Position.Absolute(0.0f, trophyPosition.y),
				position2 = Position.Absolute(
					windowInfo.containerSize.width.toFloat(),
					trophyPosition.y,
				),
			),
			ConfettiParties.fountain(
				Position.Absolute(trophyPosition.x, trophyPosition.y),
			),
		)
	}

	val confettiPresets = listOf(
		ConfettiParties.explode(
			Position.Absolute(trophyPosition.x, trophyPosition.y),
		),
		ConfettiParties.rain(),
	)

	LaunchedEffect(visible) {
		if (visible) {
			delay(200)
			trophyVisible = true
		} else {
			trophyVisible = false
		}
	}

	LaunchedEffect(visible, trophyPositionCaptured) {
		if (visible && trophyPositionCaptured) {
			delay(300)
			isAnimating = true
		}
	}

	if (isAnimating) {
		ConfettiKit(
			modifier = Modifier
				.fillMaxSize()
				.zIndex(11f),
			parties = if (isNewBest) {
				newBestConfettiPresets[Random.nextInt(newBestConfettiPresets.size)]
			} else {
				confettiPresets[Random.nextInt(confettiPresets.size)]
			},
			onParticleSystemEnded = { _, activeSystems ->
				if (activeSystems == 0) {
					isAnimating = false
				}
			},
		)
	}

	Box(
		contentAlignment = Alignment.TopCenter,
		modifier = modifier.widthIn(max = 340.dp),
	) {
		AnimatedVisibility(
			visible = trophyVisible,
			enter = scaleIn(
				animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
				initialScale = 0.2f,
			) + slideInHorizontally(
				animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
				initialOffsetX = { -it },
			),
			exit = fadeOut(
				animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
			),
			modifier = Modifier
				.fillMaxWidth()
				.zIndex(15f)
				.offset(y = (-40).dp),
		) {
			Icon(
				painterResource(R.drawable.trophy_filled),
				contentDescription = "",
				tint = MaterialTheme.extendedColorScheme.maroon.colorContainer,
				modifier = Modifier
					.size(60.dp)
					.onGloballyPositioned {
						trophyPosition = it.positionInWindow()
						trophyPosition =
							trophyPosition.copy(x = trophyPosition.x + it.size.width / 2)
						trophyPositionCaptured = true
					},
			)
		}
		DialogPanel(
			backgroundColor = MaterialTheme.colorScheme.surface,
			contentColor = MaterialTheme.colorScheme.onSurface,
			enter = fadeIn(
				animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
			),
			exit = fadeOut(
				animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
			),
			shape = MaterialTheme.shapes.medium,
			contentPadding = PaddingValues(
				LocalPadding.current.large,
			),
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Text(
					text = "Congratulations!",
					style = LocalSudokuTypography.current.displayLargeEmphasized,
					fontSize = 32.sp,
					color = MaterialTheme.colorScheme.onSurface,
				)
				Spacer(Modifier.height(16.dp))
				VictoryStatRow(
					label = "Time:",
					value = formattedTime,
				)
				VictoryStatRow(
					label = "Difficulty:",
					value = localizedDifficulty,
				)
				VictoryStatRow(
					label = "Grid Size:",
					value = localizedGridSize,
				)
				VictoryStatRow(
					label = "Hints Used:",
					value = hintsUsed.toString(),
				)
				Spacer(Modifier.height(24.dp))
				HorizontalSeparator(
					color = MaterialTheme.colorScheme.outline,
				)
				Spacer(Modifier.height(16.dp))
				if (isNewBest || bestTime == null) {
					Text("New Best!")
				} else {
					val formattedBestTime = rememberFormattedTime(bestTime.toFloat())
					VictoryStatRow(
						label = "Best Time:",
						value = formattedBestTime,
					)
				}
			}
		}
	}
}

@Composable
private fun VictoryStatRow(
	label: String,
	value: String,
	modifier: Modifier = Modifier,
	labelStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
		color = MaterialTheme.colorScheme.onSurfaceVariant,
	),
	valueStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
		fontWeight = FontWeight.Bold,
		color = MaterialTheme.colorScheme.primary,
	),
) {
	Row(
		modifier = modifier,
	) {
		Text(
			text = label,
			style = labelStyle,
		)
		Spacer(Modifier.weight(1f))
		Text(
			text = value,
			style = valueStyle,
		)
	}
}

@Preview
@Composable
private fun VictoryDialogPreview() {
	SudokuGameTheme {
		Surface(
			color = MaterialTheme.colorScheme.background,
			modifier = Modifier.fillMaxSize(),
		) {
			val dialogState = rememberDialogState(true)
			VictoryDialog(
				dialogState = dialogState,
				timeSpent = 374,
				difficulty = GameDifficulty.Easy,
				gridSize = SudokuGridSize.NINE,
				hintsUsed = 2,
				bestTime = 100,
				isNewBest = false,
				onDismissRequest = { },
				modifier = Modifier,
			)
		}
	}
}
