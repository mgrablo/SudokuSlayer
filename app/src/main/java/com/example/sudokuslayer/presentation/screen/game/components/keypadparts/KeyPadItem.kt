package com.example.sudokuslayer.presentation.screen.game.components.keypadparts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme

@Composable
fun KeyPadItem(
	text: String,
	onClick: () -> Unit,
	icon: (@Composable () -> Unit)? = null,
	bgColor: Color = MaterialTheme.colorScheme.primaryContainer,
	textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier =
			modifier
				.aspectRatio(1f)
				.clip(CircleShape)
				.background(bgColor)
				.clickable(
					onClick = onClick,
				),
		contentAlignment = Alignment.Center,
	) {
		if (icon != null) {
			icon()
		} else {
			Text(
				text = text,
				color = textColor,
				style = MaterialTheme.typography.titleMedium,
				maxLines = 1,
				fontSize = LocalTextStyle.current.fontSize.times(0.8f),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun KeyboardItemNumberPreview() {
	SudokuSlayerTheme {
		KeyPadItem(
			text = "5",
			onClick = { },
		)
	}
}

@PreviewLightDark
@Composable
private fun KeyboardItemIconPreview() {
	SudokuSlayerTheme {
		KeyPadItem(
			text = "5",
			icon = {
				Icon(
					imageVector = Icons.Default.Clear,
					contentDescription = "clear",
				)
			},
			onClick = { },
		)
	}
}
