package com.example.sudokuslayer.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.example.data.core.model.ColorScheme
import com.example.data.core.model.DarkMode
import com.example.data.settings.SettingsRepository
import com.shifthackz.catppuccin.compose.CatppuccinMaterial
import com.shifthackz.catppuccin.compose.CatppuccinTheme
import kotlinx.coroutines.flow.Flow

object ThemeProvider {
	private val settingsRepository by lazy { SettingsRepository() }

	fun getTheme(): Flow<DarkMode> = settingsRepository.darkMode

	fun getDarkColorScheme(): Flow<ColorScheme> = settingsRepository.darkModeColorScheme

	fun getLightColorScheme(): Flow<ColorScheme> = settingsRepository.lightModeColorScheme
}

@Composable
fun SudokuSlayerTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	lightScheme: ColorScheme = ColorScheme.Latte(),
	darkScheme: ColorScheme = ColorScheme.Mocha(),
	content:
		@Composable()
		() -> Unit,
) {
	val extendedColorScheme =
		when {
			darkTheme -> extendedDark
			else -> extendedLight
		}

	val boardColors =
		when {
			darkTheme && darkScheme is ColorScheme.Mocha -> MochaSudokuBoard
			darkTheme && darkScheme is ColorScheme.Macchiato -> MacchiatoSudokuBoard
			!darkTheme && lightScheme is ColorScheme.Frappe -> FrappeSudokuBoard
			else -> LatteSudokuBoard
		}

	val keypadColors =
		when {
			darkTheme && darkScheme is ColorScheme.Mocha -> MochaKeypadColors
			darkTheme && darkScheme is ColorScheme.Macchiato -> MacchiatoKeypadColors
			!darkTheme && lightScheme is ColorScheme.Frappe -> FrappeKeypadColors
			else -> LatteKeypadColors
		}

	val darkPalette =
		remember(darkScheme) {
			when (darkScheme) {
				is ColorScheme.Mocha -> CatppuccinMaterial.Mocha()
				is ColorScheme.Macchiato -> CatppuccinMaterial.Macchiato()
				else -> CatppuccinMaterial.Mocha()
			}
		}

	val lightPalette =
		remember(lightScheme) {
			when (lightScheme) {
				is ColorScheme.Latte -> CatppuccinMaterial.Latte()
				is ColorScheme.Frappe -> CatppuccinMaterial.Frappe()
				else -> CatppuccinMaterial.Latte()
			}
		}

	CompositionLocalProvider(LocalExtendedColorScheme provides extendedColorScheme) {
		CompositionLocalProvider(LocalSudokuBoardColors provides boardColors) {
			CompositionLocalProvider(LocalKeyPadColors provides keypadColors) {
				CatppuccinTheme.DarkLightPalette(
					darkTheme = darkTheme,
					darkPalette = darkPalette,
					lightPalette = lightPalette,
				) {
					content()
				}
			}
		}
	}
}
