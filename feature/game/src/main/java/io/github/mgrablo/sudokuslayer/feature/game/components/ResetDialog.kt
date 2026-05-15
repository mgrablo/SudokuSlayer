package io.github.mgrablo.sudokuslayer.feature.game.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.mgrablo.sudokuslayer.feature.game.R

@Composable
internal fun ResetDialog(
	isVisible: Boolean,
	onConfirmClick: () -> Unit,
	onDismissClick: () -> Unit,
	onClearNotesClick: () -> Unit,
) {
	if (isVisible) {
		AlertDialog(
			title = {
				Text(text = stringResource(R.string.clear_sudoku), color = MaterialTheme.colorScheme.onSurface)
			},
			text = {
				Text(
					text = stringResource(R.string.clear_sudoku_confirmation),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			},
			icon = { Icon(Icons.Default.Warning, null) },
			containerColor = MaterialTheme.colorScheme.surface,
			textContentColor = MaterialTheme.colorScheme.onSurface,
			onDismissRequest = onDismissClick,
			confirmButton = {
				TextButton(
					onClick = onConfirmClick,
					colors =
					ButtonDefaults.textButtonColors(
						contentColor = MaterialTheme.colorScheme.primary,
					),
				) {
					Text(
						text = stringResource(R.string.yes),
						color = MaterialTheme.colorScheme.primary,
					)
				}
			},
			dismissButton = {
				TextButton(onDismissClick) {
					Text(
						text = stringResource(R.string.no),
						color = MaterialTheme.colorScheme.primary,
					)
				}
				TextButton(onClearNotesClick) {
					Text(
						text = stringResource(R.string.clear_only_notes),
						color = MaterialTheme.colorScheme.primary,
					)
				}
			},
		)
	}
}
