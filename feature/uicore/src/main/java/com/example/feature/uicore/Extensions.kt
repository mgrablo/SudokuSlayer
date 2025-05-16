package com.example.feature.uicore

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.consumeHorizontalDrag(): Modifier = this.pointerInput(Unit) {
	detectDragGestures { change, dragAmount ->
		if (dragAmount.x != 0f) {
			change.consume()
		}
	}
}
