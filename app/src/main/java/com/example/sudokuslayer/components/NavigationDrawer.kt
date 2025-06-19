package com.example.sudokuslayer.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.navigation.Destination
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("RestrictedApi")
@Composable
fun NavigationDrawer(
	destinations: PersistentList<Destination>,
	isSelected: (Destination) -> Boolean,
	navigateToScreen: (Destination) -> Unit,
	drawerState: DrawerState,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	val coroutineScope = rememberCoroutineScope()
	ModalNavigationDrawer(
		drawerState = drawerState,
		modifier = modifier,
		drawerContent = {
			ModalDrawerSheet(
				drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
			) {
				Text(
					text = "Sudoku Slayer",
					modifier = Modifier.padding(16.dp),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				HorizontalDivider()
				destinations.forEach { destination ->
					NavigationDrawerItem(
						isSelected = isSelected(destination),
						destination = destination,
						onClick = {
							navigateToScreen(destination)
							coroutineScope.launch {
								drawerState.close()
							}
						},
					)
				}
			}
		},
	) {
		Box(Modifier.fillMaxSize()) {
			content()
		}
	}
}
