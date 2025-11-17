package io.github.mgrablo.sudokuslayer.feature.statistics.filter.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.util.fastRoundToInt
import io.github.mgrablo.sudokuslayer.feature.statistics.R
import io.github.mgrablo.sudokuslayer.feature.uicore.components.SliderThumbWithTooltip
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HintsFilterView(
	currentMaxHints: Int,
	isRangeEnabled: Boolean,
	initialSliderStart: Float,
	initialSliderEnd: Float,
	onValueChange: (Int, Int) -> Unit,
	onSwitchChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	val valueRange = remember(currentMaxHints) { 0f..currentMaxHints.toFloat() }
	var selectedRange by remember(initialSliderStart, initialSliderEnd) {
		mutableStateOf(
			initialSliderStart..initialSliderEnd,
		)
	}

	val startInteractionSource = remember { MutableInteractionSource() }
	val endInteractionSource = remember { MutableInteractionSource() }
	val singleInteractionSource = remember { MutableInteractionSource() }

	val steps: Int = currentMaxHints - 1

	Column(modifier = modifier.fillMaxWidth()) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth(),
		) {
			Text(stringResource(R.string.select_hintsused_range))
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
				steps = steps,
				modifier = Modifier.padding(LocalPadding.current.tiny),
				startInteractionSource = startInteractionSource,
				endInteractionSource = endInteractionSource,
				onValueChange = { selectedRange = it },
				onValueChangeFinished = {
					onValueChange(
						selectedRange.start.toInt(),
						selectedRange.endInclusive.toInt(),
					)
				},
				startThumb = {
					SliderThumbWithTooltip(
						tooltipText = selectedRange.start.fastRoundToInt().toString(),
						interactionSource = startInteractionSource,
					)
				},
				endThumb = {
					SliderThumbWithTooltip(
						tooltipText = selectedRange.endInclusive.fastRoundToInt().toString(),
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
						selectedRange.endInclusive.toInt(),
						selectedRange.endInclusive.toInt(),
					)
				},
				steps = steps,
				modifier = Modifier.padding(LocalPadding.current.tiny),
				interactionSource = singleInteractionSource,
				thumb = {
					SliderThumbWithTooltip(
						tooltipText = selectedRange.endInclusive.fastRoundToInt().toString(),
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
private fun HintFilterViewPreview() {
	SudokuSlayerTheme {
		Surface {
			Column {
				HintsFilterView(
					currentMaxHints = 10,
					isRangeEnabled = false,
					onValueChange = { min, max -> },
					initialSliderEnd = 10f,
					initialSliderStart = 0f,
					onSwitchChange = { },
				)
				HorizontalDivider(Modifier.fillMaxWidth())
				HintsFilterView(
					currentMaxHints = 2,
					isRangeEnabled = true,
					initialSliderEnd = 2f,
					initialSliderStart = 0f,
					onValueChange = { min, max -> },
					onSwitchChange = { },
				)
			}
		}
	}
}
