package com.example.feature.statistics.filter.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.util.fastRoundToInt
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HintsFilterView(
	currentMaxHints: Int?,
	modifier: Modifier = Modifier,
	isRange: Boolean = false,
) {
	val maxHints = remember(currentMaxHints) { currentMaxHints?.toFloat()?.coerceAtLeast(2f) ?: 2f }
	val valueRange = remember(currentMaxHints) { 0f..maxHints }
	var isRange by remember { mutableStateOf(isRange) }

	var rangeSliderPosition by remember { mutableStateOf(0f..maxHints) }
	var sliderPosition by remember { mutableFloatStateOf(currentMaxHints?.toFloat() ?: 1f) }

	val startInteractionSource = remember { MutableInteractionSource() }
	val endInteractionSource = remember { MutableInteractionSource() }
	val singleInteractionSource = remember { MutableInteractionSource() }

	val steps: Int = maxHints.fastRoundToInt() - 1

	Column(modifier = modifier.fillMaxWidth()) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth(),
		) {
			Text("Select range")
			Spacer(Modifier.weight(1f))
			Switch(
				checked = isRange,
				onCheckedChange = { isRange = !isRange },
				modifier = Modifier,
			)
		}
		if (isRange) {
			RangeSlider(
				value = rangeSliderPosition,
				valueRange = valueRange,
				steps = steps,
				modifier = Modifier.padding(LocalPadding.current.tiny),
				startInteractionSource = startInteractionSource,
				endInteractionSource = endInteractionSource,
				onValueChange = { rangeSliderPosition = it },
				onValueChangeFinished = {
				},
				startThumb = {
					Label(
						label = {
							PlainTooltip(
								modifier = Modifier
									.wrapContentSize(Alignment.Center),
							) {
								Text(
									text = rangeSliderPosition.start.fastRoundToInt()
										.toString(),
								)
							}
						},
						interactionSource = startInteractionSource,
					) {
						SliderDefaults.Thumb(
							interactionSource = startInteractionSource,
						)
					}
				},
				endThumb = {
					Label(
						label = {
							PlainTooltip(
								modifier = Modifier
									.wrapContentWidth(),
							) {
								Text(
									text = rangeSliderPosition.endInclusive.fastRoundToInt()
										.toString(),
								)
							}
						},
						interactionSource = endInteractionSource,
					) {
						SliderDefaults.Thumb(
							interactionSource = endInteractionSource,
						)
					}
				},
			)
		} else {
			Slider(
				value = rangeSliderPosition.endInclusive,
				valueRange = valueRange,
				onValueChange = { rangeSliderPosition = 0f..it },
				onValueChangeFinished = { },
				steps = steps,
				modifier = Modifier.padding(LocalPadding.current.tiny),
				interactionSource = singleInteractionSource,
				thumb = {
					Label(
						label = {
							PlainTooltip(
								modifier = Modifier
									.wrapContentWidth(Alignment.CenterHorizontally),
							) {
								Text(rangeSliderPosition.endInclusive.fastRoundToInt().toString())
							}
						},
						interactionSource = singleInteractionSource,
					) {
						SliderDefaults.Thumb(
							interactionSource = singleInteractionSource,
						)
					}
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
				)
				HorizontalDivider(Modifier.fillMaxWidth())
				HintsFilterView(
					currentMaxHints = null,
					isRange = true,
				)
			}
		}
	}
}
