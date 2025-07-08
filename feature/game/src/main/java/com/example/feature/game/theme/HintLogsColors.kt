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
internal data class HintLogsColors(val subtext: Color)

internal object HintLogsColorSchemes {
	val Mocha = createHintLogsColorsScheme(Catppuccin.Mocha)
	val Macchiato = createHintLogsColorsScheme(Catppuccin.Macchiato)
	val Frappe = createHintLogsColorsScheme(Catppuccin.Frappe)
	val Latte = createHintLogsColorsScheme(Catppuccin.Latte)

	private fun createHintLogsColorsScheme(palette: CatppuccinPalette) = HintLogsColors(
		subtext = palette.subtext1,
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
