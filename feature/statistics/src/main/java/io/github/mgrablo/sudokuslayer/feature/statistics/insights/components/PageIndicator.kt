package io.github.mgrablo.sudokuslayer.feature.statistics.insights.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme

@Composable
internal fun PageIndicator(
	currentPageIndex: Int,
	pageCount: Int,
	modifier: Modifier = Modifier,
	activeIndicatorSize: Dp = 8.dp,
	inactiveIndicatorSize: Dp = 4.dp,
	activeColor: Color = MaterialTheme.colorScheme.onPrimary.copy(0.9f),
	inactiveColor: Color = MaterialTheme.colorScheme.onPrimary.copy(0.6f),
) {
	Row(
		modifier = modifier
			.padding(4.dp),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		repeat(pageCount) { index ->
			val selected = index == currentPageIndex

			val height = if (selected) activeIndicatorSize else inactiveIndicatorSize
			val width: Dp by animateDpAsState(
				if (selected) activeIndicatorSize else inactiveIndicatorSize,
			)
			val color = if (selected) activeColor else inactiveColor

			Canvas(
				modifier = Modifier
					.size(width, height),
				onDraw = {
					drawRoundRect(
						color = color,
						cornerRadius = CornerRadius(height.toPx() / 2),
						size = Size(width.toPx(), height.toPx()),
					)
				},
			)
		}
	}
}

@Preview
@Composable
private fun PageIndicatorPreview() {
	SudokuSlayerTheme {
		Column(
			modifier = Modifier
				.size(100.dp)
				.background(MaterialTheme.colorScheme.primary)
				.padding(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			PageIndicator(
				currentPageIndex = 1,
				pageCount = 2,
				modifier = Modifier,
			)
			PageIndicator(
				currentPageIndex = 1,
				pageCount = 3,
				modifier = Modifier,
			)
			PageIndicator(
				currentPageIndex = 1,
				pageCount = 5,
				modifier = Modifier,
			)
		}
	}
}
