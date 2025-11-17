package com.example.sudokuslayer.feature.statistics.filter.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.sudokuslayer.feature.statistics.R
import com.example.sudokuslayer.feature.uicore.components.SliderThumbWithTooltip
import com.example.sudokuslayer.feature.uicore.rememberFormattedTime
import com.example.sudokuslayer.feature.uicore.theme.LocalPadding
import com.example.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolvingTimeFilterView(
	currentMaxTime: Long,
	isRangeEnabled: Boolean,
	initialSliderStart: Float,
	initialSliderEnd: Float,
	onValueChange: (Long, Long) -> Unit,
	onSwitchChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	val valueRange = remember(currentMaxTime) { 0f..currentMaxTime.toFloat() }
	var selectedRange by remember(initialSliderStart, initialSliderEnd) {
		mutableStateOf(initialSliderStart..initialSliderEnd)
	}

	val startInteractionSource = remember { MutableInteractionSource() }
	val endInteractionSource = remember { MutableInteractionSource() }
	val singleInteractionSource = remember { MutableInteractionSource() }

	val startTooltipText = rememberFormattedTime(selectedRange.start)
	val endTooltipText = rememberFormattedTime(selectedRange.endInclusive)

	Column(modifier = modifier.fillMaxWidth()) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth(),
		) {
			Text(stringResource(R.string.select_solvetime_range))
			Spacer(Modifier.weight(1f))
			Switch(
				checked = isRangeEnabled,
				onCheckedChange = {
					onSwitchChange(it)
				},
			)
		}

		if (isRangeEnabled) {
			RangeSlider(
				value = selectedRange,
				valueRange = valueRange,
				modifier = Modifier.padding(LocalPadding.current.tiny),
				startInteractionSource = startInteractionSource,
				endInteractionSource = endInteractionSource,
				onValueChange = { selectedRange = it },
				onValueChangeFinished = {
					onValueChange(
						selectedRange.start.toLong(),
						selectedRange.endInclusive.toLong(),
					)
				},
				startThumb = {
					SliderThumbWithTooltip(
						tooltipText = startTooltipText,
						interactionSource = startInteractionSource,
					)
				},
				endThumb = {
					SliderThumbWithTooltip(
						tooltipText = endTooltipText,
						interactionSource = endInteractionSource,
					)
				},
			)
		} else {
			Slider(
				value = selectedRange.endInclusive,
				valueRange = valueRange,
				onValueChange = { selectedRange = 0f..it },
				onValueChangeFinished = {
					onValueChange(
						selectedRange.endInclusive.toLong(),
						selectedRange.endInclusive.toLong(),
					)
				},
				modifier = Modifier.padding(LocalPadding.current.tiny),
				interactionSource = singleInteractionSource,
				thumb = {
					SliderThumbWithTooltip(
						tooltipText = endTooltipText,
						interactionSource = singleInteractionSource,
					)
				},
				colors = SliderDefaults.colors(
					inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
				),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun SolvingTimeFilterViewPreview() {
	SudokuSlayerTheme {
		Surface {
			SolvingTimeFilterView(
				currentMaxTime = 124L,
				isRangeEnabled = true,
				onValueChange = { min, max -> },
				onSwitchChange = { },
				initialSliderStart = 3f,
				initialSliderEnd = 150f,
				modifier = Modifier,
			)
		}
	}
}
