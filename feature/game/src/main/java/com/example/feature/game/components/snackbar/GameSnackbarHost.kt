package com.example.feature.game.components.snackbar

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.example.feature.game.model.SnackbarState
import com.example.feature.uicore.components.SwipeDismissSnackbarHost
import com.example.sudokuslayer.feature.game.R

@Composable
internal fun GameSnackbarHost(
	snackbarHostState: SnackbarHostState,
	snackbarState: SnackbarState?,
	onDismissSnackbar: () -> Unit,
	onShowMistakes: () -> Unit,
	modifier: Modifier = Modifier,
	dismissBoxState: SwipeToDismissBoxState = rememberSwipeToDismissBoxState(
		initialValue = SwipeToDismissBoxValue.Settled,
	),
	snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(it) },
) {
	val latestOnDismissSnackbar by rememberUpdatedState(onDismissSnackbar)
	val latestOnShowMistakes by rememberUpdatedState(onShowMistakes)

	val context = LocalContext.current
	val resources = LocalResources.current

	LaunchedEffect(snackbarState) {
		val state = snackbarState
		when (state) {
			is SnackbarState.FoundMistakes -> {
				val message = resources.getQuantityString(
					R.plurals.mistakes_found_quantity,
					state.count,
					state.count,
				)
				val result = snackbarHostState.showSnackbar(
					message = message,
					actionLabel = context.getString(R.string.show_mistakes),
					duration = SnackbarDuration.Short,
				)
				if (result == SnackbarResult.ActionPerformed) {
					latestOnShowMistakes()
				} else {
					latestOnDismissSnackbar()
				}
			}

			is SnackbarState.NoMistakesFound -> {
				val message = context.getString(R.string.no_mistakes_found)
				snackbarHostState.showSnackbar(
					message = message,
					duration = SnackbarDuration.Short,
				)
				latestOnDismissSnackbar()
			}

			null -> {
				snackbarHostState.currentSnackbarData?.dismiss()
			}
		}
	}

	SwipeDismissSnackbarHost(
		snackbarHostState = snackbarHostState,
		onDismissSnackbar = latestOnDismissSnackbar,
		modifier = modifier,
		dismissBoxState = dismissBoxState,
		snackbar = snackbar,
	)
}
