package com.example.feature.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.statistics.StatisticsViewModel.Event
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.toPersistentSet

@Composable
internal fun FilterScreen(
	viewModel: StatisticsViewModel,
	onFabClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	FilterScreenContent(
		uiState = uiState,
		onEvent = { viewModel.onEvent(it) },
		onFabClick = onFabClick,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterScreenContent(
	uiState: StatisticsUiState,
	onEvent: (StatisticsViewModel.Event) -> Unit,
	onFabClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		modifier = modifier,
		topBar = {
			TopAppBar(
				title = { },
				navigationIcon = {
					IconButton(
						onClick = onFabClick,
					) {
						Icon(
							imageVector = Icons.AutoMirrored.Default.ArrowBack,
							contentDescription = "Go back",
						)
					}
				},
			)
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = onFabClick,
			) {
				Icon(
					imageVector = Icons.Default.Check,
					contentDescription = "Accept",
				)
			}
		},
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(horizontal = LocalPadding.current.tiny),
		) {
			Text(
				text = "Visible columns",
				style = MaterialTheme.typography.titleLarge,
			)
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
			) {
				for (option in StatisticsColumn.entries.toTypedArray()) {
					FilterChip(
						selected = option in uiState.columnsToShow,
						onClick = { onEvent(Event.ToggleColumnVisibility(option)) },
						label = {
							Text(
								text = option.getDisplayText(),
								style = MaterialTheme.typography.labelLarge,
								color = if (option in
									uiState.columnsToShow
								) {
									MaterialTheme.colorScheme.onSecondaryContainer
								} else {
									MaterialTheme.colorScheme.onSurface
								},
							)
						},
						colors = FilterChipDefaults.filterChipColors(
							containerColor = MaterialTheme.colorScheme.surfaceContainer,
							labelColor = MaterialTheme.colorScheme.onSurface,
							selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
							selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
						),
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun FilterScreenPreview() {
	SudokuSlayerTheme {
		FilterScreenContent(
			uiState = StatisticsUiState(
				columnsToShow = StatisticsColumn.entries.toPersistentSet() - StatisticsColumn.Date,
				sortState = SortState(StatisticsColumn.Difficulty)
			),
			onEvent = { },
			onFabClick = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}
