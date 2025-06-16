@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.feature.statistics.insights

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.feature.statistics.InsightsUiState
import com.example.feature.statistics.STATISTICS_FAB_EXPLODE_BOUNDS
import com.example.feature.statistics.StatisticsViewModel
import com.example.feature.statistics.StatisticsViewModel.StatisticsEvent
import com.example.feature.statistics.insights.components.CompactSummaryLayout
import com.example.feature.statistics.insights.components.ExpandedSummaryLayout
import com.example.feature.statistics.insights.components.insightsTableContent
import com.example.feature.statistics.model.ColumnDisplayState
import com.example.feature.statistics.model.InsightsTableColumn
import com.example.feature.statistics.model.SortDirection
import com.example.feature.statistics.model.SortState
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.toLocalizedString
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDateTime
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun InsightsScreen(
	openDrawer: () -> Unit,
	onFabClick: () -> Unit,
	animatedVisibilityScope: AnimatedVisibilityScope,
	sharedTransitionScope: SharedTransitionScope,
	modifier: Modifier = Modifier,
	viewModel: StatisticsViewModel = koinViewModel<StatisticsViewModel>(),
) {
	val uiState by viewModel.insightsUiState.collectAsStateWithLifecycle()
	val tableColumnsState by viewModel.tableColumns.collectAsStateWithLifecycle()
	val activeFilterCount by viewModel.activeFilterCount.collectAsStateWithLifecycle()

	sharedTransitionScope.InsightsScreenContent(
		uiState = uiState,
		tableColumnsState = tableColumnsState,
		activeFilterCount = activeFilterCount,
		onEvent = viewModel::onEvent,
		openDrawer = openDrawer,
		onFabClick = onFabClick,
		animatedVisibilityScope = animatedVisibilityScope,
		modifier = modifier,
		isLoading = viewModel.isLoading,
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SharedTransitionScope.InsightsScreenContent(
	uiState: InsightsUiState,
	isLoading: Boolean,
	tableColumnsState: PersistentList<ColumnDisplayState>,
	activeFilterCount: Int,
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
			BadgedBox(
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
				modifier = Modifier.sharedBounds(
					sharedContentState = rememberSharedContentState(
						key = STATISTICS_FAB_EXPLODE_BOUNDS,
					),
					animatedVisibilityScope = animatedVisibilityScope,
				),
			) {
				FloatingActionButton(
					onClick = onFabClick,
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
		},
	) { paddingValues ->
		Crossfade(
			targetState = isLoading,
			modifier = Modifier.padding(paddingValues),
		) { loading ->
			if (loading) {
				Box(
					modifier = Modifier
						.fillMaxSize(),
					contentAlignment = Alignment.Center,
				) {
					ContainedLoadingIndicator()
				}
			} else {
				val formattedTimeSpent = rememberFormattedTime(uiState.totalTimeSpent.toFloat())
				val formattedAvgTime = rememberFormattedTime(uiState.avgTime.toFloat())
				val formattedFastest = rememberFormattedTime(uiState.fastestGame.toFloat())
				val formattedSlowest = rememberFormattedTime(uiState.longestGame.toFloat())
				val difficultyText = uiState.mostPlayedDifficulty?.toLocalizedString() ?: ""
				val gridSizeText = uiState.mostPlayedGridSize.toString()
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
					modifier = Modifier
						.fillMaxSize(),
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
							thumbVisibility = ThumbVisibility.HideWhileIdle(
								enter = fadeIn(),
								exit = fadeOut(),
								hideDelay = 0.5.seconds,
							),
						)
					}
					LazyColumn(
						modifier = Modifier
							.fillMaxWidth(),
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
									mostPlayedDifficulty = difficultyText,
									mostPlayedGridSize = gridSizeText,
									modifier = Modifier,
								)

								false -> ExpandedSummaryLayout(
									totalGamesPlayed = uiState.totalGamesPlayed.toString(),
									totalHintsUsed = uiState.totalHintsUsed.toString(),
									formattedTimeSpent = formattedTimeSpent,
									formattedSlowest = formattedSlowest,
									formattedFastest = formattedFastest,
									formattedAvgTime = formattedAvgTime,
									modifier = Modifier,
								)
							}
						}
						insightsTableContent(
							gameResults = uiState.gameResults,
							sortState = uiState.sortState,
							visibleColumns = visibleColumns,
							scrollState = horizontalScrollState,
							onPlayClick = { onEvent(StatisticsEvent.PlayGameClicked(it)) },
							onCopySeedClick = {
							},
							onColumnHeaderClick = { column ->
								onEvent(
									StatisticsEvent.ColumnHeaderClicked(
										column,
									),
								)
							},
						)
					}
				}
			}
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
						totalGamesPlayed = 3,
						totalTimeSpent = 125,
					),
					activeFilterCount = 1,
					tableColumnsState = ColumnDisplayState.getAll(),
					onEvent = { },
					openDrawer = { },
					onFabClick = { },
					animatedVisibilityScope = this,
					modifier = Modifier.fillMaxSize(),
					isLoading = false,
				)
			}
		}
	}
}
