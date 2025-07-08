package com.example.feature.uicore.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.domain.settings.models.ColorScheme

data class ThemeConfiguration(
	val extendedColorScheme: ExtendedColorScheme,
	val boardColors: SudokuBoardColors,
	val keypadColors: KeypadColors,
	val hintSheetColors: HintSheetColors,
)

private fun getDarkThemeConfiguration(darkScheme: ColorScheme): ThemeConfiguration =
	when (darkScheme) {
		is ColorScheme.Mocha ->
			ThemeConfiguration(
				extendedColorScheme = extendedDark,
				boardColors = MochaSudokuBoard,
				keypadColors = MochaKeypadColors,
				hintSheetColors = MochaHintSheetColors,
			)

		else ->
			ThemeConfiguration(
				extendedColorScheme = extendedDark,
				boardColors = MacchiatoSudokuBoard,
				keypadColors = MacchiatoKeypadColors,
				hintSheetColors = MacchiatoHintSheetColors,
			)
	}

private fun getLightThemeConfiguration(lightScheme: ColorScheme): ThemeConfiguration =
	when (lightScheme) {
		is ColorScheme.Latte ->
			ThemeConfiguration(
				extendedColorScheme = extendedLight,
				boardColors = LatteSudokuBoard,
				keypadColors = LatteKeypadColors,
				hintSheetColors = LatteHintSheetColors,
			)

		else ->
			ThemeConfiguration(
				extendedColorScheme = extendedLight,
				boardColors = FrappeSudokuBoard,
				keypadColors = FrappeKeypadColors,
				hintSheetColors = FrappeHintSheetColors,
			)
	}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SudokuSlayerTheme(
	colorScheme: ColorScheme,
	content:
	@Composable () -> Unit,
) {
	val themeConfig by
		remember(colorScheme) {
			mutableStateOf(
				if (colorScheme.isDark) {
					getDarkThemeConfiguration(colorScheme)
				} else {
					getLightThemeConfiguration(colorScheme)
				},
			)
		}

	val colorScheme = remember(colorScheme) {
		when (colorScheme) {
			is ColorScheme.Mocha -> mochaColorScheme
			is ColorScheme.Macchiato -> macchiatoColorScheme
			is ColorScheme.Latte -> latteColorScheme
			is ColorScheme.Frappe -> frappeColorScheme
		}
	}

	CompositionLocalProvider(
		LocalExtendedColorScheme provides themeConfig.extendedColorScheme,
		LocalKeyPadColors provides themeConfig.keypadColors,
		LocalHintSheetColors provides themeConfig.hintSheetColors,
		LocalSudokuTypography provides AppTypography,
	) {
		MaterialExpressiveTheme(
			colorScheme = colorScheme,
		) {
			content()
		}
	}
}

@Composable
fun SudokuSlayerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	SudokuSlayerTheme(
		colorScheme = if (darkTheme) ColorScheme.Mocha() else ColorScheme.Latte(),
		content = content,
	)
}

val LocalAppColorScheme = staticCompositionLocalOf<ColorScheme> {
	ColorScheme.Mocha()
}
