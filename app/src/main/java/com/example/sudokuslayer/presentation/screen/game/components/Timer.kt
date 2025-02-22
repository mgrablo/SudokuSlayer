package com.example.sudokuslayer.presentation.screen.game.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.delay

@Composable
fun TimerDisplay(
	elapsedTime: () -> Long,
	onPause: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var timer by rememberSaveable { mutableLongStateOf(elapsedTime()) }
	var isRunning by rememberSaveable { mutableStateOf(false) }

	LifecycleResumeEffect(Unit) {
		isRunning = true
		onPauseOrDispose {
			isRunning = false
			onPause()
		}
	}
	LaunchedEffect(isRunning) {
		if (isRunning) {
			while (true) {
				timer += 1
				delay(1000)
			}
		}
	}

	val minutes = timer / 60
	val seconds = timer % 60
	Box {
		Text(
			text = String.format("%02d:%02d", minutes, seconds),
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
		modifier = Modifier
	)
}
