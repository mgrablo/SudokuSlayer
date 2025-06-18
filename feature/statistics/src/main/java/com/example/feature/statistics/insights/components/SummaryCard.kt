package com.example.feature.statistics.insights.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.feature.statistics.model.SummaryCardData
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SummaryCard(
	data: SummaryCardData,
	modifier: Modifier = Modifier,
	animationSpec: AnimationSpec<Color> = MotionScheme.expressive().fastEffectsSpec(),
	onClick: (() -> Unit)? = null,
) = SummaryCard(
	label = data.label,
	value = data.value,
	style = data.style,
	modifier = modifier,
	animationSpec = animationSpec,
	onClick = onClick,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SummaryCard(
	label: String,
	value: String,
	modifier: Modifier = Modifier,
	animationSpec: AnimationSpec<Color> = MotionScheme.expressive().fastEffectsSpec(),
	style: SummaryCardStyle = SummaryCardStyleDefaults.defaults(),
	onClick: (() -> Unit)? = null,
) {
	val animatedBackgroundColor by animateColorAsState(
		targetValue = style.backgroundColor,
		animationSpec = animationSpec,
		label = "SummaryCardBackgroundColorAnimation",
	)
	val animatedContentColor by animateColorAsState(
		targetValue = style.contentColor,
		animationSpec = animationSpec,
		label = "SummaryCardContentColorAnimation",
	)
	if (onClick != null) {
		Card(
			modifier = modifier
				.height(IntrinsicSize.Min),
			onClick = onClick,
			shape = style.shape,
			colors = CardDefaults.cardColors(
				containerColor = animatedBackgroundColor,
				contentColor = animatedContentColor,
			),
		) {
			SummaryCardContent(
				label = label,
				value = value,
				style = style,
			)
		}
	} else {
		Card(
			modifier = modifier
				.height(IntrinsicSize.Min),
			shape = style.shape,
			colors = CardDefaults.cardColors(
				containerColor = animatedBackgroundColor,
				contentColor = animatedContentColor,
			),
		) {
			SummaryCardContent(
				label = label,
				value = value,
				style = style,
			)
		}
	}
}

@Composable
private fun SummaryCardContent(label: String, value: String, style: SummaryCardStyle) {
	Column(
		modifier = Modifier
			.fillMaxSize() // Fill the card's bounds
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		Box(
			Modifier
				.weight(0.7f)
				.padding(horizontal = LocalPadding.current.tiny),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = value,
				style = style.valueTextStyle,
				maxLines = 1,
				autoSize = TextAutoSize.StepBased(),
				textAlign = TextAlign.Center,
				color = style.contentColor,
			)
		}
		Box(
			Modifier.weight(0.3f),
		) {
			Text(
				text = label,
				style = style.labelTextStyle,
				textAlign = TextAlign.Center,
				color = style.contentColor.copy(alpha = 0.8f),
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
