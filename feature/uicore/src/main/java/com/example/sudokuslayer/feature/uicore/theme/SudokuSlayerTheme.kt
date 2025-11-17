package com.example.sudokuslayer.feature.uicore.theme

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.sudokuslayer.domain.settings.models.ColorScheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SudokuSlayerTheme(
	colorScheme: ColorScheme,
	content:
	@Composable () -> Unit,
) {
	val extendedColorScheme = remember(colorScheme) {
		if (colorScheme.isDark) {
			extendedDark
		} else {
			extendedLight
		}
	}
	val materialColorScheme = remember(colorScheme) {
		when (colorScheme) {
			is ColorScheme.Mocha -> mochaColorScheme
			is ColorScheme.Macchiato -> macchiatoColorScheme
			is ColorScheme.Latte -> latteColorScheme
			is ColorScheme.Frappe -> frappeColorScheme
		}
	}

	SharedTransitionLayout {
		CompositionLocalProvider(
			LocalAppColorScheme provides colorScheme,
			LocalExtendedColorScheme provides extendedColorScheme,
			LocalSudokuTypography provides AppTypography,
			LocalSharedTransitionScope provides this@SharedTransitionLayout,
		) {
			MaterialExpressiveTheme(
				colorScheme = materialColorScheme,
			) {
				content()
			}
		}
	}
}

@Composable
fun SudokuSlayerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	SudokuSlayerTheme(
		colorScheme = if (darkTheme) ColorScheme.Mocha() else ColorScheme.Latte(),
		content = content,
	)
}

val LocalAppColorScheme = staticCompositionLocalOf<ColorScheme> {
	ColorScheme.Mocha()
}
