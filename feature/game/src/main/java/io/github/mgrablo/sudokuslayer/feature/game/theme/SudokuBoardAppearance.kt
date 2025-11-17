package io.github.mgrablo.sudokuslayer.feature.game.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokuslayer.domain.settings.models.ColorScheme

@Immutable
internal data class SudokuBoardAppearance(
	val colors: SudokuBoardColors,
	val cornerRadius: Dp = 8.dp,
	val thinLineWidth: Dp = 1.dp,
	val thickLineWidth: Dp = 2.dp,
)

internal val defaultSudokuBoardAppearance = SudokuBoardAppearance(
	colors = BoardColorSchemes.Mocha,
)

@Composable
internal fun rememberBoardAppearance(colorScheme: ColorScheme): SudokuBoardAppearance {
	val boardColors = rememberBoardColors(colorScheme)

	return remember(boardColors) {
		SudokuBoardAppearance(
			colors = boardColors,
		)
	}
}

internal val LocalSudokuBoardAppearance = staticCompositionLocalOf { defaultSudokuBoardAppearance }
