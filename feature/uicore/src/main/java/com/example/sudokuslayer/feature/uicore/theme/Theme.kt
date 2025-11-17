package com.example.sudokuslayer.feature.uicore.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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

val MaterialTheme.extendedColorScheme: ExtendedColorScheme
	@Composable
	@ReadOnlyComposable
	get() = LocalExtendedColorScheme.current
