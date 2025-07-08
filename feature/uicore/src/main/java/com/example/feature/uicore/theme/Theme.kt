package com.example.feature.uicore.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.shifthackz.catppuccin.palette.Catppuccin

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

val LocalKeyPadColors =
	staticCompositionLocalOf<KeypadColors> {
		MochaKeypadColors
	}

val MaterialTheme.extendedColorScheme: ExtendedColorScheme
	@Composable
	@ReadOnlyComposable
	get() = LocalExtendedColorScheme.current
