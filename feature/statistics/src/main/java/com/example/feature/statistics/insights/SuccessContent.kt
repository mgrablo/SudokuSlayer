package com.example.feature.statistics.insights

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility.HideWhileIdle
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.feature.statistics.InsightsUiState
import com.example.feature.statistics.StatisticsViewModel
import com.example.feature.statistics.StatisticsViewModel.StatisticsEvent.ColumnHeaderClicked
import com.example.feature.statistics.insights.components.CompactSummaryLayout
import com.example.feature.statistics.insights.components.ExpandedSummaryLayout
import com.example.feature.statistics.insights.components.insightsTableContent
import com.example.feature.statistics.model.ColumnDisplayState
import com.example.feature.statistics.model.InsightsTableColumn
import com.example.feature.statistics.model.SortDirection
import com.example.feature.statistics.model.SortState
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.toLocalizedString
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SuccessContent(
	uiState: InsightsUiState,
	tableColumnsState: PersistentList<ColumnDisplayState>,
	onEvent: (StatisticsViewModel.StatisticsEvent) -> Unit,
	onCopySeedClick: (Long) -> Unit,
	onNavigateToCreator: (Long, SudokuGridSize, GameDifficulty) -> Unit,
	modifier: Modifier = Modifier,
) {
	val formattedTimeSpent = rememberFormattedTime(uiState.totalTimeSpent.toFloat())
	val formattedAvgTime = rememberFormattedTime(uiState.avgTime.toFloat())
	val formattedFastest = rememberFormattedTime(uiState.fastestGame.toFloat())
	val formattedSlowest = rememberFormattedTime(uiState.longestGame.toFloat())
	val mostPlayedDifficulty = uiState.mostPlayedDifficulty?.toLocalizedString() ?: ""
	val mostPlayedSize = uiState.mostPlayedGridSize?.toLocalizedString() ?: ""
	val visibleColumns = remember(tableColumnsState) {
		tableColumnsState.filter { it.visible }.map(
			ColumnDisplayState::column,
		).toPersistentList()
	}

	val lazyListState = rememberLazyListState()
	val scrollAreaState = rememberScrollAreaState(lazyListState)
	val horizontalScrollState = rememberScrollState()

	ScrollArea(
		state = scrollAreaState,
		modifier = modifier,
	) {
		VerticalScrollbar(
			modifier = Modifier
				.fillMaxHeight()
				.align(Alignment.TopEnd)
				.zIndex(1f),
		) {
			Thumb(
				modifier = Modifier.background(
					MaterialTheme.colorScheme.onSurface.copy(0.2f),
					RoundedCornerShape(100),
				),
				thumbVisibility = HideWhileIdle(
					enter = fadeIn(),
					exit = fadeOut(),
					hideDelay = 0.5.seconds,
				),
			)
		}
		LazyColumn(
			modifier = Modifier.fillMaxWidth(),
			state = lazyListState,
			contentPadding = PaddingValues(vertical = LocalPadding.current.normal),
			verticalArrangement = Arrangement.spacedBy(LocalPadding.current.small),
		) {
			item {
				when (uiState.summariesCompactLayout) {
					true -> CompactSummaryLayout(
						totalGamesPlayed = uiState.totalGamesPlayed.toString(),
						totalHintsUsed = uiState.totalHintsUsed.toString(),
						formattedTimeSpent = formattedTimeSpent,
						formattedSlowest = formattedSlowest,
						formattedFastest = formattedFastest,
						formattedAvgTime = formattedAvgTime,
						mostPlayedDifficulty = mostPlayedDifficulty,
						mostPlayedGridSize = mostPlayedSize,
						modifier = Modifier,
					)

					false -> ExpandedSummaryLayout(
						totalGamesPlayed = uiState.totalGamesPlayed.toString(),
						totalHintsUsed = uiState.totalHintsUsed.toString(),
						formattedTimeSpent = formattedTimeSpent,
						formattedSlowest = formattedSlowest,
						formattedFastest = formattedFastest,
						formattedAvgTime = formattedAvgTime,
						mostPlayedDifficulty = mostPlayedDifficulty,
						mostPlayedGridSize = mostPlayedSize,
						modifier = Modifier,
					)
				}
			}
			insightsTableContent(
				gameResults = uiState.gameResults,
				sortState = uiState.sortState,
				visibleColumns = visibleColumns,
				scrollState = horizontalScrollState,
				onPlayClick = { seed, size, difficulty -> onNavigateToCreator(seed, size, difficulty) },
				onCopySeedClick = onCopySeedClick,
				onColumnHeaderClick = { column ->
					onEvent(
						ColumnHeaderClicked(
							column,
						),
					)
				},
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SuccessContentPreview() {
	val uiState = InsightsUiState(
		sortState = SortState(InsightsTableColumn.Date, SortDirection.ASC),
		gameResults = persistentListOf(),
		totalGamesPlayed = 10,
		totalTimeSpent = 3600,
		avgTime = 360,
		totalHintsUsed = 5,
		fastestGame = 120,
		longestGame = 600,
		summariesCompactLayout = true,
		mostPlayedDifficulty = GameDifficulty.Easy,
		mostPlayedGridSize = SudokuGridSize.NINE,
	)
	val tableColumnsState = ColumnDisplayState.getAll()

	SuccessContent(
		uiState = uiState,
		tableColumnsState = tableColumnsState,
		onEvent = {},
		onCopySeedClick = {},
		onNavigateToCreator = { _, _, _ -> },
	)
}
