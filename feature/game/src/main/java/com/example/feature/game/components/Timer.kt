package com.example.feature.game.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun TimerDisplay(elapsedTime: () -> Long, onPause: () -> Unit, modifier: Modifier = Modifier) {
	LifecycleResumeEffect(Unit) {
		onPauseOrDispose {
			onPause()
		}
	}

	val string = elapsedTime().seconds.toString()
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
		elapsedTime = { 61L },
		onPause = { },
		modifier = Modifier,
	)
}
