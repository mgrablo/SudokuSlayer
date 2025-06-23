package com.example.feature.statistics.filter

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.domain.statistics.GameResultFilter
import com.example.feature.statistics.FilterUiState
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
import com.example.feature.statistics.model.InsightsTableColumn
import com.example.feature.statistics.toLong
import com.example.feature.uicore.consumeHorizontalDrag
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun FilterBottomSheet(
	uiState: FilterUiState,
	sheetState: SheetState,
	tableColumns: PersistentList<ColumnDisplayState>,
	gameResultFilterState: GameResultFilter,
	activeFilterCount: Int,
	onEvent: (StatisticsEvent) -> Unit,
	onApplyClick: () -> Unit,
	onDismissRequest: () -> Unit,
	modifier: Modifier = Modifier,
) {
	ModalBottomSheet(
		sheetState = sheetState,
		modifier = modifier
			.fillMaxWidth()
			.statusBarsPadding(),
		onDismissRequest = onDismissRequest,
		containerColor = MaterialTheme.colorScheme.surface,
	) {
		FilterBottomSheetContent(
			uiState = uiState,
			tableColumns = tableColumns,
			gameResultFilterState = gameResultFilterState,
			activeFilterCount = activeFilterCount,
			onEvent = onEvent,
			onApplyClick = onApplyClick,
			modifier = Modifier,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheetContent(
	uiState: FilterUiState,
	tableColumns: PersistentList<ColumnDisplayState>,
	gameResultFilterState: GameResultFilter,
	activeFilterCount: Int,
	onEvent: (StatisticsEvent) -> Unit,
	onApplyClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val gameDifficulties = remember { GameDifficulty.entries.toPersistentList() }
	val sudokuGridSizes = remember { SudokuGridSize.entries.toPersistentList() }

	val scrollState = rememberScrollState()
	val scrollAreaState = rememberScrollAreaState(scrollState)
	val completionDateBringIntoViewRequester = remember { BringIntoViewRequester() }

	Column(
		modifier = modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.surface),
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
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Text(
					stringResource(R.string.filter_screen_title),
					style = MaterialTheme.typography.titleLarge,
				)
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
			onApplyClick = onApplyClick,
			activeFilterCount = activeFilterCount,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun FilterScreenPreview() {
	SudokuSlayerTheme {
		FilterBottomSheetContent(
			uiState = FilterUiState(),
			onEvent = { },
			onApplyClick = { },
			tableColumns = persistentListOf(
				ColumnDisplayState(
					column = InsightsTableColumn.GridSize,
					visible = true,
				),
				ColumnDisplayState(
					column = InsightsTableColumn.Difficulty,
					visible = true,
				),
			),
			gameResultFilterState = GameResultFilter(),
			activeFilterCount = 2,
		)
	}
}
