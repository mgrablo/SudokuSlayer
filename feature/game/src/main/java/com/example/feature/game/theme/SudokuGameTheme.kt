package com.example.feature.game.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.feature.uicore.theme.LocalAppColorScheme
import com.example.feature.uicore.theme.SudokuSlayerTheme

@Composable
fun SudokuGameTheme(useSudokuSlayerTheme: Boolean = true, content: @Composable () -> Unit) {
	val themeContent = @Composable {
		val colorScheme = LocalAppColorScheme.current
		val boardColors = rememberBoardColors(colorScheme)
		val keypadColors = rememberKeypadColors(colorScheme)
		val hintLogsColors = rememberHintLogsColors(colorScheme)

		CompositionLocalProvider(
			LocalSudokuBoardColors provides boardColors,
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
