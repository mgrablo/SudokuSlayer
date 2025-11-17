package com.example.sudokuslayer.feature.game.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun TimerDisplay(elapsedTime: Long, modifier: Modifier = Modifier) {
	val string = elapsedTime.seconds.toString()
	Box(modifier = modifier) {
		Text(
			text = string,
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.primary,
		)
	}
}

@Preview
@Composable
private fun TimerPreview() {
	TimerDisplay(
		elapsedTime = 61L,
		modifier = Modifier,
	)
}
