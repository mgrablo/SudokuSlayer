package com.example.feature.statistics.insights.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.DialogState
import com.composables.core.Icon
import com.composables.core.Scrim
import com.composables.core.rememberDialogState
import com.example.feature.uicore.HideSystemBars
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R

@Composable
internal fun DeleteDataDialog(
	dialogState: DialogState,
	onCancelClick: () -> Unit,
	onConfirmClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Dialog(
		state = dialogState,
	) {
		HideSystemBars()
		Scrim()
		DialogPanel(
			modifier = modifier.widthIn(max = 340.dp),
			contentPadding = PaddingValues(LocalPadding.current.large),
			shape = MaterialTheme.shapes.extraLarge,
			backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
			contentColor = MaterialTheme.colorScheme.onSurface,
			enter = scaleIn(initialScale = 0.8f) + fadeIn(tween(durationMillis = 250)),
			exit = scaleOut(targetScale = 0.6f) + fadeOut(tween(durationMillis = 150)),
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Icon(
					painter = painterResource(R.drawable.delete),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.secondary,
				)
				Spacer(Modifier.height(LocalPadding.current.normal))
				Text(
					text = stringResource(R.string.delete_dialog_header),
					style = MaterialTheme.typography.headlineSmall,
					color = MaterialTheme.colorScheme.onSurface,
				)
				Spacer(Modifier.height(LocalPadding.current.normal))
				Text(
					text = stringResource(R.string.delete_dialog_body),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				Spacer(Modifier.height(LocalPadding.current.large))
				Row(
					modifier = Modifier.align(Alignment.End),
					horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
				) {
					TextButton(
						onClick = onCancelClick,
					) {
						Text(
							text = stringResource(R.string.delete_dialog_cancel),
							style = MaterialTheme.typography.labelLarge,
							color = MaterialTheme.colorScheme.primary,
						)
					}
					TextButton(
						onClick = onConfirmClick,
					) {
						Text(
							text = stringResource(R.string.delete_dialog_confirm),
							style = MaterialTheme.typography.labelLarge,
							color = MaterialTheme.colorScheme.error,
						)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun DeleteDataDialogPreview() {
	SudokuSlayerTheme {
		DeleteDataDialog(
			dialogState = rememberDialogState(true),
			onCancelClick = { },
			onConfirmClick = { },
			modifier = Modifier,
		)
	}
}
