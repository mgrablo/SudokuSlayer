package com.example.feature.game.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.domain.settings.models.ColorScheme
import com.example.feature.uicore.theme.Catppuccin
import com.example.feature.uicore.theme.CatppuccinPalette

@Immutable
internal data class HintLogsColors(
	val subtext: Color,
	val cellCoordinateColor: Color, // Color for cell coordinates like [1, 2]
	val valueColor: Color, // Color for Sudoku values like <5>
	val techniqueNameColor: Color, // Color for technique names like "Hidden Single"
	val scopeReferenceColor: Color, // Color for scope references like "row 3"
	val valueGroupColor: Color, // Color for groups of values like {1, 2, 3}
)

internal object HintLogsColorSchemes {
	val Mocha = createHintLogsColorsScheme(Catppuccin.Mocha)
	val Macchiato = createHintLogsColorsScheme(Catppuccin.Macchiato)
	val Frappe = createHintLogsColorsScheme(Catppuccin.Frappe)
	val Latte = createHintLogsColorsScheme(Catppuccin.Latte)

	private fun createHintLogsColorsScheme(palette: CatppuccinPalette) = HintLogsColors(
		subtext = palette.subtext1,
		cellCoordinateColor = palette.blue,
		valueColor = palette.red,
		techniqueNameColor = palette.mauve,
		scopeReferenceColor = palette.green,
		valueGroupColor = palette.peach,
	)
}

@Composable
internal fun rememberHintLogsColors(colorScheme: ColorScheme): HintLogsColors =
	remember(colorScheme) {
		when (colorScheme) {
			is ColorScheme.Mocha -> HintLogsColorSchemes.Mocha
			is ColorScheme.Macchiato -> HintLogsColorSchemes.Macchiato
			is ColorScheme.Latte -> HintLogsColorSchemes.Latte
			is ColorScheme.Frappe -> HintLogsColorSchemes.Frappe
		}
	}

internal val LocalHintLogsColors =
	staticCompositionLocalOf<HintLogsColors> { HintLogsColorSchemes.Mocha }
