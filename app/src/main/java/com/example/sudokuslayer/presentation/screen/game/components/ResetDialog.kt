package com.example.sudokuslayer.presentation.screen.game.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ResetDialog(
	isVisible: Boolean,
	onConfirmClick: () -> Unit,
	onDismissClick: () -> Unit,
	onClearNotesClick: () -> Unit,
) {
	if (isVisible) {
		AlertDialog(
			title = {
				Text(text = "Clear Sudoku", color = MaterialTheme.colorScheme.onSurface)
			},
			text = {
				Text(
					text = "Are you sure you want to clear the entire Sudoku board? This action cannot be undone.",
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			},
			icon = { Icon(Icons.Default.Warning, "") },
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
						text = "Yes",
						color = MaterialTheme.colorScheme.primary,
					)
				}
			},
			dismissButton = {
				TextButton(onDismissClick) {
					Text(
						text = "No",
						color = MaterialTheme.colorScheme.primary,
					)
				}
				TextButton(onClearNotesClick) {
					Text(
						text = "Clear only notes",
						color = MaterialTheme.colorScheme.primary,
					)
				}
			},
		)
	}
}
