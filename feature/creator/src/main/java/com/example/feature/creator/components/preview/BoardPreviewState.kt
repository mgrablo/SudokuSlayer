package com.example.feature.creator.components.preview

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.domain.core.SudokuGridSize

@Stable
internal class BoardPreviewState(initialSize: SudokuGridSize) {
	private val _progress = Animatable(1f)
	val progress: Float get() = _progress.value

	var previousSize by mutableStateOf(initialSize)
		private set
	var currentSize by mutableStateOf(initialSize)
		private set

	suspend fun animateTo(newSize: SudokuGridSize) {
		if (newSize != currentSize && !_progress.isRunning) {
			previousSize = currentSize
			currentSize = newSize

			_progress.snapTo(0f)
			_progress.animateTo(
				targetValue = 1f,
				animationSpec = tween(durationMillis = 600, easing = EaseInOut),
			)
		} else if (newSize != currentSize && _progress.isRunning) {
			_progress.snapTo(1f)
			previousSize = currentSize
			currentSize = newSize
		}
	}
}

@Composable
internal fun rememberBoardPreviewState(initialSize: SudokuGridSize) = remember(initialSize) {
	BoardPreviewState(initialSize)
}
