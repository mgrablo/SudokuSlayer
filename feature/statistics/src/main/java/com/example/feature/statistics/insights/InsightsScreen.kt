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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.core.Menu
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility.HideWhileIdle
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberDialogState
import com.composables.core.rememberMenuState
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
import com.example.feature.statistics.insights.components.DeleteDataDialog
import com.example.feature.statistics.insights.components.ExpandedSummaryLayout
import com.example.feature.statistics.insights.components.InsightsFab
import com.example.feature.statistics.insights.components.InsightsSnackbar
import com.example.feature.statistics.insights.components.TopAppBarActions
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
	val dismissState = rememberSwipeToDismissBoxState()

	val filterSheetState = rememberModalBottomSheetState(
		skipPartiallyExpanded = true,
	)
	var showBottomSheet by remember { mutableStateOf(false) }
	val actionsMenuState = rememberMenuState()
	val dialogState = rememberDialogState()

	LaunchedEffect(dismissState.currentValue) {
		if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
			dismissState.reset()
		}
	}

	DeleteDataDialog(
		dialogState = dialogState,
		onCancelClick = {
			coroutineScope.launch {
				dialogState.visible = false
			}
		},
		onConfirmClick = {
			coroutineScope.launch {
				dialogState.visible = false
				onEvent(StatisticsEvent.ClearData)
			}
		},
	)

	Scaffold(
		modifier = modifier,
		contentWindowInsets = WindowInsets.systemBars,
		snackbarHost = {
			InsightsSnackbar(
				snackBarHostState = snackBarHostState,
				dismissState = dismissState,
			)
		},
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text(stringResource(R.string.insights_screen_title)) },
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surfaceContainer,
				),
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
				actions = {
					TopAppBarActions(
						menuState = actionsMenuState,
						onClearClick = {
							dialogState.visible = true
						},
					)
				},
			)
		},
		floatingActionButton = {
			InsightsFab(
				onClick = {
					coroutineScope.launch {
						showBottomSheet = true
					}
				},
				loadingState = loadingState,
				activeFilterCount = activeFilterCount,
			)
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
						Text(
							text = stringResource(R.string.no_games_found),
							autoSize = TextAutoSize.StepBased(),
							maxLines = 1,
						)
						Text(
							text = stringResource(R.string.no_data_message),
							autoSize = TextAutoSize.StepBased(),
							color = MaterialTheme.colorScheme.onSurfaceVariant,
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
					val gridSizeText = uiState.mostPlayedGridSize?.toLocalizedString() ?: ""
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
	val entries = persistentListOf(
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
