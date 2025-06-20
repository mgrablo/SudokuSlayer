@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.feature.statistics.insights

import android.content.ClipData
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility.HideWhileIdle
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.domain.statistics.GameResultFilter
import com.example.feature.statistics.FilterUiState
import com.example.feature.statistics.InsightsUiState
import com.example.feature.statistics.LoadingState
import com.example.feature.statistics.StatisticsViewModel
import com.example.feature.statistics.StatisticsViewModel.StatisticsEvent
import com.example.feature.statistics.StatisticsViewModel.StatisticsEvent.ColumnHeaderClicked
import com.example.feature.statistics.StatisticsViewModel.StatisticsEvent.PlayGameClicked
import com.example.feature.statistics.filter.FilterBottomSheet
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
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun InsightsScreen(
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsViewModel = koinViewModel<StatisticsViewModel>(),
) {
	val uiState by viewModel.insightsUiState.collectAsStateWithLifecycle()
	val filterUiState by viewModel.filterUiState.collectAsStateWithLifecycle()
	val gameResultFilter by viewModel.gameResultFilter.collectAsStateWithLifecycle()
	val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
	val tableColumnsState by viewModel.tableColumns.collectAsStateWithLifecycle()
	val activeFilterCount by viewModel.activeFilterCount.collectAsStateWithLifecycle()
	val coroutineScope = rememberCoroutineScope()
	val clipboard = LocalClipboard.current

	InsightsScreenContent(
		uiState = uiState,
		filterUiState = filterUiState,
		gameResultFilter = gameResultFilter,
		loadingState = loadingState,
		tableColumnsState = tableColumnsState,
		activeFilterCount = activeFilterCount,
		onEvent = viewModel::onEvent,
		openDrawer = openDrawer,
		onCopySeedClick = {
			coroutineScope.launch {
				clipboard.setClipEntry(
					ClipEntry(
						ClipData(
							"Game Seed",
							arrayOf("text/plain"),
							ClipData.Item(it.toString()),
						),
					),
				)
			}
		},
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun InsightsScreenContent(
	uiState: InsightsUiState,
	filterUiState: FilterUiState,
	gameResultFilter: GameResultFilter,
	loadingState: LoadingState,
	tableColumnsState: PersistentList<ColumnDisplayState>,
	activeFilterCount: Int,
	onEvent: (StatisticsEvent) -> Unit,
	openDrawer: () -> Unit,
	onCopySeedClick: (Long) -> Unit,
	modifier: Modifier = Modifier,
) {
	val coroutineScope = rememberCoroutineScope()
	val snackBarHostState = remember { SnackbarHostState() }
	val dismissState = rememberSwipeToDismissBoxState(
		confirmValueChange = { value ->
			if (value != SwipeToDismissBoxValue.Settled) {
				snackBarHostState.currentSnackbarData?.dismiss()
				true
			} else {
				false
			}
		},
	)

	val filterSheetState = rememberModalBottomSheetState(
		skipPartiallyExpanded = true,
	)
	var showBottomSheet by remember { mutableStateOf(false) }

	LaunchedEffect(dismissState.currentValue) {
		if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
			dismissState.reset()
		}
	}

	Scaffold(
		modifier = modifier,
		snackbarHost = {
			SnackbarHost(
				hostState = snackBarHostState,
				modifier = Modifier.imePadding(),
				snackbar = {
					SwipeToDismissBox(
						state = dismissState,
						backgroundContent = {
						},
					) {
						Snackbar(
							modifier = Modifier.padding(horizontal = LocalPadding.current.small),
							containerColor = MaterialTheme.colorScheme.inverseSurface,
							contentColor = MaterialTheme.colorScheme.inversePrimary,
							dismissAction = {
								TextButton(
									onClick = {
										snackBarHostState.currentSnackbarData?.dismiss()
									},
								) {
									Text("Dismiss")
								}
							},
						) {
							Text(
								it.visuals.message,
								color = MaterialTheme.colorScheme.inversePrimary,
							)
						}
					}
				},
			)
		},
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
			if (loadingState is LoadingState.Success) {
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
				) {
					FloatingActionButton(
						onClick = {
							coroutineScope.launch {
								showBottomSheet = true
							}
						},
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
		},
	) { paddingValues ->
		Crossfade(
			targetState = loadingState,
			modifier = Modifier.padding(paddingValues),
		) { loadingState ->
			when (loadingState) {
				is LoadingState.Loading -> {
					Box(
						modifier = Modifier
							.fillMaxSize(),
						contentAlignment = Alignment.Center,
					) {
						ContainedLoadingIndicator()
					}
				}

				is LoadingState.NoData -> {
					Column(
						modifier = Modifier
							.fillMaxSize()
							.padding(LocalPadding.current.big),
						verticalArrangement = Arrangement.spacedBy(
							LocalPadding.current.small,
							Alignment.CenterVertically,
						),
						horizontalAlignment = Alignment.CenterHorizontally,
					) {
						BasicText(":(", autoSize = TextAutoSize.StepBased(), maxLines = 1)
						BasicText(
							stringResource(R.string.no_data_message),
							autoSize = TextAutoSize.StepBased(),
							maxLines = 1,
						)
					}
				}

				is LoadingState.Success -> {
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

					if (showBottomSheet) {
						FilterBottomSheet(
							uiState = filterUiState,
							sheetState = filterSheetState,
							tableColumns = tableColumnsState,
							gameResultFilterState = gameResultFilter,
							activeFilterCount = activeFilterCount,
							onEvent = onEvent,
							onApplyClick = {
								coroutineScope.launch {
									filterSheetState.hide()
									showBottomSheet = false
								}
							},
							onDismissRequest = {
								showBottomSheet = false
							},
						)
					}
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
								thumbVisibility = HideWhileIdle(
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
										mostPlayedDifficulty = difficultyText,
										mostPlayedGridSize = gridSizeText,
										modifier = Modifier,
									)
								}
							}
							insightsTableContent(
								gameResults = uiState.gameResults,
								sortState = uiState.sortState,
								visibleColumns = visibleColumns,
								scrollState = horizontalScrollState,
								onPlayClick = { onEvent(PlayGameClicked(it)) },
								onCopySeedClick = {
									onCopySeedClick(it)
									coroutineScope.launch {
										snackBarHostState.currentSnackbarData?.dismiss()
										snackBarHostState.showSnackbar(
											"Copied seed!",
											duration = SnackbarDuration.Short,
											withDismissAction = true,
										)
									}
								},
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

				is LoadingState.Error -> TODO()
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
		InsightsScreenContent(
			uiState = InsightsUiState(
				sortState = SortState(InsightsTableColumn.Difficulty, SortDirection.ASC),
				gameResults = entries,
				totalGamesPlayed = 3,
				totalTimeSpent = 125,
			),
			filterUiState = FilterUiState(),
			gameResultFilter = GameResultFilter(),
			loadingState = LoadingState.Success,
			activeFilterCount = 1,
			tableColumnsState = ColumnDisplayState.getAll(),
			onEvent = { },
			openDrawer = { },
			onCopySeedClick = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}
