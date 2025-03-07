package com.example.sudokuslayer.presentation.screen.game.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudokuslayer.R
import com.example.sudokuslayer.presentation.components.ReversibleRow
import com.example.sudokuslayer.presentation.navigation.AppIcon
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.ActionPad
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.ActionPadItem
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.ActionPadOrientation
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.InputModeSwitch
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.KeyPadItem
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.NumberPad
import com.example.sudokuslayer.presentation.ui.theme.LocalKeyPadColors
import com.example.sudokuslayer.presentation.ui.theme.LocalPadding
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.sqrt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun KeyPad(
	onNumberClick: (Int) -> Unit,
	onClearClick: () -> Unit,
	onUndoClick: () -> Unit,
	onRedoClick: () -> Unit,
	onSwitchInputMode: () -> Unit,
	onHintClick: () -> Unit,
	onShowMistakesClick: () -> Unit,
	onResetClick: () -> Unit,
	noteMode: Boolean,
	gridSize: Int,
	isLeftHandMode: Boolean,
	showActionButtonsOnTop: Boolean,
	modifier: Modifier = Modifier,
	textStyle: TextStyle = TextStyle(),
) {
	val middleActionPadItems =
		getMiddleActionPadItems(
			onUndoClick = onUndoClick,
			onClearClick = onClearClick,
			onRedoClick = onRedoClick,
		)

	val itemsInRow = remember { if (gridSize > 4) sqrt(gridSize.toFloat()).toInt() + 1 else gridSize + 1 }
	val itemsInColumn = remember { itemsInRow + 1 }

	BoxWithConstraints(
		contentAlignment = Alignment.Center,
		modifier =
			modifier
				.fillMaxSize()
				.padding(LocalPadding.current.small),
	) {
		val requiredRatio = itemsInRow.toFloat() / itemsInColumn
		val currentRatio = maxWidth / maxHeight
		val itemPadding = LocalPadding.current.tiny
		val totalItemWidthPadding = itemPadding * (itemsInRow - 1)
		val totalItemHeightPadding = itemPadding * (itemsInColumn - 1)

		val itemSize =
			if (requiredRatio > currentRatio) {
				(maxWidth - totalItemWidthPadding) / itemsInRow
			} else {
				(maxHeight - totalItemHeightPadding) / itemsInColumn
			}
		val fontSize = (itemSize.value * 0.6f).sp
		val textStyle = textStyle.copy(fontSize = fontSize)

		LazyColumn(
			reverseLayout = showActionButtonsOnTop,
			verticalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			if (gridSize > 4) {
				// Keypad with 9-16 numbers
				item() {
					ReversibleRow(
						reverseLayout = isLeftHandMode,
						horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
					) {
						Column(
							verticalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
						) {
							InputModeSwitch(
								onClick = onSwitchInputMode,
								inputMode = noteMode,
								iconSize = fontSize.value.dp,
								modifier = Modifier.size(itemSize),
							)
							KeyPadItem(
								text = "",
								icon = AppIcon.ResourceIcon(R.drawable.lightbulb, "Hint"),
								onClick = onHintClick,
								textStyle = textStyle,
								modifier = Modifier.size(itemSize),
								bgColor = LocalKeyPadColors.current.actionPadBackground,
								textColor = LocalKeyPadColors.current.actionPadOnBackground,
							)
							KeyPadItem(
								text = "",
								icon = AppIcon.VectorIcon(Icons.Default.Refresh, "Restart"),
								onClick = onResetClick,
								textStyle = textStyle,
								modifier = Modifier.size(itemSize),
								bgColor = LocalKeyPadColors.current.actionPadBackground,
								textColor = LocalKeyPadColors.current.actionPadOnBackground,
							)
						}
						NumberPad(
							gridSize = gridSize,
							onButtonClick = onNumberClick,
							noteMode = noteMode,
							textStyle = textStyle,
							itemSize = itemSize,
						)
					}
				}
				item {
					ReversibleRow(
						reverseLayout = isLeftHandMode,
						horizontalArrangement = Arrangement.Center,
						modifier = Modifier.fillMaxWidth(),
					) {
						Spacer(modifier = Modifier.size(itemSize))
						ActionPad(
							items = middleActionPadItems,
							orientation = ActionPadOrientation.HORIZONTAL,
							itemSize = itemSize,
							textStyle = textStyle,
						)
					}
				}
			} else {
				// Keypad with 4 numbers
				item {
					ReversibleRow(
						reverseLayout = isLeftHandMode,
						horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
					) {
						KeyPadItem(
							text = "",
							icon = AppIcon.ResourceIcon(R.drawable.lightbulb, "Hint"),
							onClick = onHintClick,
							textStyle = textStyle,
							modifier = Modifier.size(itemSize),
							bgColor = LocalKeyPadColors.current.actionPadBackground,
							textColor = LocalKeyPadColors.current.actionPadOnBackground,
						)
						NumberPad(
							gridSize = gridSize,
							onButtonClick = onNumberClick,
							noteMode = noteMode,
							textStyle = textStyle,
							itemSize = itemSize,
						)
					}
				}
				item {
					Row(
						horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
					) {
						KeyPadItem(
							text = "",
							icon = AppIcon.VectorIcon(Icons.Default.Refresh, "Restart"),
							onClick = onResetClick,
							textStyle = textStyle,
							modifier = Modifier.size(itemSize),
							bgColor = LocalKeyPadColors.current.actionPadBackground,
							textColor = LocalKeyPadColors.current.actionPadOnBackground,
						)
						ActionPad(
							items = middleActionPadItems,
							orientation = ActionPadOrientation.HORIZONTAL,
							itemSize = itemSize,
							textStyle = textStyle,
						)
						InputModeSwitch(
							onClick = onSwitchInputMode,
							inputMode = noteMode,
							iconSize = fontSize.value.dp,
							modifier = Modifier.size(itemSize),
						)
					}
				}
			}
		}
	}
}

