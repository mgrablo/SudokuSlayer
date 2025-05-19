package com.example.feature.statistics.filter

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.domain.statistics.GameResultFilter
import com.example.feature.statistics.FilterUiState
import com.example.feature.statistics.StatisticsViewModel
import com.example.feature.statistics.StatisticsViewModel.StatisticsEvent
import com.example.feature.statistics.filter.components.CompletionDateFilterView
import com.example.feature.statistics.filter.components.FilterActionButtons
import com.example.feature.statistics.filter.components.FilterCategory
import com.example.feature.statistics.filter.components.GenericFilterChip
import com.example.feature.statistics.filter.components.HintsFilterView
import com.example.feature.statistics.filter.components.SolvingTimeFilterView
import com.example.feature.statistics.filter.components.VisibleColumnsView
import com.example.feature.statistics.insights.components.toText
import com.example.feature.statistics.model.ColumnDisplayState
import com.example.feature.statistics.toLong
import com.example.feature.uicore.consumeHorizontalDrag
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun FilterScreen(
	viewModel: StatisticsViewModel,
	onFabClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val uiState by viewModel.filterUiState.collectAsStateWithLifecycle()
	val tableColumnsState by viewModel.tableColumns.collectAsStateWithLifecycle()
	val gameResultFilterState by viewModel.gameResultFilter.collectAsStateWithLifecycle()
	val activeFilterCount by viewModel.activeFilterCount.collectAsStateWithLifecycle()

	FilterScreenContent(
		uiState = uiState,
		tableColumns = tableColumnsState,
		gameResultFilterState = gameResultFilterState,
		activeFilterCount = activeFilterCount,
		onEvent = { viewModel.onEvent(it) },
		onFabClick = onFabClick,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterScreenContent(
	uiState: FilterUiState,
	tableColumns: PersistentList<ColumnDisplayState>,
	gameResultFilterState: GameResultFilter,
	activeFilterCount: Int,
	onEvent: (StatisticsEvent) -> Unit,
	onFabClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val gameDifficulties = remember { GameDifficulty.entries.toPersistentList() }
	val sudokuGridSizes = remember { SudokuGridSize.entries.toPersistentList() }

	val scrollState = rememberScrollState()
	val scrollAreaState = rememberScrollAreaState(scrollState)
	val completionDateBringIntoViewRequester = remember { BringIntoViewRequester() }

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
				.padding(paddingValues)
				.fillMaxSize(),
		) {
			ScrollArea(
				state = scrollAreaState,
				modifier = Modifier.weight(1f),
			) {
				Column(
					modifier = Modifier
						.padding(
							horizontal = LocalPadding.current.normal,
							vertical = LocalPadding.current.tiny,
						)
						.verticalScroll(scrollState)
						.padding(bottom = LocalPadding.current.big),
					verticalArrangement = Arrangement.spacedBy(LocalPadding.current.normal),
				) {
					FilterCategory(
						label = stringResource(R.string.visible_columns),
					) {
						VisibleColumnsView(
							allTableColumns = tableColumns,
							toggleVisibility = { column ->
								onEvent(
									StatisticsEvent.ToggleColumnVisibility(
										column,
									),
								)
							},
							onReorder = { from, to ->
								onEvent(
									StatisticsEvent.ReorderColumns(from, to),
								)
							},
						)
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
									onClick = {
										onEvent(
											StatisticsEvent.ToggleGridSizeFilter(
												gridSize,
											),
										)
									},
								)
							}
						}
					}
					FilterCategory(
						label = stringResource(R.string.filter_by_hints_used),
					) {
						HintsFilterView(
							modifier = Modifier.consumeHorizontalDrag(),
							currentMaxHints = uiState.maxHintsUsed.coerceAtLeast(2),
							initialSliderStart = gameResultFilterState.minHintsUsed?.toFloat()
								?: 0f,
							initialSliderEnd = gameResultFilterState.maxHintsUsed?.toFloat()
								?.coerceAtLeast(2f) ?: uiState.maxHintsUsed.coerceAtLeast(2)
								.toFloat(),
							isRangeEnabled = uiState.isHintsUsedRangeEnabled,
							onValueChange = { min, max ->
								onEvent(StatisticsEvent.SetHintsUsedRangeFilter(min, max))
							},
							onSwitchChange = {
								onEvent(
									StatisticsEvent.UpdateHintsUsedRangeEnabled(
										it,
									),
								)
							},
						)
					}
					FilterCategory(
						label = stringResource(R.string.filter_by_solve_time),
					) {
						SolvingTimeFilterView(
							modifier = Modifier.consumeHorizontalDrag(),
							currentMaxTime = uiState.longestGame,
							isRangeEnabled = uiState.isSolveTimeRangeEnabled,
							initialSliderStart = gameResultFilterState.minCompletionTime?.toFloat()
								?: 0f,
							initialSliderEnd = gameResultFilterState.maxCompletionTime?.toFloat()
								?.coerceAtLeast(2f) ?: uiState.longestGame.coerceAtLeast(2)
								.toFloat(),
							onValueChange = { min, max ->
								onEvent(StatisticsEvent.SetSolveTimeRangeFilter(min, max))
							},
							onSwitchChange = {
								onEvent(
									StatisticsEvent.UpdateSolveTimeRangeEnabled(
										it,
									),
								)
							},
						)
					}
					FilterCategory(
						label = "Filter by Completion Date",
					) {
						CompletionDateFilterView(
							isRangeEnabled = uiState.isCompletionDateRangeEnabled,
							onSwitchChange = {
								onEvent(
									StatisticsEvent.UpdateCompletionDateRangeEnabled(
										it,
									),
								)
							},
							initialDateRange = gameResultFilterState.let {
								it.dateRangeStart?.toLong() to it.dateRangeEnd?.toLong()
							},
							onDateRangeSelect = {
								onEvent(StatisticsEvent.SetCompletionDateRangeFilter(it))
							},
							bringIntoViewRequester = completionDateBringIntoViewRequester,
						)
					}
				}
				VerticalScrollbar(
					modifier = Modifier
						.align(Alignment.TopEnd)
						.fillMaxHeight(),
				) {
					Thumb(
						modifier = Modifier.background(
							MaterialTheme.colorScheme.onSurface.copy(0.2f),
							RoundedCornerShape(100),
						),
						thumbVisibility = ThumbVisibility.HideWhileIdle(
							enter = fadeIn(),
							exit = fadeOut(),
							hideDelay = 0.5.seconds,
						),
					)
				}
			}
			FilterActionButtons(
				onClearClick = { onEvent(StatisticsEvent.ClearFilters) },
				onApplyClick = onFabClick,
				activeFilterCount = activeFilterCount,
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun FilterScreenPreview() {
	SudokuSlayerTheme {
		FilterScreenContent(
			uiState = FilterUiState(),
			onEvent = { },
			onFabClick = { },
			modifier = Modifier.fillMaxSize(),
			tableColumns = persistentListOf(),
			gameResultFilterState = GameResultFilter(),
			activeFilterCount = 2,
		)
	}
}
