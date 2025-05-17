package com.example.feature.uicore.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderThumbWithTooltip(
	tooltipText: String,
	interactionSource: MutableInteractionSource,
	modifier: Modifier = Modifier,
) {
	Label(
		modifier = modifier,
		label = {
			PlainTooltip(
				modifier = Modifier
					.wrapContentWidth(Alignment.CenterHorizontally),
			) {
				Text(tooltipText)
			}
		},
		interactionSource = interactionSource,
	) {
		SliderDefaults.Thumb(
			interactionSource = interactionSource,
		)
	}
}
