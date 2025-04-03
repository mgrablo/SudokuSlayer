package com.example.sudokuslayer.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import com.example.feature.uicore.theme.LocalPadding

@Composable
fun NavigationDrawerItem(
	isSelected: Boolean,
	destination: Destination,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	NavigationDrawerItem(
		icon = {
			DestinationIcon(destination.icon)
		},
		label = {
			Text(
				text = destination.routeName,
				color = if (isSelected) {
					MaterialTheme.colorScheme.onSecondaryContainer
				} else {
					MaterialTheme.colorScheme.onSurfaceVariant
				},
			)
		},
		selected = isSelected,
		onClick = onClick,
		modifier = modifier.padding(LocalPadding.current.tiny),
		colors =
		NavigationDrawerItemDefaults.colors(
			selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
		),
	)
}

@Composable
private fun DestinationIcon(icon: AppIcon) {
	when (icon) {
		is AppIcon.ResourceIcon -> Icon(painterResource(icon.resourceId), "")
		is AppIcon.VectorIcon -> Icon(icon.imageVector, "")
	}
}
