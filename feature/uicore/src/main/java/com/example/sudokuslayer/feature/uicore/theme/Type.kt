package com.example.sudokuslayer.feature.uicore.theme

import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.example.sudokuslayer.feature.uicore.R

val default = FontFamily(
	Font(
		R.font.roboto_flex,
	),
)

object DisplayLargeEmphasizedVFConfig {
	const val WEIGHT = 950
	const val WIDTH = 30f
	const val SLANT = -6f
}

object DisplayMediumEmphasizedVFConfig {
	const val WEIGHT = 500
	const val WIDTH = 20f
}

@OptIn(ExperimentalTextApi::class)
val displayLargeEmphasizedFontFamily: FontFamily = if (Build.VERSION.SDK_INT >=
	Build.VERSION_CODES.O
) {
	FontFamily(
		Font(
			R.font.roboto_flex_variable,
			variationSettings = FontVariation.Settings(
				FontVariation.weight(DisplayLargeEmphasizedVFConfig.WEIGHT),
				FontVariation.width(DisplayLargeEmphasizedVFConfig.WIDTH),
				FontVariation.slant(DisplayLargeEmphasizedVFConfig.SLANT),
				FontVariation.italic(1.0f),
			),
		),
	)
} else {
	default
}

@OptIn(ExperimentalTextApi::class)
val displayMediumEmphasizedFontFamily: FontFamily = if (Build.VERSION.SDK_INT >=
	Build.VERSION_CODES.O
) {
	FontFamily(
		Font(
			R.font.roboto_serif_variable,
			variationSettings = FontVariation.Settings(
				FontVariation.weight(DisplayMediumEmphasizedVFConfig.WEIGHT),
				FontVariation.width(DisplayMediumEmphasizedVFConfig.WIDTH),
			),
			weight = FontWeight.Normal,
		),
	)
} else {
	default
}

@OptIn(ExperimentalTextApi::class)
val displayLargeFontFamily: FontFamily = if (Build.VERSION.SDK_INT >=
	Build.VERSION_CODES.O
) {
	FontFamily(
		Font(
			R.font.roboto_flex_variable,
			variationSettings = FontVariation.Settings(
				FontVariation.weight(700),
				FontVariation.width(20f),
			),
			weight = FontWeight.Normal,
		),
	)
} else {
	default
}

// Set of Material typography styles to start with
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val AppTypography =
	Typography(
		displayLargeEmphasized = TextStyle(
			fontFamily = displayLargeEmphasizedFontFamily,
		),
		displayMediumEmphasized = TextStyle(
			fontFamily = displayMediumEmphasizedFontFamily,
		),
		displayLarge = TextStyle(
			fontFamily = displayLargeFontFamily,
		),
	)

val LocalSudokuTypography = staticCompositionLocalOf {
	AppTypography
}

fun FontVariation.ascenderHeight(ascenderHeight: Float): FontVariation.Setting {
	require(ascenderHeight in 649f..854f) { "'Ascender Height' must be in 649f..854f" }
	return FontVariation.Setting("YTAS", ascenderHeight)
}
