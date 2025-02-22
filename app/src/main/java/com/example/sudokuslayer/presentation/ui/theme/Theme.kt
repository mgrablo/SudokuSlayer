package com.example.sudokuslayer.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.shifthackz.catppuccin.compose.CatppuccinMaterial
import com.shifthackz.catppuccin.compose.CatppuccinTheme
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

private val lightScheme =
	lightColorScheme(
		primary = primaryLight,
		onPrimary = onPrimaryLight,
		primaryContainer = primaryContainerLight,
		onPrimaryContainer = onPrimaryContainerLight,
		secondary = secondaryLight,
		onSecondary = onSecondaryLight,
		secondaryContainer = secondaryContainerLight,
		onSecondaryContainer = onSecondaryContainerLight,
		tertiary = tertiaryLight,
		onTertiary = onTertiaryLight,
		tertiaryContainer = tertiaryContainerLight,
		onTertiaryContainer = onTertiaryContainerLight,
		error = errorLight,
		onError = onErrorLight,
		errorContainer = errorContainerLight,
		onErrorContainer = onErrorContainerLight,
		background = backgroundLight,
		onBackground = onBackgroundLight,
		surface = surfaceLight,
		onSurface = onSurfaceLight,
		surfaceVariant = surfaceVariantLight,
		onSurfaceVariant = onSurfaceVariantLight,
		outline = outlineLight,
		outlineVariant = outlineVariantLight,
		scrim = scrimLight,
		inverseSurface = inverseSurfaceLight,
		inverseOnSurface = inverseOnSurfaceLight,
		inversePrimary = inversePrimaryLight,
		surfaceDim = surfaceDimLight,
		surfaceBright = surfaceBrightLight,
		surfaceContainerLowest = surfaceContainerLowestLight,
		surfaceContainerLow = surfaceContainerLowLight,
		surfaceContainer = surfaceContainerLight,
		surfaceContainerHigh = surfaceContainerHighLight,
		surfaceContainerHighest = surfaceContainerHighestLight,
	)

private val darkScheme =
	darkColorScheme(
		primary = primaryDark,
		onPrimary = onPrimaryDark,
		primaryContainer = primaryContainerDark,
		onPrimaryContainer = onPrimaryContainerDark,
		secondary = secondaryDark,
		onSecondary = onSecondaryDark,
		secondaryContainer = secondaryContainerDark,
		onSecondaryContainer = onSecondaryContainerDark,
		tertiary = tertiaryDark,
		onTertiary = onTertiaryDark,
		tertiaryContainer = tertiaryContainerDark,
		onTertiaryContainer = onTertiaryContainerDark,
		error = errorDark,
		onError = onErrorDark,
		errorContainer = errorContainerDark,
		onErrorContainer = onErrorContainerDark,
		background = backgroundDark,
		onBackground = onBackgroundDark,
		surface = surfaceDark,
		onSurface = onSurfaceDark,
		surfaceVariant = surfaceVariantDark,
		onSurfaceVariant = onSurfaceVariantDark,
		outline = outlineDark,
		outlineVariant = outlineVariantDark,
		scrim = scrimDark,
		inverseSurface = inverseSurfaceDark,
		inverseOnSurface = inverseOnSurfaceDark,
		inversePrimary = inversePrimaryDark,
		surfaceDim = surfaceDimDark,
		surfaceBright = surfaceBrightDark,
		surfaceContainerLowest = surfaceContainerLowestDark,
		surfaceContainerLow = surfaceContainerLowDark,
		surfaceContainer = surfaceContainerDark,
		surfaceContainerHigh = surfaceContainerHighDark,
		surfaceContainerHighest = surfaceContainerHighestDark,
	)

