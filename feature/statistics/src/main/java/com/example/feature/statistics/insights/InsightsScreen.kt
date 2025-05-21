@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.feature.statistics.insights

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.feature.statistics.InsightsUiState
import com.example.feature.statistics.STATISTICS_FAB_EXPLODE_BOUNDS
import com.example.feature.statistics.StatisticsViewModel
import com.example.feature.statistics.StatisticsViewModel.StatisticsEvent
import com.example.feature.statistics.insights.components.InsightsTable
import com.example.feature.statistics.model.ColumnDisplayState
import com.example.feature.statistics.model.InsightsTableColumn
import com.example.feature.statistics.model.SortDirection
import com.example.feature.statistics.model.SortState
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDateTime

@Composable
internal fun InsightsScreen(
	viewModel: StatisticsViewModel,
	openDrawer: () -> Unit,
	onFabClick: () -> Unit,
	animatedVisibilityScope: AnimatedVisibilityScope,
	sharedTransitionScope: SharedTransitionScope,
	modifier: Modifier = Modifier,
) {
	val uiState by viewModel.insightsUiState.collectAsStateWithLifecycle()
	val tableColumnsState by viewModel.tableColumns.collectAsStateWithLifecycle()

	sharedTransitionScope.InsightsScreenContent(
		uiState = uiState,
		tableColumnsState = tableColumnsState,
		onEvent = { viewModel.onEvent(it) },
		openDrawer = openDrawer,
		onFabClick = onFabClick,
		animatedVisibilityScope = animatedVisibilityScope,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SharedTransitionScope.InsightsScreenContent(
	uiState: InsightsUiState,
	tableColumnsState: PersistentList<ColumnDisplayState>,
	onEvent: (StatisticsEvent) -> Unit,
	openDrawer: () -> Unit,
	onFabClick: () -> Unit,
	animatedVisibilityScope: AnimatedVisibilityScope,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		modifier = modifier,
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text(stringResource(R.string.insights_screen_title)) },
				modifier = Modifier,
				windowInsets = WindowInsets.displayCutout,
				navigationIcon = {
					IconButton(onClick = openDrawer) {
						Icon(
							Icons.Default.Menu,
							contentDescription = stringResource(
								R.string.open_menu_content_description,
							),
						)
					}
				},
			)
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = onFabClick,
				modifier = Modifier
					.sharedBounds(
						sharedContentState = rememberSharedContentState(
							key = STATISTICS_FAB_EXPLODE_BOUNDS,
						),
						animatedVisibilityScope = animatedVisibilityScope,
					),
			) {
				Icon(
					painterResource(R.drawable.filter),
					contentDescription = stringResource(
						R.string.filter_fab_content_description,
					),
				)
			}
		},
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize(),
		) {
			InsightsTable(
				gameResults = uiState.gameResults,
				sortState = uiState.sortState,
				tableColumnsState = tableColumnsState,
				onColumnHeaderClick = { onEvent(StatisticsEvent.ColumnHeaderClicked(it)) },
				onPlayClick = { onEvent(StatisticsEvent.PlayGameClicked(it)) },
				modifier = Modifier.padding(vertical = LocalPadding.current.tiny),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun InsightsScreenPreview() {
	val entries = persistentListOf<GameResult>(
		GameResult(
			id = "1",
			timeInSeconds = 124,
			difficulty = GameDifficulty.Medium,
			gridSize = SudokuGridSize.NINE,
			hintsUsed = 4,
			completionDate = LocalDateTime.parse("2010-06-21T22:19:44"),
			seed = null,
		),
		GameResult(
			id = "2",
			timeInSeconds = 32,
			difficulty = GameDifficulty.Easy,
			gridSize = SudokuGridSize.FOUR,
			hintsUsed = 0,
			completionDate = LocalDateTime.parse("2010-06-01T22:19:44"),
			seed = 1,
		),
		GameResult(
			id = "3",
			timeInSeconds = 1268,
			difficulty = GameDifficulty.Expert,
			gridSize = SudokuGridSize.SIXTEEN,
			hintsUsed = 45,
			completionDate = LocalDateTime.parse("2010-12-29T22:19:44"),
			seed = 2,
		),
	)
	SudokuSlayerTheme {
		SharedTransitionLayout {
			AnimatedVisibility(true) {
				InsightsScreenContent(
					uiState = InsightsUiState(
						sortState = SortState(InsightsTableColumn.Difficulty, SortDirection.ASC),
						gameResults = entries,
						isLoading = false,
						totalGamesPlayed = 3,
						totalTimeSpent = 125,
					),
					tableColumnsState = ColumnDisplayState.getAll(),
					onEvent = { },
					openDrawer = { },
					onFabClick = { },
					animatedVisibilityScope = this,
					modifier = Modifier.fillMaxSize(),
				)
			}
		}
	}
}
