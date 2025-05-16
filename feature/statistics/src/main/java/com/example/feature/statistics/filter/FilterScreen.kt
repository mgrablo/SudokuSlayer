package com.example.feature.statistics.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.domain.statistics.GameResultFilter
import com.example.feature.statistics.SortState
import com.example.feature.statistics.StatisticsColumn
import com.example.feature.statistics.StatisticsUiState
import com.example.feature.statistics.StatisticsViewModel
import com.example.feature.statistics.StatisticsViewModel.StatisticsEvent
import com.example.feature.statistics.filter.components.FilterActionButtons
import com.example.feature.statistics.filter.components.FilterCategory
import com.example.feature.statistics.filter.components.GenericFilterChip
import com.example.feature.statistics.filter.components.HintsFilterView
import com.example.feature.statistics.insights.components.toText
import com.example.feature.uicore.consumeHorizontalDrag
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun FilterScreen(
	viewModel: StatisticsViewModel,
	onFabClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val visibleColumns by viewModel.visibleColumns.collectAsStateWithLifecycle()
	val gameResultFilterState by viewModel.gameResultFilter.collectAsStateWithLifecycle()

	FilterScreenContent(
		uiState = uiState,
		visibleColumns = visibleColumns,
		gameResultFilterState = gameResultFilterState,
		onEvent = { viewModel.onEvent(it) },
		onFabClick = onFabClick,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterScreenContent(
	uiState: StatisticsUiState,
	visibleColumns: PersistentSet<StatisticsColumn>,
	gameResultFilterState: GameResultFilter,
	onEvent: (StatisticsEvent) -> Unit,
	onFabClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val tableColumns = remember { StatisticsColumn.entries.toPersistentList() }
	val gameDifficulties = remember { GameDifficulty.entries.toPersistentList() }
	val sudokuGridSizes = remember { SudokuGridSize.entries.toPersistentList() }

	Scaffold(
		modifier = modifier,
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.screen_title)) },
				windowInsets = WindowInsets.displayCutout,
				navigationIcon = {
					IconButton(
						onClick = onFabClick,
					) {
						Icon(
							imageVector = Icons.AutoMirrored.Default.ArrowBack,
							contentDescription = stringResource(R.string.back_content_description),
						)
					}
				},
			)
		},
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues),
		) {
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(
						horizontal = LocalPadding.current.normal,
						vertical = LocalPadding.current.tiny,
					),
				verticalArrangement = Arrangement.spacedBy(LocalPadding.current.normal),
			) {
				FilterCategory(
					label = stringResource(R.string.visible_columns),
				) {
					FlowRow(
						horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
						maxItemsInEachRow = 3,
					) {
						for (column in tableColumns) {
							GenericFilterChip(
								isSelected = column in visibleColumns,
								label = column.getDisplayText(),
								onClick = { onEvent(StatisticsEvent.ToggleColumnVisibility(column)) },
							)
						}
					}
				}
				FilterCategory(
					label = stringResource(R.string.filter_by_difficulty),
				) {
					LazyRow(
						horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
					) {
						items(gameDifficulties) { difficulty ->
							GenericFilterChip(
								isSelected = difficulty in gameResultFilterState.difficulties,
								label = difficulty.name,
								onClick = {
									onEvent(
										StatisticsEvent.ToggleDifficultyFilter(
											difficulty,
										),
									)
								},
							)
						}
					}
				}
				FilterCategory(
					label = stringResource(R.string.filter_by_grid_size),
				) {
					LazyRow(
						horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
					) {
						items(sudokuGridSizes) { gridSize ->
							GenericFilterChip(
								isSelected = gridSize in gameResultFilterState.gridSizes,
								label = gridSize.toText(),
								onClick = { onEvent(StatisticsEvent.ToggleGridSizeFilter(gridSize)) },
							)
						}
					}
				}
				FilterCategory(
					label = "Filter by Hints Used",
					modifier = Modifier.consumeHorizontalDrag(),
				) {
					HintsFilterView(
						currentMaxHints = uiState.maxHintsUsed,
					)
				}
			}
			FilterActionButtons(
				onClearClick = { },
				onApplyClick = onFabClick,
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun FilterScreenPreview() {
	SudokuSlayerTheme {
		FilterScreenContent(
			uiState = StatisticsUiState(
				sortState = SortState(StatisticsColumn.Difficulty),
			),
			onEvent = { },
			onFabClick = { },
			modifier = Modifier.fillMaxSize(),
			visibleColumns = persistentSetOf(),
			gameResultFilterState = GameResultFilter(),
		)
	}
}
