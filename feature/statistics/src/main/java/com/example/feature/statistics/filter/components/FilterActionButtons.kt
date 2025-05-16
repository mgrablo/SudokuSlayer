package com.example.feature.statistics.filter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.theme.LocalPadding

@Composable
fun FilterActionButtons(
	onClearClick: () -> Unit,
	onApplyClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		tonalElevation = 3.dp,
	) {
		Column {
			HorizontalDivider(Modifier.fillMaxWidth())
			Row(
				Modifier.padding(LocalPadding.current.normal),
				horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.normal),
			) {
				OutlinedButton(
					onClick = onClearClick,
					modifier = Modifier.weight(1f),
				) {
					Icon(Icons.Default.Clear, contentDescription = null)
					Text("Clear", color = MaterialTheme.colorScheme.tertiary)
				}
				Button(
					onClick = onApplyClick,
					modifier = Modifier.weight(1f),
				) {
					Icon(Icons.Default.Check, contentDescription = null)
					Text("Apply", color = MaterialTheme.colorScheme.onPrimary)
				}
			}
		}
	}
}