private val mediumContrastLightColorScheme =
	lightColorScheme(
		primary = primaryLightMediumContrast,
		onPrimary = onPrimaryLightMediumContrast,
		primaryContainer = primaryContainerLightMediumContrast,
		onPrimaryContainer = onPrimaryContainerLightMediumContrast,
		secondary = secondaryLightMediumContrast,
		onSecondary = onSecondaryLightMediumContrast,
		secondaryContainer = secondaryContainerLightMediumContrast,
		onSecondaryContainer = onSecondaryContainerLightMediumContrast,
		tertiary = tertiaryLightMediumContrast,
		onTertiary = onTertiaryLightMediumContrast,
		tertiaryContainer = tertiaryContainerLightMediumContrast,
		onTertiaryContainer = onTertiaryContainerLightMediumContrast,
		error = errorLightMediumContrast,
		onError = onErrorLightMediumContrast,
		errorContainer = errorContainerLightMediumContrast,
		onErrorContainer = onErrorContainerLightMediumContrast,
		background = backgroundLightMediumContrast,
		onBackground = onBackgroundLightMediumContrast,
		surface = surfaceLightMediumContrast,
		onSurface = onSurfaceLightMediumContrast,
		surfaceVariant = surfaceVariantLightMediumContrast,
		onSurfaceVariant = onSurfaceVariantLightMediumContrast,
		outline = outlineLightMediumContrast,
		outlineVariant = outlineVariantLightMediumContrast,
		scrim = scrimLightMediumContrast,
		inverseSurface = inverseSurfaceLightMediumContrast,
		inverseOnSurface = inverseOnSurfaceLightMediumContrast,
		inversePrimary = inversePrimaryLightMediumContrast,
		surfaceDim = surfaceDimLightMediumContrast,
		surfaceBright = surfaceBrightLightMediumContrast,
		surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
		surfaceContainerLow = surfaceContainerLowLightMediumContrast,
		surfaceContainer = surfaceContainerLightMediumContrast,
		surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
		surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
	)

private val highContrastLightColorScheme =
	lightColorScheme(
		primary = primaryLightHighContrast,
		onPrimary = onPrimaryLightHighContrast,
		primaryContainer = primaryContainerLightHighContrast,
		onPrimaryContainer = onPrimaryContainerLightHighContrast,
		secondary = secondaryLightHighContrast,
		onSecondary = onSecondaryLightHighContrast,
		secondaryContainer = secondaryContainerLightHighContrast,
		onSecondaryContainer = onSecondaryContainerLightHighContrast,
		tertiary = tertiaryLightHighContrast,
		onTertiary = onTertiaryLightHighContrast,
		tertiaryContainer = tertiaryContainerLightHighContrast,
		onTertiaryContainer = onTertiaryContainerLightHighContrast,
		error = errorLightHighContrast,
		onError = onErrorLightHighContrast,
		errorContainer = errorContainerLightHighContrast,
		onErrorContainer = onErrorContainerLightHighContrast,
		background = backgroundLightHighContrast,
		onBackground = onBackgroundLightHighContrast,
		surface = surfaceLightHighContrast,
		onSurface = onSurfaceLightHighContrast,
		surfaceVariant = surfaceVariantLightHighContrast,
		onSurfaceVariant = onSurfaceVariantLightHighContrast,
		outline = outlineLightHighContrast,
		outlineVariant = outlineVariantLightHighContrast,
		scrim = scrimLightHighContrast,
		inverseSurface = inverseSurfaceLightHighContrast,
		inverseOnSurface = inverseOnSurfaceLightHighContrast,
		inversePrimary = inversePrimaryLightHighContrast,
		surfaceDim = surfaceDimLightHighContrast,
		surfaceBright = surfaceBrightLightHighContrast,
		surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
		surfaceContainerLow = surfaceContainerLowLightHighContrast,
		surfaceContainer = surfaceContainerLightHighContrast,
		surfaceContainerHigh = surfaceContainerHighLightHighContrast,
		surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
	)

private val mediumContrastDarkColorScheme =
	darkColorScheme(
		primary = primaryDarkMediumContrast,
		onPrimary = onPrimaryDarkMediumContrast,
		primaryContainer = primaryContainerDarkMediumContrast,
		onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
		secondary = secondaryDarkMediumContrast,
		onSecondary = onSecondaryDarkMediumContrast,
		secondaryContainer = secondaryContainerDarkMediumContrast,
		onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
		tertiary = tertiaryDarkMediumContrast,
		onTertiary = onTertiaryDarkMediumContrast,
		tertiaryContainer = tertiaryContainerDarkMediumContrast,
		onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
		error = errorDarkMediumContrast,
		onError = onErrorDarkMediumContrast,
		errorContainer = errorContainerDarkMediumContrast,
		onErrorContainer = onErrorContainerDarkMediumContrast,
		background = backgroundDarkMediumContrast,
		onBackground = onBackgroundDarkMediumContrast,
		surface = surfaceDarkMediumContrast,
		onSurface = onSurfaceDarkMediumContrast,
		surfaceVariant = surfaceVariantDarkMediumContrast,
		onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
		outline = outlineDarkMediumContrast,
		outlineVariant = outlineVariantDarkMediumContrast,
		scrim = scrimDarkMediumContrast,
		inverseSurface = inverseSurfaceDarkMediumContrast,
		inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
		inversePrimary = inversePrimaryDarkMediumContrast,
		surfaceDim = surfaceDimDarkMediumContrast,
		surfaceBright = surfaceBrightDarkMediumContrast,
		surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
		surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
		surfaceContainer = surfaceContainerDarkMediumContrast,
		surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
		surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
	)

