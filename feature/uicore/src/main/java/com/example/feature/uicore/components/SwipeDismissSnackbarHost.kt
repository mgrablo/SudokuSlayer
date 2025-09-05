package com.example.feature.uicore.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SwipeDismissSnackbarHost(
	snackbarHostState: SnackbarHostState,
	onDismissSnackbar: () -> Unit,
	modifier: Modifier = Modifier,
	dismissBoxState: SwipeToDismissBoxState = rememberSwipeToDismissBoxState(
		initialValue = SwipeToDismissBoxValue.Settled,
	),
	snackbar: @Composable ((SnackbarData) -> Unit) = { Snackbar(it) },
) {
	val scope = rememberCoroutineScope()
	SwipeToDismissBox(
		state = dismissBoxState,
		backgroundContent = { },
		modifier = modifier.fillMaxWidth(),
		onDismiss = {
			scope.launch {
				snackbarHostState.currentSnackbarData?.dismiss()
				onDismissSnackbar()
				// Delay to make sure the snackbar completely disappears before resetting the state
				delay(150)
				dismissBoxState.snapTo(SwipeToDismissBoxValue.Settled)
			}
		},
	) {
		SnackbarHost(hostState = snackbarHostState, snackbar = snackbar)
	}
}
