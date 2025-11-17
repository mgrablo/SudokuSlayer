package io.github.mgrablo.sudokuslayer.feature.uicore.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.mgrablo.sudokuslayer.feature.uicore.R
import io.github.mgrablo.sudokuslayer.feature.uicore.navigation.AppIcon
import io.github.mgrablo.sudokuslayer.feature.uicore.navigation.Destination
import io.github.mgrablo.sudokuslayer.feature.uicore.navigation.DestinationIcon
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalSudokuTypography
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SudokuNavigationRail(
	state: WideNavigationRailState,
	destinations: PersistentList<Destination>,
	isSelected: (Destination) -> Boolean,
	navigateToScreen: (Destination) -> Unit,
	onCloseDrawer: () -> Unit,
	modifier: Modifier = Modifier,
	hasActiveGame: Boolean = false,
) {
	ModalWideNavigationRail(
		modifier = modifier.widthIn(max = 240.dp),
		state = state,
		hideOnCollapse = true,
		expandedHeaderTopPadding = 0.dp,
		windowInsets = WindowInsets(),
		header = {
			Row(
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(
					text = "Sudoku Slayer",
					textAlign = TextAlign.Center,
					style = LocalSudokuTypography.current.displayMediumEmphasized,
					fontSize = 28.sp,
				)
				IconButton(
					onClick = onCloseDrawer,
				) {
					Icon(
						painterResource(R.drawable.menu_open),
						contentDescription = "Close menu",
					)
				}
			}
		},
	) {
		destinations.forEach {
			val isSelected = isSelected(it)
			val isEnabled = if (it.routeId == "sudoku_game") {
				hasActiveGame
			} else {
				true
			}
			val textColor =
				if (isSelected) {
					MaterialTheme.colorScheme.onSecondary
				} else if (!isEnabled) {
					MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
				} else {
					MaterialTheme.colorScheme.onSurfaceVariant
				}
			WideNavigationRailItem(
				railExpanded = state.currentValue == WideNavigationRailValue.Expanded,
				selected = isSelected,
				enabled = isEnabled,
				onClick = { navigateToScreen(it) },
				icon = {
					DestinationIcon(it.icon)
				},
				label = {
					Text(
						text = stringResource(it.displayNameRes),
						color = textColor,
					)
				},
			)
		}
	}
}

private data class PreviewDestination(
	override val routeId: String,
	override val displayNameRes: Int = R.string.preview_destination_name,
	override val icon: AppIcon = AppIcon.VectorIcon(Icons.Default.Settings),
) : Destination

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLightDark
@Composable
private fun SudokuNavigationRailPreview() {
	val state = rememberWideNavigationRailState(
		initialValue = WideNavigationRailValue.Expanded,
	)
	val destinations = persistentListOf(
		PreviewDestination("preview1"),
		PreviewDestination("preview2"),
		PreviewDestination("preview3"),
		PreviewDestination("preview4"),
		PreviewDestination("preview5"),
	)
	var selectedDestination by remember { mutableStateOf(destinations.first()) }
	SudokuSlayerTheme {
		SudokuNavigationRail(
			state = state,
			destinations = destinations,
			isSelected = { it == selectedDestination },
			onCloseDrawer = { },
			navigateToScreen = {
				selectedDestination = it as PreviewDestination
			},
		)
	}
}
