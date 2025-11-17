package com.example.sudokuslayer.feature.uicore.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun ReversibleColumn(
	modifier: Modifier = Modifier,
	verticalArrangement: Arrangement.Vertical = Arrangement.Top,
	horizontalAlignment: Alignment.Horizontal = Alignment.Start,
	reverseLayout: Boolean = false,
	content: @Composable ColumnScope.() -> Unit,
) {
	val originDirection = LocalLayoutDirection.current
	val direction = when {
		reverseLayout -> when (originDirection) {
			LayoutDirection.Rtl -> LayoutDirection.Ltr
			else -> LayoutDirection.Rtl
		}
		else -> originDirection
	}
	CompositionLocalProvider(LocalLayoutDirection provides direction) {
		Column(
			modifier = modifier,
			verticalArrangement = verticalArrangement,
			horizontalAlignment = horizontalAlignment,
		) {
			CompositionLocalProvider(LocalLayoutDirection provides originDirection) {
				content()
			}
		}
	}
}
