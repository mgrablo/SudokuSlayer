package com.example.sudokuslayer.feature.game.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.sudokuslayer.domain.settings.models.ColorScheme
import com.example.sudokuslayer.feature.uicore.theme.Catppuccin
import com.example.sudokuslayer.feature.uicore.theme.CatppuccinPalette

@Immutable
internal data class KeypadColors(
	val numberPadBackground: Color,
	val numberPadOnBackground: Color,
	val actionPadBackground: Color,
	val actionPadOnBackground: Color,
	val noteModeSelectedBackground: Color,
	val noteModeSelectedOnBackground: Color,
	val numberModeSelectedBackground: Color,
	val numberModeSelectedOnBackground: Color,
)

internal object KeypadColorSchemes {
	val Mocha = createKeypadColorsScheme(Catppuccin.Mocha)
	val Macchiato = createKeypadColorsScheme(Catppuccin.Macchiato)
	val Frappe = createKeypadColorsScheme(Catppuccin.Frappe)
	val Latte = createKeypadColorsScheme(Catppuccin.Latte)

	private fun createKeypadColorsScheme(palette: CatppuccinPalette) = KeypadColors(
		numberPadBackground = palette.lavender,
		numberPadOnBackground = palette.crust,
		actionPadBackground = palette.base,
		actionPadOnBackground = palette.text,
		noteModeSelectedBackground = palette.teal,
		noteModeSelectedOnBackground = palette.crust,
		numberModeSelectedBackground = palette.sky,
		numberModeSelectedOnBackground = palette.crust,
	)
}

@Composable
internal fun rememberKeypadColors(colorScheme: ColorScheme): KeypadColors = remember(colorScheme) {
	when (colorScheme) {
		is ColorScheme.Mocha -> KeypadColorSchemes.Mocha
		is ColorScheme.Macchiato -> KeypadColorSchemes.Macchiato
		is ColorScheme.Frappe -> KeypadColorSchemes.Frappe
		is ColorScheme.Latte -> KeypadColorSchemes.Latte
	}
}

internal val LocalKeyPadColors =
	staticCompositionLocalOf<KeypadColors> {
		KeypadColorSchemes.Mocha
	}
