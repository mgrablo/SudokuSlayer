package com.example.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.LocalPadding

@Composable
internal fun SettingsCategory(title: String, content: @Composable () -> Unit) {
	Column(
		modifier = Modifier.fillMaxWidth().padding(bottom = LocalPadding.current.small),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.primary,
		)
		content()
		HorizontalDivider(
			color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
		)
	}
}
