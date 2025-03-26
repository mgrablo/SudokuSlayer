package com.example.sudokuslayer.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.data.settings.AndroidSettingsRepository
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import com.shifthackz.catppuccin.compose.CatppuccinMaterial
import com.shifthackz.catppuccin.compose.CatppuccinTheme
import com.shifthackz.catppuccin.palette.CatppuccinPalette
import kotlinx.coroutines.flow.Flow

object ThemeProvider {
	private val settingsRepository by lazy { AndroidSettingsRepository() }

	fun getTheme(): Flow<DarkMode> = settingsRepository.darkMode

	fun getDarkColorScheme(): Flow<ColorScheme> = settingsRepository.darkModeColorScheme

	fun getLightColorScheme(): Flow<ColorScheme> = settingsRepository.lightModeColorScheme
}

data class ThemeConfiguration(
	val extendedColorScheme: ExtendedColorScheme,
	val boardColors: SudokuBoardColors,
	val keypadColors: KeypadColors,
	val catppuccinPalette: CatppuccinPalette,
)

private fun getDarkThemeConfiguration(darkScheme: ColorScheme): ThemeConfiguration =
	when (darkScheme) {
		is ColorScheme.Mocha ->
			ThemeConfiguration(
				extendedColorScheme = extendedDark,
				boardColors = MochaSudokuBoard,
				keypadColors = MochaKeypadColors,
				catppuccinPalette = CatppuccinMaterial.Mocha(),
			)

		else ->
			ThemeConfiguration(
				extendedColorScheme = extendedDark,
				boardColors = MacchiatoSudokuBoard,
				keypadColors = MacchiatoKeypadColors,
				catppuccinPalette = CatppuccinMaterial.Macchiato(),
			)
	}

private fun getLightThemeConfiguration(lightScheme: ColorScheme): ThemeConfiguration =
	when (lightScheme) {
		is ColorScheme.Latte ->
			ThemeConfiguration(
				extendedColorScheme = extendedLight,
				boardColors = LatteSudokuBoard,
				keypadColors = LatteKeypadColors,
				catppuccinPalette = CatppuccinMaterial.Latte(),
			)

		else ->
			ThemeConfiguration(
				extendedColorScheme = extendedLight,
				boardColors = FrappeSudokuBoard,
				keypadColors = FrappeKeypadColors,
				catppuccinPalette = CatppuccinMaterial.Frappe(),
			)
	}

@Composable
internal fun SudokuSlayerTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	lightScheme: ColorScheme = ColorScheme.Latte(),
	darkScheme: ColorScheme = ColorScheme.Mocha(),
	content:
	@Composable () -> Unit,
) {
	val themeConfig by
		remember(darkTheme, darkScheme, lightScheme) {
			mutableStateOf(
				if (darkTheme) {
					getDarkThemeConfiguration(darkScheme)
				} else {
					getLightThemeConfiguration(lightScheme)
				},
			)
		}

	CompositionLocalProvider(
		LocalExtendedColorScheme provides themeConfig.extendedColorScheme,
		LocalSudokuBoardColors provides themeConfig.boardColors,
		LocalKeyPadColors provides themeConfig.keypadColors,
	) {
		CatppuccinTheme.Palette(
			palette = themeConfig.catppuccinPalette,
		) {
			content()
		}
	}
}
