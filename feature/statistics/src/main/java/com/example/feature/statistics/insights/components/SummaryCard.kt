package com.example.feature.statistics.insights.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.theme.SudokuSlayerTheme

@Composable
internal fun SummaryCard(
	label: String,
	value: String,
	modifier: Modifier = Modifier,
	style: SummaryCardStyle = SummaryCardStyleDefaults.defaults(),
) {
	Card(
		modifier = modifier
			.height(IntrinsicSize.Min),
		shape = style.shape,
		colors = CardDefaults.cardColors(
			containerColor = style.backgroundColor,
			contentColor = style.contentColor,
		),
	) {
		Column(
			modifier = Modifier
				.fillMaxSize() // Fill the card's bounds
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			BasicText(
				text = value,
				style = style.valueTextStyle,
				maxLines = 1,
				autoSize = TextAutoSize.StepBased(),
				modifier = Modifier.weight(0.7f),
				color = { style.contentColor },
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = label,
				style = style.labelTextStyle,
				textAlign = TextAlign.Center,
				color = style.contentColor.copy(alpha = 0.8f),
				modifier = Modifier.weight(0.3f),
			)
		}
	}
}

@Preview(showBackground = true, widthDp = 150, heightDp = 150)
@Composable
private fun SummaryCardPreview_Rounded() {
	SudokuSlayerTheme {
		SummaryCard(
			label = "Games Played",
			value = "127",
			modifier = Modifier.size(140.dp),
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun SummaryCardPreview_Cut() {
	SudokuSlayerTheme {
		SummaryCard(
			label = "Avg. Time",
			value = "5m3s",
			style = SummaryCardStyleDefaults.defaults(
				shape = CutCornerShape(
					topStart = 24.dp,
					bottomEnd = 24.dp,
					topEnd = 8.dp,
					bottomStart = 8.dp,
				),
				backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
				contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
			),
			modifier = Modifier.size(140.dp),
		)
	}
}

@Preview(showBackground = true, widthDp = 150, heightDp = 150)
@Composable
private fun SummaryCardPreview_Circle() {
	SudokuSlayerTheme {
		SummaryCard(
			label = "Win Rate",
			value = "88%",
			modifier = Modifier.size(140.dp),
			style = SummaryCardStyleDefaults.defaults(
				shape = CircleShape,
				backgroundColor = MaterialTheme.colorScheme.primaryContainer,
				contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
			),
		)
	}
}
