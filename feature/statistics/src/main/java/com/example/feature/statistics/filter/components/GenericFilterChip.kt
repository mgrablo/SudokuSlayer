package com.example.feature.statistics.filter.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun GenericFilterChip(
	isSelected: Boolean,
	label: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	AnimatedContent(isSelected) { selected ->
		FilterChip(
			modifier = modifier,
			selected = selected,
			onClick = onClick,
			label = {
				Text(
					text = label,
					style = MaterialTheme.typography.labelLarge,
					color = filterChipLabelColor(selected),
				)
			},
			leadingIcon = {
				if (selected) {
					Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
				}
			},
			colors = FilterChipDefaults.filterChipColors(
				containerColor = MaterialTheme.colorScheme.surfaceContainer,
				labelColor = MaterialTheme.colorScheme.onSurface,
				selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
				selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
			),
		)
	}
}

@Composable
private fun filterChipLabelColor(selected: Boolean): Color = if (selected) {
	MaterialTheme.colorScheme.onSecondaryContainer
} else {
	MaterialTheme.colorScheme.onSurface
}
