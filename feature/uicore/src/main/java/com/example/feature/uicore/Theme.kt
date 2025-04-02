package com.example.feature.uicore

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.shifthackz.catppuccin.palette.Catppuccin
import com.shifthackz.catppuccin.palette.CatppuccinPalette

@Immutable
data class ExtendedColorScheme(
	val peach: ColorFamily,
	val rosewater: ColorFamily,
	val maroon: ColorFamily,
	val pink: ColorFamily,
	val teal: ColorFamily,
	val lavender: ColorFamily,
	val yellow: ColorFamily,
)

val extendedLight =
	ExtendedColorScheme(
		peach =
		ColorFamily(
			peachLight,
			onPeachLight,
			peachContainerLight,
			onPeachContainerLight,
		),
		rosewater =
		ColorFamily(
			rosewaterLight,
			onRosewaterLight,
			rosewaterContainerLight,
			onRosewaterContainerLight,
		),
		maroon =
		ColorFamily(
			maroonLight,
			onMaroonLight,
			maroonContainerLight,
			onMaroonContainerLight,
		),
		pink =
		ColorFamily(
			pinkLight,
			onPinkLight,
			pinkContainerLight,
			onPinkContainerLight,
		),
		teal =
		ColorFamily(
			tealLight,
			onTealLight,
			tealContainerLight,
			onTealContainerLight,
		),
		lavender =
		ColorFamily(
			lavenderLight,
			onLavenderLight,
			lavenderContainerLight,
			onLavenderContainerLight,
		),
		yellow =
		ColorFamily(
			yellowLight,
			onYellowLight,
			yellowContainerLight,
			onYellowContainerLight,
		),
	)

val extendedDark =
	ExtendedColorScheme(
		peach =
		ColorFamily(
			peachDark,
			onPeachDark,
			peachContainerDark,
			onPeachContainerDark,
		),
		rosewater =
		ColorFamily(
			rosewaterDark,
			onRosewaterDark,
			rosewaterContainerDark,
			onRosewaterContainerDark,
		),
		maroon =
		ColorFamily(
			maroonDark,
			onMaroonDark,
			maroonContainerDark,
			onMaroonContainerDark,
		),
		pink =
		ColorFamily(
			pinkDark,
			onPinkDark,
			pinkContainerDark,
			onPinkContainerDark,
		),
		teal =
		ColorFamily(
			tealDark,
			onTealDark,
			tealContainerDark,
			onTealContainerDark,
		),
		lavender =
		ColorFamily(
			lavenderDark,
			onLavenderDark,
			lavenderContainerDark,
			onLavenderContainerDark,
		),
		yellow =
		ColorFamily(
			yellowDark,
			onYellowDark,
			yellowContainerDark,
			onYellowContainerDark,
		),
	)

@Immutable
data class ColorFamily(
	val color: Color,
	val onColor: Color,
	val colorContainer: Color,
	val onColorContainer: Color,
)

