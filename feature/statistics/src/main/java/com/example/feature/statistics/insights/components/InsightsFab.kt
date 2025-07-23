package com.example.feature.statistics.insights.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.feature.statistics.InsightsViewState
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R

@Composable
internal fun InsightsFab(
	onClick: () -> Unit,
	insightsViewState: InsightsViewState,
	activeFilterCount: Int,
	modifier: Modifier = Modifier,
) {
	if (insightsViewState is InsightsViewState.Success) {
		BadgedBox(
			modifier = modifier,
			badge = {
				if (activeFilterCount > 0) {
					Badge(
						modifier = Modifier.clip(CircleShape),
						contentColor = MaterialTheme.colorScheme.onError,
						containerColor = MaterialTheme.colorScheme.error,
					) {
						Text(
							text = activeFilterCount.toString(),
							color = MaterialTheme.colorScheme.onErrorContainer,
						)
					}
				}
			},
		) {
			FloatingActionButton(
				onClick = onClick,
				modifier = Modifier,
			) {
				Icon(
					painterResource(R.drawable.filter),
					contentDescription = stringResource(
						R.string.filter_fab_content_description,
					),
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun InsightsFABPreview() {
	SudokuSlayerTheme {
		InsightsFab(
			onClick = {},
			insightsViewState = InsightsViewState.Success,
			activeFilterCount = 0,
		)
	}
}

@PreviewLightDark
@Composable
private fun InsightsFABPreviewActiveFilters() {
	SudokuSlayerTheme {
		Box(Modifier.padding(8.dp)) {
			InsightsFab(
				onClick = {},
				insightsViewState = InsightsViewState.Success,
				activeFilterCount = 3,
			)
		}
	}
}
