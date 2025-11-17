package com.example.sudokuslayer.feature.creator.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.sudokuslayer.feature.uicore.theme.LocalAppColorScheme
import com.example.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme

@Composable
internal fun SudokuCreatorTheme(
	useSudokuSlayerTheme: Boolean = true,
	content: @Composable () -> Unit,
) {
	val themeContent = @Composable {
		val colorScheme = LocalAppColorScheme.current
		val boardPreviewColors = rememberBoardPreviewColors(colorScheme)
		CompositionLocalProvider(
			LocalBoardPreviewColors provides boardPreviewColors,
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
