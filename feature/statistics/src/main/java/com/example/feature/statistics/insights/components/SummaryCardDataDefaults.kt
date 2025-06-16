package com.example.feature.statistics.insights.components

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Immutable
data class SummaryCardStyle(
	val backgroundColor: Color,
	val contentColor: Color,
	val shape: Shape,
	val valueTextStyle: TextStyle,
	val labelTextStyle: TextStyle,
)

object SummaryCardStyleDefaults {
	@OptIn(ExperimentalMaterial3ExpressiveApi::class)
	@Composable
	fun defaults(
		backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
		contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
		shape: Shape = MaterialTheme.shapes.medium,
		valueTextStyle: TextStyle = MaterialTheme.typography.displayMediumEmphasized.copy(
			fontWeight = FontWeight.Bold,
		),
		labelTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
	): SummaryCardStyle = SummaryCardStyle(
		backgroundColor = backgroundColor,
		contentColor = contentColor,
		shape = shape,
		valueTextStyle = valueTextStyle,
		labelTextStyle = labelTextStyle,
	)
}
