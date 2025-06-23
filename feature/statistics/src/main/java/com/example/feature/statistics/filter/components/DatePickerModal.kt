package com.example.feature.statistics.filter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import com.composables.core.DialogProperties
import com.composables.core.DialogState
import com.composables.core.Scrim
import com.composeunstyled.LocalModalWindow
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerModal(
	dialogState: DialogState,
	onDateSelect: (Long?) -> Unit,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	properties: DialogProperties = DialogProperties(),
	initialDateMillis: Long? = null,
) {
	val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
	Dialog(
		state = dialogState,
		onDismiss = onDismiss,
		properties = properties,
	) {
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
			DatePickerModalContent(
				datePickerState = datePickerState,
				onDateSelect = onDateSelect,
				onDismiss = onDismiss,
			)
		}
	}
}

/**
 * Content of the DatePickerModal for preview purposes without the Dialog wrapper
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModalContent(
	datePickerState: DatePickerState,
	onDateSelect: (Long?) -> Unit,
	onDismiss: () -> Unit,
) {
	Surface(
		color = MaterialTheme.colorScheme.surface,
		tonalElevation = 3.dp,
		shape = RoundedCornerShape(LocalPadding.current.normal),
	) {
		Column {
			DatePicker(state = datePickerState)
			Row(
				horizontalArrangement = Arrangement.End,
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
				Spacer(modifier = Modifier.size(LocalPadding.current.tiny))
				TextButton(
					onClick = {
						onDateSelect(datePickerState.selectedDateMillis)
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
private fun DatePickerModalPreviewLight() {
	SudokuSlayerTheme {
		DatePickerModalContent(
			datePickerState = rememberDatePickerState(initialSelectedDateMillis = null),
			onDateSelect = {},
			onDismiss = {},
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun DatePickerModalWithInitialDatePreview() {
	SudokuSlayerTheme {
		DatePickerModalContent(
			datePickerState = rememberDatePickerState(
				initialSelectedDateMillis = System.currentTimeMillis(),
			),
			onDateSelect = {},
			onDismiss = {},
		)
	}
}
