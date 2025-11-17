package com.example.sudokuslayer.feature.statistics.insights.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.sudokuslayer.feature.statistics.R
import com.example.sudokuslayer.feature.uicore.theme.LocalPadding

@Composable
internal fun InsightsSnackbar(
	snackBarHostState: SnackbarHostState,
	dismissState: SwipeToDismissBoxState,
	modifier: Modifier = Modifier,
) {
	SnackbarHost(
		hostState = snackBarHostState,
		modifier = modifier.imePadding(),
		snackbar = {
			SwipeToDismissBox(
				state = dismissState,
				backgroundContent = { },
			) {
				Snackbar(
					modifier = Modifier.padding(horizontal = LocalPadding.current.small),
					containerColor = MaterialTheme.colorScheme.inverseSurface,
					contentColor = MaterialTheme.colorScheme.inversePrimary,
					dismissAction = {
						TextButton(
							onClick = {
								snackBarHostState.currentSnackbarData?.dismiss()
							},
						) {
							Text(stringResource(R.string.snackbar_dismiss))
						}
					},
				) {
					Text(
						it.visuals.message,
						color = MaterialTheme.colorScheme.inversePrimary,
					)
				}
			}
		},
	)
}
