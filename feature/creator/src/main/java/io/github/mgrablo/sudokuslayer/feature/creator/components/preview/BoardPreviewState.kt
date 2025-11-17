package io.github.mgrablo.sudokuslayer.feature.creator.components.preview

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
import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.domain.core.toCellsToRemove
import kotlin.random.Random

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

	var previousPositions by mutableStateOf(
		generateRandomPositions(initialSize, initialDifficulty),
	)
		private set
	var currentPositions by mutableStateOf(
		generateRandomPositions(initialSize, initialDifficulty),
	)
		private set

	suspend fun animateTo(
		newSize: SudokuGridSize,
		newDifficulty: GameDifficulty,
		animationSpec: AnimationSpec<Float> = tween(
			durationMillis = 600,
			easing = EaseInOut,
		),
	) {
		val needsAnimation = (newSize != currentSize || newDifficulty != currentDifficulty)
		if (!needsAnimation) return

		if (_progress.isRunning) {
			_progress.snapTo(1f)
		}

		previousSize = currentSize
		currentSize = newSize
		previousDifficulty = currentDifficulty
		currentDifficulty = newDifficulty
		previousPositions = currentPositions
		currentPositions = generateRandomPositions(newSize, newDifficulty)

		_progress.snapTo(0f)
		_progress.animateTo(
			targetValue = 1f,
			animationSpec = animationSpec,
		)
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

private fun generateRandomPositions(
	gridSize: SudokuGridSize,
	difficulty: GameDifficulty,
	random: Random = Random.Default,
): List<Pair<Int, Int>> {
	val gridSizeInt = gridSize.toIntSize()
	val count = gridSizeInt * gridSizeInt - difficulty.toCellsToRemove(gridSize)

	if (gridSizeInt <= 0 || count <= 0) {
		return emptyList()
	}

	val allPositions = (0 until gridSizeInt).flatMap { row ->
		(0 until gridSizeInt).map { col ->
			row to col
		}
	}

	return allPositions.shuffled(random).take(count)
}
