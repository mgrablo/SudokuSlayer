package com.example.feature.creator.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.creator.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun NewGameButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
	Button(
		onClick = onClick,
		modifier = modifier
			.height(50.dp),
		shapes = ButtonShapes(
			shape = ButtonDefaults.squareShape,
			pressedShape = ButtonDefaults.shape,
		),
		elevation = ButtonDefaults.buttonElevation(
			defaultElevation = 6.dp,
		),
		colors = ButtonDefaults.buttonColors(
			containerColor = MaterialTheme.colorScheme.primaryContainer,
		),
	) {
		Box(
			modifier = Modifier.weight(1f),
			contentAlignment = Alignment.CenterEnd,
		) {
			Icon(
				Icons.Default.Add,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onPrimaryContainer,
			)
		}
		Spacer(Modifier.size(ButtonDefaults.IconSpacing))
		Text(
			text = stringResource(R.string.new_game_button),
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
		)
		Spacer(Modifier.weight(1f))
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLightDark
@Composable
internal fun NewGameButtonPreview() {
	SudokuSlayerTheme {
		NewGameButton(onClick = {})
	}
}
