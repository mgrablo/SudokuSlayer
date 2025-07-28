package com.example.feature.creator.components.preview

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize

@Stable
internal class BoardPreviewState(initialSize: SudokuGridSize, initialDifficulty: GameDifficulty) {
	private val _progress = Animatable(1f)
	val progress: Float get() = _progress.value
	val isRunning: Boolean get() = _progress.isRunning

	var previousSize by mutableStateOf(initialSize)
		private set
	var currentSize by mutableStateOf(initialSize)
		private set
	var previousDifficulty by mutableStateOf(initialDifficulty)
		private set
	var currentDifficulty by mutableStateOf(initialDifficulty)
		private set

	suspend fun animateTo(
		newSize: SudokuGridSize,
		newDifficulty: GameDifficulty,
		animationSpec: AnimationSpec<Float> = tween(
			durationMillis = 600,
			easing = EaseInOut,
		),
	) {
		val needsAnimation = (newSize != currentSize || newDifficulty != previousDifficulty)
		if (needsAnimation && !_progress.isRunning) {
			previousSize = currentSize
			currentSize = newSize
			previousDifficulty = currentDifficulty
			currentDifficulty = newDifficulty

			_progress.snapTo(0f)
			_progress.animateTo(
				targetValue = 1f,
				animationSpec = animationSpec,
			)
		} else if (needsAnimation && _progress.isRunning) {
			_progress.snapTo(1f)
			previousSize = currentSize
			currentSize = newSize
			previousDifficulty = currentDifficulty
			currentDifficulty = newDifficulty
		}
	}
}

@Composable
internal fun rememberBoardPreviewState(
	initialSize: SudokuGridSize,
	initialDifficulty: GameDifficulty,
	animationSpec: AnimationSpec<Float> = tween(
		durationMillis = 600,
		easing = EaseInOut,
	),
): BoardPreviewState {
	val state = remember { BoardPreviewState(initialSize, initialDifficulty) }
	LaunchedEffect(initialSize, initialDifficulty) {
		state.animateTo(initialSize, initialDifficulty, animationSpec)
	}

	return state
}
