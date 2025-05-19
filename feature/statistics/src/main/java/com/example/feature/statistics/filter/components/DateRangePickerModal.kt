package com.example.feature.statistics.filter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.DialogState
import com.composables.core.LocalModalWindow
import com.composables.core.Scrim
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateRangePickerModal(
	dialogState: DialogState,
	onDateRangeSelect: (Pair<Long?, Long?>) -> Unit,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	initialStartDate: Long? = null,
	initialEndDate: Long? = null,
) {
	val dateRangePickerState = rememberDateRangePickerState(
		initialSelectedStartDateMillis = initialStartDate,
		initialSelectedEndDateMillis = initialEndDate,
	)

	Dialog(dialogState) {
		Scrim()
		DialogPanel(
			modifier = modifier.padding(LocalPadding.current.big),
		) {
			val window = LocalModalWindow.current
			val view = LocalView.current
			val insetsController = WindowCompat.getInsetsController(window, view)
			insetsController.apply {
				hide(WindowInsetsCompat.Type.systemBars())
				systemBarsBehavior =
					WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
			}
			DateRangePickerModalContent(
				state = dateRangePickerState,
				onDismiss = onDismiss,
				onDateRangeSelect = onDateRangeSelect,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateRangePickerModalContent(
	state: DateRangePickerState,
	onDismiss: () -> Unit,
	onDateRangeSelect: (Pair<Long?, Long?>) -> Unit,
) {
	Surface(
		color = MaterialTheme.colorScheme.surface,
		tonalElevation = 3.dp,
		shape = RoundedCornerShape(LocalPadding.current.normal),
	) {
		Column {
			DateRangePicker(
				state = state,
				modifier = Modifier
					.sizeIn(
						maxHeight = 524.dp,
					),
				title = {
					DateRangePickerDefaults.DateRangePickerTitle(
						displayMode = state.displayMode,
						modifier = Modifier.padding(
							horizontal = LocalPadding.current.normal,
							vertical = LocalPadding.current.normal,
						),
						contentColor = DatePickerDefaults.colors().titleContentColor,
					)
				},
				headline = {
					DateRangePickerDefaults.DateRangePickerHeadline(
						selectedStartDateMillis = state.selectedStartDateMillis,
						selectedEndDateMillis = state.selectedEndDateMillis,
						displayMode = state.displayMode,
						dateFormatter = remember { DatePickerDefaults.dateFormatter() },
						contentColor = DatePickerDefaults.colors().headlineContentColor,
						modifier = Modifier.padding(
							start = LocalPadding.current.normal,
							bottom = LocalPadding.current.normal,
						),
					)
				},
			)
			Row(
				horizontalArrangement = Arrangement.End,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(LocalPadding.current.tiny),
			) {
				TextButton(
					onClick = onDismiss,
				) {
					Text(
						text = stringResource(R.string.cancel_button),
						color = MaterialTheme.colorScheme.primary,
					)
				}
				Spacer(Modifier.size(LocalPadding.current.tiny))
				TextButton(
					onClick = {
						onDateRangeSelect(
							state.selectedStartDateMillis to state.selectedEndDateMillis,
						)
						onDismiss()
					},
				) {
					Text(
						text = stringResource(R.string.ok_button),
						color = MaterialTheme.colorScheme.primary,
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun DateRangePickerModalPreview() {
	SudokuSlayerTheme {
		DateRangePickerModalContent(
			state = rememberDateRangePickerState(),
			onDismiss = {},
			onDateRangeSelect = {},
		)
	}
}