@Composable
private fun getMiddleActionPadItems(
	onUndoClick: () -> Unit,
	onClearClick: () -> Unit,
	onRedoClick: () -> Unit,
): PersistentList<ActionPadItem> =
	persistentListOf(
		ActionPadItem(
			icon = AppIcon.ResourceIcon(R.drawable.undo, "Undo icon"),
			onClick = onUndoClick,
			contentDescription = "Undo last move",
		),
		ActionPadItem(
			icon = AppIcon.VectorIcon(Icons.Default.Clear, "Clear icon"),
			onClick = onClearClick,
			contentDescription = "Clear cell",
		),
		ActionPadItem(
			icon = AppIcon.ResourceIcon(R.drawable.redo, "Redo icon"),
			onClick = onRedoClick,
			contentDescription = "Redo last move",
		),
	)

@PreviewLightDark
@Composable
private fun KeyPadPreview() {
	SudokuSlayerTheme {
		KeyPad(
			onNumberClick = { },
			onClearClick = { },
			onUndoClick = { },
			onRedoClick = { },
			onSwitchInputMode = { },
			onHintClick = { },
			onShowMistakesClick = { },
			onResetClick = { },
			noteMode = true,
			isLeftHandMode = true,
			showActionButtonsOnTop = true,
			gridSize = 9,
		)
	}
}

@Preview
@Composable
private fun KeyPadFourPreview() {
	SudokuSlayerTheme {
		KeyPad(
			onNumberClick = { },
			onClearClick = { },
			onUndoClick = { },
			onRedoClick = { },
			onHintClick = { },
			onSwitchInputMode = { },
			onShowMistakesClick = { },
			onResetClick = { },
			noteMode = false,
			isLeftHandMode = false,
			showActionButtonsOnTop = false,
			gridSize = 4,
		)
	}
}

@Preview
@Composable
private fun KeyPadFourLeftHandPreview() {
	SudokuSlayerTheme {
		KeyPad(
			onNumberClick = { },
			onClearClick = { },
			onUndoClick = { },
			onRedoClick = { },
			onHintClick = { },
			onSwitchInputMode = { },
			onShowMistakesClick = { },
			onResetClick = { },
			noteMode = true,
			isLeftHandMode = true,
			showActionButtonsOnTop = true,
			gridSize = 4,
		)
	}
}

@Preview
@Composable
private fun KeyPadSixteenPreview() {
	SudokuSlayerTheme {
		KeyPad(
			onNumberClick = { },
			onClearClick = { },
			onUndoClick = { },
			onRedoClick = { },
			onSwitchInputMode = { },
			onHintClick = { },
			onShowMistakesClick = { },
			onResetClick = { },
			noteMode = false,
			isLeftHandMode = false,
			showActionButtonsOnTop = false,
			gridSize = 16,
		)
	}
}

@Preview
@Composable
private fun KeyPadSixteenLeftPreview() {
	SudokuSlayerTheme {
		KeyPad(
			onNumberClick = { },
			onClearClick = { },
			onUndoClick = { },
			onRedoClick = { },
			onSwitchInputMode = { },
			onHintClick = { },
			onShowMistakesClick = { },
			onResetClick = { },
			noteMode = true,
			isLeftHandMode = true,
			showActionButtonsOnTop = true,
			gridSize = 16,
		)
	}
}
