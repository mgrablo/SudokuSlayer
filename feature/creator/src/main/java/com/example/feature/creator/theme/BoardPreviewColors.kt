package com.example.feature.creator.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.domain.settings.models.ColorScheme
import com.example.feature.uicore.theme.Catppuccin
import com.example.feature.uicore.theme.CatppuccinPalette

@Immutable
internal data class BoardPreviewColors(
	val background: Color,
	val frame: Color,
	val thinLine: Color,
	val thickLine: Color,
)

internal object BoardPreviewColorSchemes {
	val Mocha = createBoardColorsScheme(Catppuccin.Mocha)
	val Macchiato = createBoardColorsScheme(Catppuccin.Macchiato)
	val Frappe = createBoardColorsScheme(Catppuccin.Frappe)
	val Latte = createBoardColorsScheme(Catppuccin.Latte)

	private fun createBoardColorsScheme(palette: CatppuccinPalette) = BoardPreviewColors(
		background = palette.crust,
		frame = palette.overlay0,
		thinLine = palette.overlay1,
		thickLine = palette.overlay2,
	)
}

@Composable
internal fun rememberBoardPreviewColors(colorScheme: ColorScheme): BoardPreviewColors =
	when (colorScheme) {
		is ColorScheme.Frappe -> BoardPreviewColorSchemes.Frappe
		is ColorScheme.Latte -> BoardPreviewColorSchemes.Latte
		is ColorScheme.Macchiato -> BoardPreviewColorSchemes.Macchiato
		is ColorScheme.Mocha -> BoardPreviewColorSchemes.Mocha
	}

internal val LocalBoardPreviewColors =
	staticCompositionLocalOf<BoardPreviewColors> {
		BoardPreviewColorSchemes.Mocha
	}
