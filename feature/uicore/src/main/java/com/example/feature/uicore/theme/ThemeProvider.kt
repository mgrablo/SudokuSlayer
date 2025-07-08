package com.example.feature.uicore.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.domain.settings.SettingsRepository
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
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

	val colorScheme = remember(darkTheme, darkScheme, lightScheme) {
		if (darkTheme) {
			when (darkScheme) {
				is ColorScheme.Macchiato -> macchiatoColorScheme
				else -> mochaColorScheme
			}
		} else {
			when (lightScheme) {
				is ColorScheme.Frappe -> frappeColorScheme
				else -> latteColorScheme
			}
		}
	}

	CompositionLocalProvider(
		LocalExtendedColorScheme provides themeConfig.extendedColorScheme,
		LocalSudokuBoardColors provides themeConfig.boardColors,
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
