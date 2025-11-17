package com.example.sudokuslayer.feature.game.components.keypadparts

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.feature.game.theme.SudokuGameTheme
import com.example.sudokuslayer.feature.uicore.theme.LocalPadding
import com.example.sudokuslayer.feature.uicore.theme.extendedColorScheme
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlin.math.sqrt

@Composable
internal fun NumberPad(
	gridSize: Int,
	remainingDigitCounts: PersistentMap<Int, Int>,
	onButtonClick: (Int) -> Unit,
	onButtonLongClick: (Int) -> Unit,
	modifier: Modifier = Modifier,
	itemSize: Dp = 48.dp,
) {
	val numbers by remember(gridSize) { derivedStateOf { (1..gridSize).toList() } }
	val keyboardRows = numbers.chunked(
		if (numbers.size >
			4
		) {
			sqrt(gridSize.toDouble()).toInt()
		} else {
			numbers.size
		},
	)

	val keyColor = MaterialTheme.extendedColorScheme.lavender.colorContainer
	val textColor = MaterialTheme.extendedColorScheme.lavender.onColorContainer

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		keyboardRows.forEach { row ->
			Row(
				horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
				verticalAlignment = Alignment.CenterVertically,
			) {
				for (number in row) {
					if (number in remainingDigitCounts) {
						KeyPadDigitItem(
							digit = number,
							remainingCount = remainingDigitCounts.getOrDefault(number, 0),
							onClick = { onButtonClick(number) },
							onLongClick = { onButtonLongClick(number) },
							containerColor = keyColor,
							contentColor = textColor,
							modifier = Modifier
								.size(itemSize),
						)
					} else {
						KeyPadTextItem(
							text = number.toString(),
							onClick = { onButtonClick(number) },
							onLongClick = { onButtonLongClick(number) },
							containerColor = keyColor,
							contentColor = textColor,
							modifier = Modifier
								.size(itemSize),
						)
					}
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun NumberPadNineItemsPreview() {
	SudokuGameTheme {
		NumberPad(
			remainingDigitCounts = persistentMapOf(1 to 1, 2 to 1, 3 to 1, 4 to 1),
			onButtonClick = { },
			onButtonLongClick = { },
			gridSize = 9,
			modifier = Modifier.padding(16.dp),
		)
	}
}

@Preview
@Composable
private fun NumberPadFourItemsPreview() {
	SudokuGameTheme {
		NumberPad(
			remainingDigitCounts = persistentMapOf(1 to 1, 2 to 1, 3 to 1, 4 to 1),
			onButtonClick = { },
			onButtonLongClick = { },
			gridSize = 4,
			modifier = Modifier.padding(16.dp),
		)
	}
}

@Preview
@Composable
private fun NumberPadSixteenItemsPreview() {
	SudokuGameTheme {
		NumberPad(
			remainingDigitCounts = persistentMapOf(1 to 1, 2 to 1, 3 to 1, 4 to 1),
			onButtonClick = { },
			onButtonLongClick = { },
			gridSize = 16,
			modifier = Modifier.padding(16.dp),
		)
	}
}
