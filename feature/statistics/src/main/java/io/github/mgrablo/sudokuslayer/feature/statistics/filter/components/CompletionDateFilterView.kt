package io.github.mgrablo.sudokuslayer.feature.statistics.filter.components

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.composables.core.rememberDialogState
import io.github.mgrablo.sudokuslayer.feature.statistics.R
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CompletionDateFilterView(
	isRangeEnabled: Boolean,
	initialDateRange: Pair<Long?, Long?>,
	onSwitchChange: (Boolean) -> Unit,
	onDateRangeSelect: (Pair<Long?, Long?>) -> Unit,
	bringIntoViewRequester: BringIntoViewRequester,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
	) {
		var selectedStartDate by remember(initialDateRange.first) {
			mutableStateOf<Long?>(initialDateRange.first)
		}
		var selectedEndDate by remember(initialDateRange.second) {
			mutableStateOf<Long?>(initialDateRange.second)
		}
		var showModal by remember { mutableStateOf(false) }
		val dialogState = rememberDialogState(false)

		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth(),
		) {
			Text("Select date range")
			Spacer(Modifier.weight(1f))
			Switch(
				checked = isRangeEnabled,
				onCheckedChange = {
					onSwitchChange(it)
				},
			)
		}

		OutlinedTextField(
			value = selectedStartDate?.let { convertMillisToDate(it) } ?: "",
			onValueChange = { },
			readOnly = true,
			label = {
				Text(
					if (isRangeEnabled) {
						stringResource(R.string.start_day)
					} else {
						stringResource(
							R.string.selected_date,
						)
					},
				)
			},
			trailingIcon = {
				Icon(
					Icons.Default.DateRange,
					contentDescription = stringResource(
						R.string.select_date_content_description,
					),
				)
			},
			modifier = Modifier
				.padding(LocalPadding.current.tiny)
				.fillMaxWidth()
				.pointerInput(selectedStartDate) {
					awaitEachGesture {
						awaitFirstDown(pass = PointerEventPass.Initial)
						val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
						if (upEvent != null) {
							showModal = true
							dialogState.visible = true
						}
					}
				},
		)

		AnimatedVisibility(isRangeEnabled) {
			OutlinedTextField(
				value = selectedEndDate?.let { convertMillisToDate(it) } ?: "",
				readOnly = true,
				onValueChange = { },
				label = { Text(stringResource(R.string.end_day)) },
				trailingIcon = {
					Icon(
						Icons.Default.DateRange,
						contentDescription = stringResource(
							R.string.select_date_content_description,
						),
					)
				},
				modifier = Modifier
					.padding(LocalPadding.current.tiny)
					.fillMaxWidth()
					.bringIntoViewRequester(bringIntoViewRequester)
					.pointerInput(selectedEndDate) {
						awaitEachGesture {
							awaitFirstDown(pass = PointerEventPass.Initial)
							val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
							if (upEvent != null) {
								showModal = true
							}
						}
					},
			)
		}

		if (showModal) {
			if (isRangeEnabled) {
				DateRangePickerModal(
					dialogState = dialogState,
					onDateRangeSelect = {
						onDateRangeSelect(it)
					},
					onDismiss = { showModal = false },
					initialStartDate = selectedStartDate,
					initialEndDate = selectedEndDate,
				)
			} else {
				DatePickerModal(
					onDateSelect = {
						onDateRangeSelect(it to null)
					},
					onDismiss = {
						showModal = false
						dialogState.visible = false
					},
					initialDateMillis = selectedStartDate,
					dialogState = dialogState,
				)
			}
		}
	}

	LaunchedEffect(isRangeEnabled, bringIntoViewRequester) {
		if (isRangeEnabled) {
			bringIntoViewRequester.bringIntoView()
		}
	}
}

private fun convertMillisToDate(millis: Long): String {
	val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
	return formatter.format(Date(millis))
}

@PreviewLightDark
@Composable
private fun CompletionDateFilterViewPreview() {
	val bringIntoViewRequester = remember { BringIntoViewRequester() }
	SudokuSlayerTheme {
		Surface {
			CompletionDateFilterView(
				isRangeEnabled = false,
				initialDateRange = null to null,
				onDateRangeSelect = { },
				onSwitchChange = { },
				modifier = Modifier,
				bringIntoViewRequester = bringIntoViewRequester,
			)
		}
	}
}
