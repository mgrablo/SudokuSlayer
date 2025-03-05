package com.example.sudokuslayer.presentation.screen.game.components.keypadparts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.presentation.ui.theme.LocalPadding
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme
import com.example.sudokuslayer.presentation.ui.theme.extendedColorScheme
import kotlin.math.sqrt

@Composable
fun NumberPad(
	gridSize: Int,
	onButtonClick: (Int) -> Unit,
	noteMode: Boolean,
	modifier: Modifier = Modifier,
	itemSize: Dp = 48.dp,
	textStyle: TextStyle = TextStyle(),
) {
	val numbers by remember {  derivedStateOf { (1..gridSize).toList() } }
	val keyboardRows = numbers.chunked(if(numbers.size > 4) sqrt(gridSize.toDouble()).toInt() else numbers.size)

	val keyColor =
		if (noteMode) {
			MaterialTheme.extendedColorScheme.pink.colorContainer
		} else {
			MaterialTheme.extendedColorScheme.lavender.colorContainer
		}

	val textColor =
		if (noteMode) {
			MaterialTheme.extendedColorScheme.pink.onColorContainer
		} else {
			MaterialTheme.extendedColorScheme.lavender.onColorContainer
		}

	Column(
		modifier =
			modifier,
		verticalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		keyboardRows.forEach { row ->
			Row(
				horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
				verticalAlignment = Alignment.CenterVertically,
			) {
				for (number in row) {
					KeyPadItem(
						text = number.toString(),
						onClick = { onButtonClick(number) },
						bgColor = keyColor,
						textColor = textColor,
						textStyle = textStyle,
						modifier = Modifier
							.size(itemSize)
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
			noteMode = false,
			gridSize = 9,
			modifier = Modifier.padding(16.dp),
		)
	}
}

@Preview
@Composable
private fun NumberPadFourItemsPreview() {
	SudokuSlayerTheme {
		NumberPad(
			onButtonClick = { },
			noteMode = false,
			gridSize = 4,
			modifier = Modifier.padding(16.dp),
		)
	}
}

@Preview
@Composable
private fun NumberPadSixteenItemsPreview() {
	SudokuSlayerTheme {
		NumberPad(
			onButtonClick = { },
			noteMode = false,
			gridSize = 16,
			modifier = Modifier.padding(16.dp),
		)
	}
}
