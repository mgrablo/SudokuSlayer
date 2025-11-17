package io.github.mgrablo.sudokuslayer.feature.game.components.keypadparts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokuslayer.feature.game.R
import io.github.mgrablo.sudokuslayer.feature.game.theme.LocalKeyPadColors
import io.github.mgrablo.sudokuslayer.feature.game.theme.SudokuGameTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InputModeSwitch(checked: Boolean, onClick: (Boolean) -> Unit, modifier: Modifier = Modifier) {
	val contentDesc = if (checked) {
		stringResource(R.string.input_switch_action_to_numbers)
	} else {
		stringResource(R.string.input_switch_action_to_notes)
	}

	IconToggleButton(
		checked = checked,
		onCheckedChange = { onClick(it) },
		modifier = modifier,
		shapes = IconToggleButtonShapes(
			shape = IconButtonDefaults.standardShape,
			checkedShape = IconButtonDefaults.extraSmallSquareShape,
			pressedShape = IconButtonDefaults.largePressedShape,
		),
		colors = IconButtonDefaults.iconToggleButtonColors(
			containerColor = LocalKeyPadColors.current.numberModeSelectedBackground,
			contentColor = LocalKeyPadColors.current.numberModeSelectedOnBackground,
			checkedContainerColor = LocalKeyPadColors.current.noteModeSelectedBackground,
			checkedContentColor = LocalKeyPadColors.current.noteModeSelectedOnBackground,
			disabledContainerColor = LocalKeyPadColors.current.actionPadBackground,
			disabledContentColor = LocalKeyPadColors.current.actionPadOnBackground.copy(alpha = 0.38f),
		),
	) {
		Icon(
			painter = if (checked) {
				painterResource(
					R.drawable.stylus_note,
				)
			} else {
				painterResource(R.drawable.tag)
			},
			contentDescription = contentDesc,
			modifier = Modifier.size(IconButtonDefaults.largeIconSize),
		)
	}
}

@PreviewLightDark
@Composable
private fun InputModeSwitchPreview() {
	var checked by remember { mutableStateOf(false) }
	SudokuGameTheme {
		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			InputModeSwitch(
				onClick = { checked = !checked },
				checked = checked,
				modifier = Modifier.size(50.dp),
			)
			InputModeSwitch(
				onClick = { checked = !checked },
				checked = !checked,
				modifier = Modifier.size(50.dp),
			)
		}
	}
}