private val highContrastDarkColorScheme =
	darkColorScheme(
		primary = primaryDarkHighContrast,
		onPrimary = onPrimaryDarkHighContrast,
		primaryContainer = primaryContainerDarkHighContrast,
		onPrimaryContainer = onPrimaryContainerDarkHighContrast,
		secondary = secondaryDarkHighContrast,
		onSecondary = onSecondaryDarkHighContrast,
		secondaryContainer = secondaryContainerDarkHighContrast,
		onSecondaryContainer = onSecondaryContainerDarkHighContrast,
		tertiary = tertiaryDarkHighContrast,
		onTertiary = onTertiaryDarkHighContrast,
		tertiaryContainer = tertiaryContainerDarkHighContrast,
		onTertiaryContainer = onTertiaryContainerDarkHighContrast,
		error = errorDarkHighContrast,
		onError = onErrorDarkHighContrast,
		errorContainer = errorContainerDarkHighContrast,
		onErrorContainer = onErrorContainerDarkHighContrast,
		background = backgroundDarkHighContrast,
		onBackground = onBackgroundDarkHighContrast,
		surface = surfaceDarkHighContrast,
		onSurface = onSurfaceDarkHighContrast,
		surfaceVariant = surfaceVariantDarkHighContrast,
		onSurfaceVariant = onSurfaceVariantDarkHighContrast,
		outline = outlineDarkHighContrast,
		outlineVariant = outlineVariantDarkHighContrast,
		scrim = scrimDarkHighContrast,
		inverseSurface = inverseSurfaceDarkHighContrast,
		inverseOnSurface = inverseOnSurfaceDarkHighContrast,
		inversePrimary = inversePrimaryDarkHighContrast,
		surfaceDim = surfaceDimDarkHighContrast,
		surfaceBright = surfaceBrightDarkHighContrast,
		surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
		surfaceContainerLow = surfaceContainerLowDarkHighContrast,
		surfaceContainer = surfaceContainerDarkHighContrast,
		surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
		surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
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

val extendedLightMediumContrast =
	ExtendedColorScheme(
		peach =
			ColorFamily(
				peachLightMediumContrast,
				onPeachLightMediumContrast,
				peachContainerLightMediumContrast,
				onPeachContainerLightMediumContrast,
			),
		rosewater =
			ColorFamily(
				rosewaterLightMediumContrast,
				onRosewaterLightMediumContrast,
				rosewaterContainerLightMediumContrast,
				onRosewaterContainerLightMediumContrast,
			),
		maroon =
			ColorFamily(
				maroonLightMediumContrast,
				onMaroonLightMediumContrast,
				maroonContainerLightMediumContrast,
				onMaroonContainerLightMediumContrast,
			),
		pink =
			ColorFamily(
				pinkLightMediumContrast,
				onPinkLightMediumContrast,
				pinkContainerLightMediumContrast,
				onPinkContainerLightMediumContrast,
			),
		teal =
			ColorFamily(
				tealLightMediumContrast,
				onTealLightMediumContrast,
				tealContainerLightMediumContrast,
				onTealContainerLightMediumContrast,
			),
		lavender =
			ColorFamily(
				lavenderLightMediumContrast,
				onLavenderLightMediumContrast,
				lavenderContainerLightMediumContrast,
				onLavenderContainerLightMediumContrast,
			),
		yellow =
			ColorFamily(
				yellowLightMediumContrast,
				onYellowLightMediumContrast,
				yellowContainerLightMediumContrast,
				onYellowContainerLightMediumContrast,
			),
	)

val extendedLightHighContrast =
	ExtendedColorScheme(
		peach =
			ColorFamily(
				peachLightHighContrast,
				onPeachLightHighContrast,
				peachContainerLightHighContrast,
				onPeachContainerLightHighContrast,
			),
		rosewater =
			ColorFamily(
				rosewaterLightHighContrast,
				onRosewaterLightHighContrast,
				rosewaterContainerLightHighContrast,
				onRosewaterContainerLightHighContrast,
			),
		maroon =
			ColorFamily(
				maroonLightHighContrast,
				onMaroonLightHighContrast,
				maroonContainerLightHighContrast,
				onMaroonContainerLightHighContrast,
			),
		pink =
			ColorFamily(
				pinkLightHighContrast,
				onPinkLightHighContrast,
				pinkContainerLightHighContrast,
				onPinkContainerLightHighContrast,
			),
		teal =
			ColorFamily(
				tealLightHighContrast,
				onTealLightHighContrast,
				tealContainerLightHighContrast,
				onTealContainerLightHighContrast,
			),
		lavender =
			ColorFamily(
				lavenderLightHighContrast,
				onLavenderLightHighContrast,
				lavenderContainerLightHighContrast,
				onLavenderContainerLightHighContrast,
			),
		yellow =
			ColorFamily(
				yellowLightHighContrast,
				onYellowLightHighContrast,
				yellowContainerLightHighContrast,
				onYellowContainerLightHighContrast,
			),
	)

val extendedDarkMediumContrast =
	ExtendedColorScheme(
		peach =
			ColorFamily(
				peachDarkMediumContrast,
				onPeachDarkMediumContrast,
				peachContainerDarkMediumContrast,
				onPeachContainerDarkMediumContrast,
			),
		rosewater =
			ColorFamily(
				rosewaterDarkMediumContrast,
				onRosewaterDarkMediumContrast,
				rosewaterContainerDarkMediumContrast,
				onRosewaterContainerDarkMediumContrast,
			),
		maroon =
			ColorFamily(
				maroonDarkMediumContrast,
				onMaroonDarkMediumContrast,
				maroonContainerDarkMediumContrast,
				onMaroonContainerDarkMediumContrast,
			),
		pink =
			ColorFamily(
				pinkDarkMediumContrast,
				onPinkDarkMediumContrast,
				pinkContainerDarkMediumContrast,
				onPinkContainerDarkMediumContrast,
			),
		teal =
			ColorFamily(
				tealDarkMediumContrast,
				onTealDarkMediumContrast,
				tealContainerDarkMediumContrast,
				onTealContainerDarkMediumContrast,
			),
		lavender =
			ColorFamily(
				lavenderDarkMediumContrast,
				onLavenderDarkMediumContrast,
				lavenderContainerDarkMediumContrast,
				onLavenderContainerDarkMediumContrast,
			),
		yellow =
			ColorFamily(
				yellowDarkMediumContrast,
				onYellowDarkMediumContrast,
				yellowContainerDarkMediumContrast,
				onYellowContainerDarkMediumContrast,
			),
	)

val extendedDarkHighContrast =
	ExtendedColorScheme(
		peach =
			ColorFamily(
				peachDarkHighContrast,
				onPeachDarkHighContrast,
				peachContainerDarkHighContrast,
				onPeachContainerDarkHighContrast,
			),
		rosewater =
			ColorFamily(
				rosewaterDarkHighContrast,
				onRosewaterDarkHighContrast,
				rosewaterContainerDarkHighContrast,
				onRosewaterContainerDarkHighContrast,
			),
		maroon =
			ColorFamily(
				maroonDarkHighContrast,
				onMaroonDarkHighContrast,
				maroonContainerDarkHighContrast,
				onMaroonContainerDarkHighContrast,
			),
		pink =
			ColorFamily(
				pinkDarkHighContrast,
				onPinkDarkHighContrast,
				pinkContainerDarkHighContrast,
				onPinkContainerDarkHighContrast,
			),
		teal =
			ColorFamily(
				tealDarkHighContrast,
				onTealDarkHighContrast,
				tealContainerDarkHighContrast,
				onTealContainerDarkHighContrast,
			),
		lavender =
			ColorFamily(
				lavenderDarkHighContrast,
				onLavenderDarkHighContrast,
				lavenderContainerDarkHighContrast,
				onLavenderContainerDarkHighContrast,
			),
		yellow =
			ColorFamily(
				yellowDarkHighContrast,
				onYellowDarkHighContrast,
				yellowContainerDarkHighContrast,
				onYellowContainerDarkHighContrast,
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

private val LocalExtendedColorScheme =
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

@Composable
fun SudokuSlayerTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content:
		@Composable()
		() -> Unit,
) {
	val extendedColorScheme =
		when {
			darkTheme -> extendedDark
			else -> extendedLight
		}

	val boardColors =
		when {
			darkTheme -> MochaSudokuBoard
			else -> LatteSudokuBoard
		}

	val keypadColors =
		when {
			darkTheme -> MochaKeypadColors
			else -> LatteKeypadColors
		}

	CompositionLocalProvider(LocalExtendedColorScheme provides extendedColorScheme) {
		CompositionLocalProvider(LocalSudokuBoardColors provides boardColors) {
			CompositionLocalProvider(LocalKeyPadColors provides keypadColors) {
				CatppuccinTheme.DarkLightPalette(
					darkTheme = darkTheme,
					darkPalette = CatppuccinMaterial.Mocha(),
					lightPalette = CatppuccinMaterial.Latte(),
				) {
					content()
				}
			}
		}
	}
}
