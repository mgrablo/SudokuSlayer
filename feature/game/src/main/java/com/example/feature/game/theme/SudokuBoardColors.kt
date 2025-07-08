package com.example.feature.game.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.domain.settings.models.ColorScheme
import com.example.feature.uicore.theme.Catppuccin
import com.example.feature.uicore.theme.CatppuccinPalette

internal data class SudokuBoardColors(
	val defaultBackground: Color,
	val onDefaultBackground: Color,
	val generatedNumber: Color,
	val selectedBackground: Color,
	val onSelectedBackground: Color,
	val highlightedBackground: Color,
	val onHighlightedBackground: Color,
	val hintMarkBackground: Color,
	val onHintMarkBackground: Color,
	val invalidMarkBackground: Color,
	val onInvalidMarkBackground: Color,
	val matchingMarkBackground: Color,
	val onMatchingMarkBackground: Color,
	val cellBorder: Color,
	val blockBorder: Color,
)

internal object BoardColorSchemes {
	val Mocha = createBoardColorsScheme(Catppuccin.Mocha)
	val Macchiato = createBoardColorsScheme(Catppuccin.Macchiato)
	val Frappe = createBoardColorsScheme(Catppuccin.Frappe)
	val Latte = createBoardColorsScheme(Catppuccin.Latte)

	private fun createBoardColorsScheme(palette: CatppuccinPalette) = SudokuBoardColors(
		defaultBackground = palette.base,
		onDefaultBackground = palette.text,
		selectedBackground = palette.surface1,
		onSelectedBackground = palette.text,
		highlightedBackground = palette.surface0,
		onHighlightedBackground = palette.text,
		hintMarkBackground = palette.green,
		onHintMarkBackground = palette.crust,
		invalidMarkBackground = palette.red,
		onInvalidMarkBackground = palette.crust,
		matchingMarkBackground = palette.lavender,
		onMatchingMarkBackground = palette.crust,
		cellBorder = palette.overlay0,
		blockBorder = palette.overlay2,
		generatedNumber = palette.subtext0,
	)
}

@Composable
internal fun rememberBoardColors(colorScheme: ColorScheme): SudokuBoardColors =
	remember(colorScheme) {
		when (colorScheme) {
			is ColorScheme.Mocha -> BoardColorSchemes.Mocha
			is ColorScheme.Macchiato -> BoardColorSchemes.Macchiato
			is ColorScheme.Latte -> BoardColorSchemes.Latte
			is ColorScheme.Frappe -> BoardColorSchemes.Frappe
		}
	}

internal val LocalSudokuBoardColors =
	staticCompositionLocalOf<SudokuBoardColors> {
		BoardColorSchemes.Mocha
	}
