package com.example.feature.statistics.filter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.feature.uicore.theme.LocalPadding

@Composable
internal fun FilterCategory(
	label: String,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.background,
		shape = RoundedCornerShape(LocalPadding.current.normal),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(LocalPadding.current.tiny),
			verticalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
		) {
			Text(
				text = label,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
			)
			content()
		}
	}
}
