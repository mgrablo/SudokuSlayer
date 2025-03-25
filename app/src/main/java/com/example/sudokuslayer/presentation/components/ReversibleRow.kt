package com.example.sudokuslayer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Vertical
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun ReversibleRow(
	modifier: Modifier = Modifier,
	horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
	verticalAlignment: Vertical = Alignment.Top,
	reverseLayout: Boolean = false,
	content: @Composable RowScope.() -> Unit,
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
		Row(modifier, horizontalArrangement, verticalAlignment) {
			CompositionLocalProvider(LocalLayoutDirection provides originDirection) {
				content()
			}
		}
	}
}
