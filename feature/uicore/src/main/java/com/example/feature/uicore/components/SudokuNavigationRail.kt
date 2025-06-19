package com.example.feature.uicore.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailItemDefaults
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import com.example.feature.uicore.navigation.DestinationIcon
import com.example.feature.uicore.theme.LocalSudokuTypography
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.uicore.R
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
) {
	ModalWideNavigationRail(
		modifier = modifier.widthIn(max = 240.dp),
		state = state,
		hideOnCollapse = true,
		expandedHeaderTopPadding = 0.dp,
		windowInsets = WindowInsets(),
		colors = WideNavigationRailDefaults.colors(
			modalContainerColor = MaterialTheme.colorScheme.surface,
		),
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
			val selected = isSelected(it)
			WideNavigationRailItem(
				railExpanded = state.currentValue == WideNavigationRailValue.Expanded,
				selected = selected,
				onClick = { navigateToScreen(it) },
				icon = {
					DestinationIcon(it.icon)
				},
				colors = WideNavigationRailItemDefaults.colors(
					selectedTextColor = MaterialTheme.colorScheme.secondary,
					selectedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
					selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
				),
				label = {
					Text(
						text = it.routeName,
						color = if (selected) {
							MaterialTheme.colorScheme.onSecondary
						} else {
							MaterialTheme.colorScheme.onSurfaceVariant
						},
					)
				},
			)
		}
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLightDark
@Composable
private fun SudokuNavigationRailPreview() {
	val state = rememberWideNavigationRailState(
		initialValue = WideNavigationRailValue.Expanded,
	)
	val destinations = persistentListOf(
		Destination("Home", AppIcon.VectorIcon(Icons.Default.Home)),
		Destination("Settings", AppIcon.VectorIcon(Icons.Default.Settings)),
		Destination("Home", AppIcon.VectorIcon(Icons.Default.Home)),
		Destination("Home", AppIcon.VectorIcon(Icons.Default.Home)),
	)
	SudokuSlayerTheme {
		SudokuNavigationRail(
			state = state,
			destinations = destinations,
			isSelected = { it == destinations.first() },
			onCloseDrawer = { },
			navigateToScreen = { },
		)
	}
}
