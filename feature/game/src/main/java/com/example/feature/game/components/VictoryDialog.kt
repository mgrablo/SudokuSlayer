package com.example.feature.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.DialogScope
import com.composables.core.DialogState
import com.composables.core.Icon
import com.composables.core.Scrim
import com.composables.core.rememberDialogState
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.LocalSudokuTypography
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.theme.extendedColorScheme
import com.example.sudokuslayer.feature.game.R

@Composable
internal fun VictoryDialog(
	dialogState: DialogState,
	timeSpent: Long,
	difficulty: GameDifficulty,
	gridSize: SudokuGridSize,
	isNewBest: Boolean,
	onDismissRequest: () -> Unit,
	onMainMenuClick: () -> Unit,
	onShowBoardClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Dialog(
		state = dialogState,
		onDismiss = onDismissRequest,
	) {
		Scrim()
		VictoryDialogContent(
			timeSpent = timeSpent,
			difficulty = difficulty,
			gridSize = gridSize,
			isNewBest = isNewBest,
			onMainMenuClick = onMainMenuClick,
			onShowBoardClick = onShowBoardClick,
			modifier = modifier,
		)
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DialogScope.VictoryDialogContent(
	timeSpent: Long,
	difficulty: GameDifficulty,
	gridSize: SudokuGridSize,
	isNewBest: Boolean,
	onMainMenuClick: () -> Unit,
	onShowBoardClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val formattedTime = rememberFormattedTime(timeSpent.toFloat())

	Box(
		contentAlignment = Alignment.TopCenter,
		modifier = modifier.widthIn(max = 340.dp),
	) {
		Icon(
			painterResource(R.drawable.trophy_filled),
			contentDescription = "",
			tint = MaterialTheme.extendedColorScheme.peach.color,
			modifier = Modifier
				.zIndex(10f)
				.size(60.dp)
				.offset(y = (-40).dp),
		)
		DialogPanel(
			backgroundColor = MaterialTheme.colorScheme.surface,
			contentColor = MaterialTheme.colorScheme.onSurface,
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
				)
				Spacer(Modifier.height(16.dp))
				VictoryStatRow(
					label = "Time:",
					value = formattedTime,
				)
				VictoryStatRow(
					label = "Difficulty:",
					value = difficulty.name,
				)
				VictoryStatRow(
					label = "Grid Size:",
					value = gridSize.name,
				)
				Spacer(Modifier.height(24.dp))
				Row(
					horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
				) {
					Button(
						onClick = onShowBoardClick,
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.secondary,
						),
					) {
						Text(
							text = "Show Board",
							color = MaterialTheme.colorScheme.onSecondary,
						)
					}
					Button(
						onClick = onMainMenuClick,
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.primary,
						),
					) {
						Text(
							"Main Menu",
							color = MaterialTheme.colorScheme.onPrimary,
						)
					}
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
	labelStyle: TextStyle = MaterialTheme.typography.bodyLarge,
	valueStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
		fontWeight = FontWeight.Bold,
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
	SudokuSlayerTheme {
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
				isNewBest = true,
				onDismissRequest = { },
				onMainMenuClick = { },
				onShowBoardClick = { },
				modifier = Modifier,
			)
		}
	}
}
