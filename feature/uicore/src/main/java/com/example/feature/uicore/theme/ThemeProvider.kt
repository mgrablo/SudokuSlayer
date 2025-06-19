package com.example.feature.uicore.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.domain.settings.SettingsRepository
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import com.shifthackz.catppuccin.compose.CatppuccinMaterial
import com.shifthackz.catppuccin.compose.CatppuccinTheme
import com.shifthackz.catppuccin.palette.CatppuccinPalette
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ThemeProvider : KoinComponent {
	private val settingsRepository: SettingsRepository by inject()

	fun getTheme(): Flow<DarkMode> = settingsRepository.darkMode

	fun getDarkColorScheme(): Flow<ColorScheme> = settingsRepository.darkModeColorScheme

	fun getLightColorScheme(): Flow<ColorScheme> = settingsRepository.lightModeColorScheme
}

data class ThemeConfiguration(
	val extendedColorScheme: ExtendedColorScheme,
	val boardColors: SudokuBoardColors,
	val keypadColors: KeypadColors,
	val catppuccinPalette: CatppuccinPalette,
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
				catppuccinPalette = CatppuccinMaterial.Mocha(),
			)

		else ->
			ThemeConfiguration(
				extendedColorScheme = extendedDark,
				boardColors = MacchiatoSudokuBoard,
				keypadColors = MacchiatoKeypadColors,
				hintSheetColors = MacchiatoHintSheetColors,
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
				hintSheetColors = LatteHintSheetColors,
				catppuccinPalette = CatppuccinMaterial.Latte(),
			)

		else ->
			ThemeConfiguration(
				extendedColorScheme = extendedLight,
				boardColors = FrappeSudokuBoard,
				keypadColors = FrappeKeypadColors,
				hintSheetColors = FrappeHintSheetColors,
				catppuccinPalette = CatppuccinMaterial.Frappe(),
			)
	}

@Composable
fun SudokuSlayerTheme(
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
		LocalHintSheetColors provides themeConfig.hintSheetColors,
		LocalSudokuTypography provides AppTypography,
	) {
		CatppuccinTheme.Palette(
			palette = themeConfig.catppuccinPalette,
		) {
			content()
		}
	}
}
