@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.feature.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.feature.statistics.StatisticsViewModel.Event
import com.example.feature.statistics.components.TableHeader
import com.example.feature.statistics.components.TableRow
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.datetime.LocalDateTime

@Composable
internal fun StatisticsScreen(
	viewModel: StatisticsViewModel,
	openDrawer: () -> Unit,
	onFabClick: () -> Unit,
	animatedVisibilityScope: AnimatedVisibilityScope,
	sharedTransitionScope: SharedTransitionScope,
	modifier: Modifier = Modifier,
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	sharedTransitionScope.StatisticsScreenContent(
		uiState = uiState,
		onEvent = { viewModel.onEvent(it) },
		openDrawer = openDrawer,
		onFabClick = onFabClick,
		animatedVisibilityScope = animatedVisibilityScope,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SharedTransitionScope.StatisticsScreenContent(
	uiState: StatisticsUiState,
	onEvent: (Event) -> Unit,
	openDrawer: () -> Unit,
	onFabClick: () -> Unit,
	animatedVisibilityScope: AnimatedVisibilityScope,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		modifier = modifier,
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text("Statistics") },
				modifier = Modifier,
				windowInsets = WindowInsets.displayCutout,
				navigationIcon = {
					IconButton(onClick = openDrawer) {
						Icon(Icons.Default.Menu, contentDescription = "Open menu")
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
				Icon(painterResource(R.drawable.filter), contentDescription = "Filter")
			}
		},
	) { paddingValues ->
		LazyColumn(
			modifier = Modifier.padding(paddingValues),
		) {
			item {
				TableHeader(
					sortState = uiState.sortState,
					visibleColumns = uiState.columnsToShow,
					onSortChange = { onEvent(Event.UpdateSorting(it)) },
				)
			}
			items(
				items = uiState.gameResults,
				key = { it.id },
			) { entry ->
				TableRow(entry)
				HorizontalDivider()
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun StatisticsScreenPreview() {
	val entries = persistentListOf<GameResult>(
		GameResult(
			id = "1",
			timeInSeconds = 124,
			difficulty = GameDifficulty.Medium,
			gridSize = SudokuGridSize.NINE,
			hintsUsed = 4,
			completedAt = LocalDateTime.parse("2010-06-21T22:19:44"),
		),
		GameResult(
			id = "2",
			timeInSeconds = 32,
			difficulty = GameDifficulty.Easy,
			gridSize = SudokuGridSize.FOUR,
			hintsUsed = 0,
			completedAt = LocalDateTime.parse("2010-06-01T22:19:44"),
		),
		GameResult(
			id = "3",
			timeInSeconds = 1268,
			difficulty = GameDifficulty.Expert,
			gridSize = SudokuGridSize.SIXTEEN,
			hintsUsed = 45,
			completedAt = LocalDateTime.parse("2010-12-29T22:19:44"),
		),
	)
	SudokuSlayerTheme {
		SharedTransitionLayout {
			AnimatedVisibility(true) {
				StatisticsScreenContent(
					uiState = StatisticsUiState(
						columnsToShow = StatisticsColumn.entries.toPersistentSet(),
						sortState = SortState(StatisticsColumn.Difficulty, SortDirection.ASC),
						gameResults = entries,
						isLoading = false,
						totalGamesPlayed = 3,
						totalTimeSpent = 125,
					),
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