@Immutable
data class SudokuBoardColors(
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

@Immutable
data class KeypadColors(
	val numberPadBackground: Color,
	val numberPadOnBackground: Color,
	val actionPadBackground: Color,
	val actionPadOnBackground: Color,
	val noteModeSelectedBackground: Color,
	val noteModeSelectedOnBackground: Color,
	val numberModeSelectedBackground: Color,
	val numberModeSelectedOnBackground: Color,
)

val MochaSudokuBoard: SudokuBoardColors =
	SudokuBoardColors(
		defaultBackground = Catppuccin.Mocha.Base,
		onDefaultBackground = Catppuccin.Mocha.Text,
		selectedBackground = Catppuccin.Mocha.Surface1,
		onSelectedBackground = Catppuccin.Mocha.Text,
		highlightedBackground = Catppuccin.Mocha.Surface0,
		onHighlightedBackground = Catppuccin.Mocha.Text,
		hintMarkBackground = Catppuccin.Mocha.Green,
		onHintMarkBackground = Catppuccin.Mocha.Crust,
		invalidMarkBackground = Catppuccin.Mocha.Red,
		onInvalidMarkBackground = Catppuccin.Mocha.Crust,
		matchingMarkBackground = Catppuccin.Mocha.Lavender,
		onMatchingMarkBackground = Catppuccin.Mocha.Crust,
		cellBorder = Catppuccin.Mocha.Overlay0,
		blockBorder = Catppuccin.Mocha.Overlay2,
	)

val MacchiatoSudokuBoard: SudokuBoardColors =
	SudokuBoardColors(
		defaultBackground = Catppuccin.Macchiato.Base,
		onDefaultBackground = Catppuccin.Macchiato.Text,
		selectedBackground = Catppuccin.Macchiato.Surface1,
		onSelectedBackground = Catppuccin.Macchiato.Text,
		highlightedBackground = Catppuccin.Macchiato.Surface0,
		onHighlightedBackground = Catppuccin.Macchiato.Text,
		hintMarkBackground = Catppuccin.Macchiato.Green,
		onHintMarkBackground = Catppuccin.Macchiato.Crust,
		invalidMarkBackground = Catppuccin.Macchiato.Red,
		onInvalidMarkBackground = Catppuccin.Macchiato.Crust,
		matchingMarkBackground = Catppuccin.Macchiato.Lavender,
		onMatchingMarkBackground = Catppuccin.Macchiato.Crust,
		cellBorder = Catppuccin.Macchiato.Overlay0,
		blockBorder = Catppuccin.Macchiato.Overlay2,
	)

val FrappeSudokuBoard: SudokuBoardColors =
	SudokuBoardColors(
		defaultBackground = Catppuccin.Frappe.Base,
		onDefaultBackground = Catppuccin.Frappe.Text,
		selectedBackground = Catppuccin.Frappe.Surface1,
		onSelectedBackground = Catppuccin.Frappe.Text,
		highlightedBackground = Catppuccin.Frappe.Surface0,
		onHighlightedBackground = Catppuccin.Frappe.Text,
		hintMarkBackground = Catppuccin.Frappe.Green,
		onHintMarkBackground = Catppuccin.Frappe.Crust,
		invalidMarkBackground = Catppuccin.Frappe.Red,
		onInvalidMarkBackground = Catppuccin.Frappe.Crust,
		matchingMarkBackground = Catppuccin.Frappe.Lavender,
		onMatchingMarkBackground = Catppuccin.Frappe.Crust,
		cellBorder = Catppuccin.Frappe.Overlay0,
		blockBorder = Catppuccin.Frappe.Overlay2,
	)
val LatteSudokuBoard: SudokuBoardColors =
	SudokuBoardColors(
		defaultBackground = Catppuccin.Latte.Base,
		onDefaultBackground = Catppuccin.Latte.Text,
		selectedBackground = Catppuccin.Latte.Surface1,
		onSelectedBackground = Catppuccin.Latte.Text,
		highlightedBackground = Catppuccin.Latte.Surface0,
		onHighlightedBackground = Catppuccin.Latte.Text,
		hintMarkBackground = Catppuccin.Latte.Green,
		onHintMarkBackground = Catppuccin.Latte.Crust,
		invalidMarkBackground = Catppuccin.Latte.Red,
		onInvalidMarkBackground = Catppuccin.Latte.Crust,
		matchingMarkBackground = Catppuccin.Latte.Lavender,
		onMatchingMarkBackground = Catppuccin.Latte.Crust,
		cellBorder = Catppuccin.Latte.Overlay0,
		blockBorder = Catppuccin.Latte.Overlay2,
	)

val MochaKeypadColors: KeypadColors =
	KeypadColors(
		numberPadBackground = Catppuccin.Mocha.Lavender,
		numberPadOnBackground = Catppuccin.Mocha.Crust,
		actionPadBackground = Catppuccin.Mocha.Base,
		actionPadOnBackground = Catppuccin.Mocha.Text,
		noteModeSelectedBackground = Catppuccin.Mocha.Teal,
		noteModeSelectedOnBackground = Catppuccin.Mocha.Crust,
		numberModeSelectedBackground = Catppuccin.Mocha.Sky,
		numberModeSelectedOnBackground = Catppuccin.Mocha.Crust,
	)
val MacchiatoKeypadColors: KeypadColors =
	KeypadColors(
		numberPadBackground = Catppuccin.Macchiato.Lavender,
		numberPadOnBackground = Catppuccin.Macchiato.Crust,
		actionPadBackground = Catppuccin.Macchiato.Base,
		actionPadOnBackground = Catppuccin.Macchiato.Text,
		noteModeSelectedBackground = Catppuccin.Macchiato.Teal,
		noteModeSelectedOnBackground = Catppuccin.Macchiato.Crust,
		numberModeSelectedBackground = Catppuccin.Macchiato.Sky,
		numberModeSelectedOnBackground = Catppuccin.Macchiato.Crust,
	)

val FrappeKeypadColors: KeypadColors =
	KeypadColors(
		numberPadBackground = Catppuccin.Frappe.Lavender,
		numberPadOnBackground = Catppuccin.Frappe.Crust,
		actionPadBackground = Catppuccin.Frappe.Base,
		actionPadOnBackground = Catppuccin.Frappe.Text,
		noteModeSelectedBackground = Catppuccin.Frappe.Teal,
		noteModeSelectedOnBackground = Catppuccin.Frappe.Crust,
		numberModeSelectedBackground = Catppuccin.Frappe.Sky,
		numberModeSelectedOnBackground = Catppuccin.Frappe.Crust,
	)

val LatteKeypadColors: KeypadColors =
	KeypadColors(
		numberPadBackground = Catppuccin.Latte.Lavender,
		numberPadOnBackground = Catppuccin.Latte.Crust,
		actionPadBackground = Catppuccin.Latte.Base,
		actionPadOnBackground = Catppuccin.Latte.Text,
		noteModeSelectedBackground = Catppuccin.Latte.Teal,
		noteModeSelectedOnBackground = Catppuccin.Latte.Crust,
		numberModeSelectedBackground = Catppuccin.Latte.Sky,
		numberModeSelectedOnBackground = Catppuccin.Latte.Crust,
	)

val unspecified_scheme =
	ColorFamily(
		Color.Unspecified,
		Color.Unspecified,
		Color.Unspecified,
		Color.Unspecified,
	)

internal val LocalExtendedColorScheme =
	staticCompositionLocalOf<ExtendedColorScheme> {
		extendedLight
	}

val LocalCatppuccinPalette = staticCompositionLocalOf<CatppuccinPalette> { Catppuccin.Latte }

val LocalSudokuBoardColors =
	staticCompositionLocalOf<SudokuBoardColors> {
		MochaSudokuBoard
	}

val LocalKeyPadColors =
	staticCompositionLocalOf<KeypadColors> {
		MochaKeypadColors
	}

val MaterialTheme.extendedColorScheme: ExtendedColorScheme
	@Composable
	@ReadOnlyComposable
	get() = LocalExtendedColorScheme.current

val MaterialTheme.catppuccinPalette: CatppuccinPalette
	@Composable
	@ReadOnlyComposable
	get() = LocalCatppuccinPalette.current
