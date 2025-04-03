package com.example.feature.uicore.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.shifthackz.catppuccin.palette.Catppuccin

@Immutable
data class HintSheetColors(val subtext: Color)

val MochaHintSheetColors = HintSheetColors(
	subtext = Catppuccin.Mocha.Subtext1,
)

val FrappeHintSheetColors = HintSheetColors(
	subtext = Catppuccin.Frappe.Subtext1,
)

val MacchiatoHintSheetColors = HintSheetColors(
	subtext = Catppuccin.Macchiato.Subtext1,
)

val LatteHintSheetColors = HintSheetColors(
	subtext = Catppuccin.Latte.Subtext1,
)

val LocalHintSheetColors = staticCompositionLocalOf<HintSheetColors> {
	MochaHintSheetColors
}
