package com.example.feature.game.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.feature.uicore.theme.Catppuccin
import com.example.feature.uicore.theme.CatppuccinPalette

internal data class SudokuBoardColors(
	val defaultBackground: Color,
	val onDefaultBackground: Color,
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

	private fun createBoardColorsScheme(pallete: CatppuccinPalette) = SudokuBoardColors(
		defaultBackground = pallete.base,
		onDefaultBackground = pallete.text,
		selectedBackground = pallete.surface1,
		onSelectedBackground = pallete.text,
		highlightedBackground = pallete.surface0,
		onHighlightedBackground = pallete.text,
		hintMarkBackground = pallete.green,
		onHintMarkBackground = pallete.crust,
		invalidMarkBackground = pallete.red,
		onInvalidMarkBackground = pallete.crust,
		matchingMarkBackground = pallete.lavender,
		onMatchingMarkBackground = pallete.crust,
		cellBorder = pallete.overlay0,
		blockBorder = pallete.overlay2,
	)
}

internal val LocalSudokuBoardColors =
	staticCompositionLocalOf<SudokuBoardColors> {
		BoardColorSchemes.Mocha
	}
