package io.github.mgrablo.sudokuslayer.feature.game.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalAppColorScheme
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme

@Composable
fun SudokuGameTheme(useSudokuSlayerTheme: Boolean = true, content: @Composable () -> Unit) {
	val themeContent = @Composable {
		val colorScheme = LocalAppColorScheme.current
		val boardAppearance = rememberBoardAppearance(colorScheme)
		val keypadColors = rememberKeypadColors(colorScheme)
		val hintLogsColors = rememberHintLogsColors(colorScheme)

		CompositionLocalProvider(
			LocalSudokuBoardAppearance provides boardAppearance,
			LocalKeyPadColors provides keypadColors,
			LocalHintLogsColors provides hintLogsColors,
		) {
			content()
		}
	}

	if (useSudokuSlayerTheme) {
		SudokuSlayerTheme {
			themeContent()
		}
	} else {
		themeContent()
	}
}
