package com.example.sudokuslayer.presentation.screen.game.components.keypadparts

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.R
import com.example.sudokuslayer.presentation.ui.theme.LocalKeyPadColors
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme

@Composable
fun InputModeSwitch(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	iconSize: Dp = 40.dp,
	inputMode: Boolean = false,
) {
	IconToggleButton(
		checked = inputMode,
		onCheckedChange = { onClick() },
		modifier = modifier,
		colors = IconButtonDefaults.iconToggleButtonColors(
			containerColor = LocalKeyPadColors.current.actionPadBackground,
			checkedContainerColor = LocalKeyPadColors.current.actionPadBackground,
		),
	) {
		val transition = updateTransition(inputMode)
		val tint by transition.animateColor(
			label = "tint",
		) {
			if (it) {
				LocalKeyPadColors.current.noteModeSelectedBackground
			} else {
				LocalKeyPadColors.current.numberModeSelectedBackground
			}
		}

		Icon(
			painter = if (inputMode) {
				painterResource(
					R.drawable.stylus_note,
				)
			} else {
				painterResource(R.drawable.tag)
			},
			contentDescription = "Input mode switch",
			modifier = Modifier.size(iconSize),
			tint = tint,
		)
	}
}

@PreviewLightDark
@Composable
private fun InputModeSwitchPreview() {
	SudokuSlayerTheme {
		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			InputModeSwitch(
				onClick = { },
				inputMode = false,
				iconSize = 30.dp,
				modifier = Modifier.size(50.dp),
			)
			InputModeSwitch(
				onClick = { },
				inputMode = true,
				iconSize = 30.dp,
				modifier = Modifier.size(50.dp),
			)
		}
	}
}
