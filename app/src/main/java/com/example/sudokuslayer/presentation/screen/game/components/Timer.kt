package com.example.sudokuslayer.presentation.screen.game.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TimerDisplay(
	elapsedTime: () -> Long
) {
	val minutes = elapsedTime() / 60
	val seconds = elapsedTime() % 60
	Box {
		Text(
			text = String.format("%02d:%02d", minutes, seconds),
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.primary
		)
	}
}

@Preview
@Composable
private fun TimerPreview() {

	TimerDisplay { 0 }
}