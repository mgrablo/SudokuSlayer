package com.example.sudokuslayer.presentation.screen.game.components.keypadparts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.presentation.screen.game.model.InputMode
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme
import com.example.sudokuslayer.presentation.ui.theme.extendedColorScheme
import kotlin.math.sqrt

@Composable
fun NumberPad(
	gridSize: Int,
	onButtonClick: (Int) -> Unit,
	inputMode: InputMode,
	modifier: Modifier = Modifier,
) {
	val numbers = (1..gridSize).toList()
	val keyboardRows = numbers.chunked(sqrt(gridSize.toDouble()).toInt())

	val keyColor =
		when (inputMode) {
			InputMode.NUMBER -> MaterialTheme.extendedColorScheme.lavender.colorContainer
			InputMode.NOTE -> MaterialTheme.extendedColorScheme.pink.colorContainer
			InputMode.COLOR -> MaterialTheme.extendedColorScheme.rosewater.colorContainer
		}

	val textColor =
		when (inputMode) {
			InputMode.NUMBER -> MaterialTheme.extendedColorScheme.lavender.onColorContainer
			InputMode.NOTE -> MaterialTheme.extendedColorScheme.pink.onColorContainer
			InputMode.COLOR -> MaterialTheme.extendedColorScheme.rosewater.onColorContainer
		}

	Column(
		modifier =
			modifier
				.padding(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		keyboardRows.forEach { row ->
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				for (number in row) {
					KeyPadItem(
						text = number.toString(),
						onClick = { onButtonClick(number) },
						bgColor = keyColor,
						textColor = textColor,
						modifier = Modifier.weight(1f),
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun NumberPadNineItemsPreview() {
	SudokuSlayerTheme {
		NumberPad(
			onButtonClick = { },
			inputMode = InputMode.NUMBER,
			gridSize = 9,
		)
	}
}

@Preview
@Composable
private fun NumberPadFourItemsPreview() {
	SudokuSlayerTheme {
		NumberPad(
			onButtonClick = { },
			inputMode = InputMode.NUMBER,
			gridSize = 4,
		)
	}
}

@Preview
@Composable
private fun NumberPadSixteenItemsPreview() {
	SudokuSlayerTheme {
		NumberPad(
			onButtonClick = { },
			inputMode = InputMode.NUMBER,
			gridSize = 16,
		)
	}
}
