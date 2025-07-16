package com.example.feature.game.components

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
import com.example.feature.game.components.keypadparts.ActionPad
import com.example.feature.game.components.keypadparts.ActionPadItem
import com.example.feature.game.components.keypadparts.ActionPadOrientation
import com.example.feature.game.components.keypadparts.InputModeSwitch
import com.example.feature.game.components.keypadparts.KeyPadItem
import com.example.feature.game.components.keypadparts.NumberPad
import com.example.feature.game.theme.LocalKeyPadColors
import com.example.feature.game.theme.SudokuGameTheme
import com.example.feature.uicore.components.ReversibleRow
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.theme.LocalPadding
import com.example.sudokuslayer.feature.game.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.sqrt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun KeyPad(
	onNumberClick: (Int) -> Unit,
	onNumberLongClick: (Int) -> Unit,
	onClearClick: () -> Unit,
	onUndoClick: () -> Unit,
	onRedoClick: () -> Unit,
	onSwitchInputMode: (Boolean) -> Unit,
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

	val itemsInRow = remember {
		if (gridSize >
			4
		) {
			sqrt(gridSize.toFloat()).toInt() + 1
		} else {
			gridSize + 1
		}
	}
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
				item {
					ReversibleRow(
						reverseLayout = isLeftHandMode,
						horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
					) {
						Column(
							verticalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
						) {
							InputModeSwitch(
								onClick = onSwitchInputMode,
								checked = noteMode,
								iconSize = fontSize.value.dp,
								modifier = Modifier.size(itemSize),
							)
							KeyPadItem(
								text = "",
								icon = AppIcon.ResourceIcon(R.drawable.lightbulb, "Hint"),
								onClick = onHintClick,
								textStyle = textStyle,
								modifier = Modifier.size(itemSize),
								containerColor = LocalKeyPadColors.current.actionPadBackground,
								textColor = LocalKeyPadColors.current.actionPadOnBackground,
							)
							KeyPadItem(
								text = "",
								icon = AppIcon.VectorIcon(Icons.Default.Refresh, "Restart"),
								onClick = onResetClick,
								textStyle = textStyle,
								modifier = Modifier.size(itemSize),
								containerColor = LocalKeyPadColors.current.actionPadBackground,
								textColor = LocalKeyPadColors.current.actionPadOnBackground,
							)
						}
						NumberPad(
							gridSize = gridSize,
							onButtonClick = onNumberClick,
							onButtonLongClick = onNumberLongClick,
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
							itemContainerColor = LocalKeyPadColors.current.actionPadBackground,
							itemContentColor = LocalKeyPadColors.current.actionPadOnBackground,
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
							containerColor = LocalKeyPadColors.current.actionPadBackground,
							textColor = LocalKeyPadColors.current.actionPadOnBackground,
						)
						NumberPad(
							gridSize = gridSize,
							onButtonClick = onNumberClick,
							onButtonLongClick = onNumberLongClick,
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
							containerColor = LocalKeyPadColors.current.actionPadBackground,
							textColor = LocalKeyPadColors.current.actionPadOnBackground,
						)
						ActionPad(
							items = middleActionPadItems,
							orientation = ActionPadOrientation.HORIZONTAL,
							itemSize = itemSize,
							textStyle = textStyle,
							itemContainerColor = LocalKeyPadColors.current.actionPadBackground,
							itemContentColor = LocalKeyPadColors.current.actionPadOnBackground,
						)
						InputModeSwitch(
							onClick = onSwitchInputMode,
							checked = noteMode,
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
): PersistentList<ActionPadItem> = persistentListOf(
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
	SudokuGameTheme {
		KeyPad(
			onNumberClick = { },
			onNumberLongClick = { },
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
	SudokuGameTheme {
		KeyPad(
			onNumberClick = { },
			onNumberLongClick = { },
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
	SudokuGameTheme {
		KeyPad(
			onNumberClick = { },
			onNumberLongClick = { },
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
	SudokuGameTheme {
		KeyPad(
			onNumberClick = { },
			onNumberLongClick = { },
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
	SudokuGameTheme {
		KeyPad(
			onNumberClick = { },
			onNumberLongClick = { },
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
