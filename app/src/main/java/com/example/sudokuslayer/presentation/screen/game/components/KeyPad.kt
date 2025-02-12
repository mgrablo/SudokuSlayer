package com.example.sudokuslayer.presentation.screen.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.R
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.ActionPad
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.ActionPadItem
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.ActionPadOrientation
import com.example.sudokuslayer.presentation.screen.game.components.keypadparts.NumberPad
import com.example.sudokuslayer.presentation.screen.game.model.InputMode
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme
import kotlin.math.sqrt

@Composable
fun KeyPad(
	onNumberClick: (Int) -> Unit,
	onClearClick: () -> Unit,
	onUndoClick: () -> Unit,
	onRedoClick: () -> Unit,
	onNumberSwitchClick: () -> Unit,
	onNoteSwitchClick: () -> Unit,
	onColorSwitchClick: () -> Unit,
	onHintClick: () -> Unit,
	onShowMistakesClick: () -> Unit,
	onResetClick: () -> Unit,
	inputMode: InputMode,
	gridSize: Int,
	modifier: Modifier = Modifier,
) {
	val leftActionPadItems = listOf(
		ActionPadItem(
			icon = {
				Icon(
					painter = painterResource(R.drawable.lightbulb),
					contentDescription = "Show Hint"
				)
			},
			contentDescription = "Show Hint",
			onClick = onHintClick,
			backgroundColor = MaterialTheme.colorScheme.background,
			iconColor = MaterialTheme.colorScheme.onBackground
		),
		ActionPadItem(
			icon = {
				Icon(
					painter = painterResource(R.drawable.question_mark),
					contentDescription = "Show mistakes"
				)
			},
			contentDescription = "Show mistakes",
			onClick = onShowMistakesClick,
			backgroundColor = MaterialTheme.colorScheme.background,
			iconColor = MaterialTheme.colorScheme.onBackground
		),
		ActionPadItem(
			icon = {
				Icon(
					imageVector = Icons.Default.Refresh,
					contentDescription = "Restart this game"
				)
			},
			contentDescription = "Restart game",
			onClick = onResetClick,
			backgroundColor = MaterialTheme.colorScheme.background,
			iconColor = MaterialTheme.colorScheme.onBackground
		)
	)
	val middleActionPadItems = listOf(
		ActionPadItem(
			icon = { Icon(painterResource(R.drawable.undo), "Undo icon") },
			onClick = onUndoClick,
			contentDescription = "Undo last move",
			backgroundColor = MaterialTheme.colorScheme.background,
			iconColor = MaterialTheme.colorScheme.onBackground
		),
		ActionPadItem(
			icon = { Icon(Icons.Default.Clear, "Clear icon") },
			onClick = onClearClick,
			contentDescription = "Clear cell",
			backgroundColor = MaterialTheme.colorScheme.background,
			iconColor = MaterialTheme.colorScheme.onBackground

		),
		ActionPadItem(
			icon = { Icon(painterResource(R.drawable.redo), "Redo icon") },
			onClick = onRedoClick,
			contentDescription = "Redo last move",
			backgroundColor = MaterialTheme.colorScheme.background,
			iconColor = MaterialTheme.colorScheme.onBackground
		)
	)
	val rightActionPadItems = listOf(
		ActionPadItem(
			icon = { Icon(painterResource(R.drawable.tag), "Hashtag icon") },
			onClick = onNumberSwitchClick,
			contentDescription = "Switch to number input mode",
			backgroundColor = MaterialTheme.colorScheme.background,
			iconColor = MaterialTheme.colorScheme.onBackground,
		),
		ActionPadItem(
			icon = { Icon(painterResource(R.drawable.stylus_note), "Pen icon") },
			onClick = onNoteSwitchClick,
			contentDescription = "Switch to note input mode",
			backgroundColor = MaterialTheme.colorScheme.background,
			iconColor = MaterialTheme.colorScheme.onBackground
		),
		ActionPadItem(
			icon = { Icon(painterResource(R.drawable.palette), "Palette icon") },
			onClick = onColorSwitchClick,
			contentDescription = "Switch to color input mode",
			backgroundColor = MaterialTheme.colorScheme.background,
			iconColor = MaterialTheme.colorScheme.onBackground
		)
	)

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = modifier
			.heightIn(350.dp, 400.dp)
			.fillMaxWidth()
			.padding(8.dp)
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceEvenly,
			verticalAlignment = Alignment.CenterVertically,
//			modifier = Modifier.weight(maxOf(sqrt(gridSize.toDouble()).toFloat(), 3f))
		) {
			ActionPad(
				items = leftActionPadItems,
				orientation = ActionPadOrientation.VERTICAL,
				modifier = Modifier.weight(1f)
			)
			NumberPad(
				gridSize = gridSize,
				onButtonClick = onNumberClick,
				inputMode = inputMode,
				modifier = Modifier.weight(sqrt(gridSize.toDouble()).toFloat())
			)
			ActionPad(
				items = rightActionPadItems,
				orientation = ActionPadOrientation.VERTICAL,
				modifier = Modifier.weight(1f)
			)
		}
		ActionPad(
			items = middleActionPadItems,
			orientation = ActionPadOrientation.HORIZONTAL,
//			modifier = Modifier.weight(1f)
		)
	}
}

@PreviewLightDark
@Preview(showBackground = true)
@Composable
private fun KeyPadPreview() {
	SudokuSlayerTheme {
		KeyPad(
			onNumberClick = { },
			onClearClick = { },
			onUndoClick = { },
			onRedoClick = { },
			onNumberSwitchClick = { },
			onNoteSwitchClick = { },
			onColorSwitchClick = { },
			onHintClick = { },
			onShowMistakesClick = { },
			onResetClick = { },
			inputMode = InputMode.NUMBER,
			gridSize = 9,
		)
	}
}

@PreviewLightDark
@Preview(showBackground = true)
@Composable
private fun KeyPadFourPreview() {
	SudokuSlayerTheme {
		KeyPad(
			onNumberClick = { },
			onClearClick = { },
			onUndoClick = { },
			onRedoClick = { },
			onNumberSwitchClick = { },
			onNoteSwitchClick = { },
			onColorSwitchClick = { },
			onHintClick = { },
			onShowMistakesClick = { },
			onResetClick = { },
			inputMode = InputMode.NUMBER,
			gridSize = 4
		)
	}
}

@PreviewLightDark
@Preview(showBackground = true)
@Composable
private fun KeyPadSixteenPreview() {
	SudokuSlayerTheme {
		KeyPad(
			onNumberClick = { },
			onClearClick = { },
			onUndoClick = { },
			onRedoClick = { },
			onNumberSwitchClick = { },
			onNoteSwitchClick = { },
			onColorSwitchClick = { },
			onHintClick = { },
			onShowMistakesClick = { },
			onResetClick = { },
			inputMode = InputMode.NUMBER,
			gridSize = 16
		)
	}
}
