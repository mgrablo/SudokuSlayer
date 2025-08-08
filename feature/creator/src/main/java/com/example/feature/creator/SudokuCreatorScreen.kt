package com.example.feature.creator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.example.domain.core.Game
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.feature.creator.SudokuCreatorViewModel.Event
import com.example.feature.creator.components.ActiveGameCard
import com.example.feature.creator.components.AdvancedOptions
import com.example.feature.creator.components.DifficultySelector
import com.example.feature.creator.components.GridSizeSelector
import com.example.feature.creator.components.NewGameButton
import com.example.feature.creator.components.preview.BoardLoadingIndicator
import com.example.feature.creator.components.preview.BoardPreview
import com.example.feature.creator.components.preview.BoardPreviewState
import com.example.feature.creator.components.preview.rememberBoardPreviewState
import com.example.feature.creator.theme.SudokuCreatorTheme
import com.example.feature.uicore.theme.LocalPadding
import com.example.sudoku.model.SolutionGrid
import com.example.sudoku.model.SudokuGrid
import com.example.sudokuslayer.feature.creator.R
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

private val PreviewBoxSize = 200.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SudokuCreatorScreen(
	onNavigateToGameScreen: () -> Unit,
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SudokuCreatorViewModel = koinViewModel(),
) {
	val uiState by viewModel.uiState.collectAsState()

	SudokuCreatorTheme(false) {
		SudokuCreatorContent(
			uiState = uiState,
			onEvent = viewModel::onEvent,
			openDrawer = openDrawer,
			onNavigateToGameScreen = onNavigateToGameScreen,
			modifier = modifier,
		)
	}
}

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalMaterial3ExpressiveApi::class,
	ExperimentalSharedTransitionApi::class,
)
@Composable
private fun SudokuCreatorContent(
	uiState: SudokuCreatorUiState,
	onEvent: (Event) -> Unit,
	openDrawer: () -> Unit,
	onNavigateToGameScreen: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var gameCreationInProgress by rememberSaveable { mutableStateOf(false) }

	if (gameCreationInProgress) {
		val lifecycle = LocalLifecycleOwner.current.lifecycle
		val currentNavigateToGameScreen by rememberUpdatedState(onNavigateToGameScreen)

		LaunchedEffect(uiState, lifecycle) {
			snapshotFlow { uiState }
				.filter {
					it.savedGame != null && it.loadingState != ScreenState.LOADING
				}.flowWithLifecycle(lifecycle).collect {
					gameCreationInProgress = false
					currentNavigateToGameScreen()
				}
		}
	}
	val boardPreviewState =
		rememberBoardPreviewState(uiState.selectedGridSize, uiState.selectedDifficulty)

	Scaffold(
		modifier = modifier,
		topBar = {
			CenterAlignedTopAppBar(
				windowInsets = WindowInsets.displayCutout,
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surfaceContainer,
				),
				title = { },
				navigationIcon = {
					IconButton(onClick = openDrawer) {
						Icon(
							Icons.Default.Menu,
							contentDescription = stringResource(R.string.content_desc_open_nav_menu),
						)
					}
				},
			)
		},
		floatingActionButtonPosition = FabPosition.Center,
		floatingActionButton = {
			AnimatedVisibility(uiState.loadingState == ScreenState.INITIAL) {
				NewGameButton(
					modifier = Modifier.fillMaxWidth(0.8f),
					onClick = {
						onEvent(Event.NewGame)
						gameCreationInProgress = true
					},
				)
			}
		},
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.consumeWindowInsets(innerPadding)
				.imePadding()
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			SharedTransitionLayout {
				AnimatedContent(
					targetState = uiState.loadingState,
					label = "SudokuCreatorScreenTransition",
				) { targetState ->
					when (targetState) {
						ScreenState.INITIAL -> {
							InitialContent(
								uiState = uiState,
								boardPreviewState = boardPreviewState,
								onContinueClick = {
									onEvent(Event.LoadSudoku)
									gameCreationInProgress = true
								},
								onToggleCardClick = { onEvent(Event.ToggleActiveGameCard) },
								onGridSizeChange = { onEvent(Event.ChangeGridSize(it)) },
								onDifficulyChange = { onEvent(Event.ChangeDifficulty(it)) },
								onToggleAdvancedOptions = { onEvent(Event.ToggleAdvancedOptions) },
								onSeedChange = { onEvent(Event.ChangePuzzleSeed(it)) },
								sharedTransitionScope = this@SharedTransitionLayout,
								animatedVisibilityScope = this@AnimatedContent,
								modifier = Modifier
									.fillMaxWidth()
									.sharedBounds(
										rememberSharedContentState("creator_screen_content"),
										animatedVisibilityScope = this@AnimatedContent,
									),
							)
						}

						ScreenState.LOADING -> {
							LoadingContent(
								selectedGridSize = uiState.selectedGridSize,
								sharedTransitionScope = this@SharedTransitionLayout,
								animatedVisibilityScope = this@AnimatedContent,
								modifier = Modifier
									.weight(1f)
									.sharedBounds(
										rememberSharedContentState("creator_screen_content"),
										animatedVisibilityScope = this@AnimatedContent,
									),
							)
						}
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun InitialContent(
	uiState: SudokuCreatorUiState,
	boardPreviewState: BoardPreviewState,
	onContinueClick: () -> Unit,
	onToggleCardClick: () -> Unit,
	onGridSizeChange: (SudokuGridSize) -> Unit,
	onDifficulyChange: (GameDifficulty) -> Unit,
	onToggleAdvancedOptions: () -> Unit,
	onSeedChange: (String) -> Unit,
	sharedTransitionScope: SharedTransitionScope,
	animatedVisibilityScope: AnimatedVisibilityScope,
	modifier: Modifier = Modifier,
) {
	val hasActiveGame by remember(uiState) {
		derivedStateOf {
			uiState.hasActiveGame &&
				uiState.savedGame != null
		}
	}
	val lazyColumnState = rememberLazyListState()
	val focusManager = LocalFocusManager.current

	LazyColumn(
		state = lazyColumnState,
		modifier = modifier
			.fillMaxWidth()
			.pointerInput(Unit) {
				detectTapGestures(
					onTap = {
						focusManager.clearFocus()
					},
				)
			},
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		item {
			AnimatedVisibility(visible = hasActiveGame) {
				ActiveGameCard(
					isExpanded = uiState.activeGameCardExpanded,
					difficulty = uiState.savedGame!!.difficulty,
					gridSize = SudokuGridSize.fromIntSize(uiState.savedGame.grid.gridSize),
					elapsedTime = uiState.savedGame.elapsedTime,
					completed = uiState.savedGame.completed,
					onContinueClick = onContinueClick,
					onToggle = onToggleCardClick,
					modifier = Modifier.padding(LocalPadding.current.small),
				)
			}
		}
		item {
			with(sharedTransitionScope) {
				BoardPreview(
					modifier = Modifier
						.size(PreviewBoxSize)
						.sharedBounds(
							sharedContentState = rememberSharedContentState("board_preview"),
							animatedVisibilityScope = animatedVisibilityScope,
						),
					state = boardPreviewState,
				)
			}
			Spacer(Modifier.height(LocalPadding.current.big))
		}
		item {
			GridSizeSelector(
				options = SudokuGridSize.entries.toPersistentList(),
				selectedSize = uiState.selectedGridSize,
				onCheckedChange = onGridSizeChange,
				modifier = Modifier
					.padding(LocalPadding.current.small)
					.fillMaxWidth(),
			)
		}
		item {
			DifficultySelector(
				options = GameDifficulty.entries.toPersistentList(),
				selectedDifficulty = uiState.selectedDifficulty,
				onCheckedChange = onDifficulyChange,
				modifier = Modifier
					.padding(LocalPadding.current.small)
					.fillMaxWidth(),
			)
		}
		item {
			AdvancedOptions(
				expanded = uiState.advancedOptionsState.expanded,
				onToggle = onToggleAdvancedOptions,
				seed = uiState.advancedOptionsState.seedInput,
				onSeedChange = onSeedChange,
				modifier = Modifier
					.padding(LocalPadding.current.small)
					.fillMaxWidth(),
			)
		}
	}
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun LoadingContent(
	selectedGridSize: SudokuGridSize,
	sharedTransitionScope: SharedTransitionScope,
	animatedVisibilityScope: AnimatedVisibilityScope,
	modifier: Modifier = Modifier,
) {
	with(sharedTransitionScope) {
		Box(
			modifier = modifier.fillMaxSize(),
			contentAlignment = Alignment.Center,
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.padding(horizontal = LocalPadding.current.big)
					.fillMaxWidth(),
			) {
				BoardLoadingIndicator(
					gridSize = selectedGridSize,
					modifier = Modifier
						.size(300.dp)
						.sharedBounds(
							rememberSharedContentState("board_preview"),
							animatedVisibilityScope = animatedVisibilityScope,
						),
				)
				Spacer(Modifier.size(LocalPadding.current.big))

				Text(
					text = stringResource(R.string.generating_puzzle),
					style = MaterialTheme.typography.titleLarge,
					autoSize = TextAutoSize.StepBased(),
					maxLines = 1,
					modifier = Modifier.widthIn(max = 300.dp),
				)
				if (selectedGridSize == SudokuGridSize.SIXTEEN) {
					Spacer(Modifier.size(LocalPadding.current.tiny))
					Text(
						text = stringResource(R.string.generating_big_puzzle_info),
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.widthIn(max = 300.dp),
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun SudokuCreatorScreenPreview() {
	SudokuCreatorTheme {
		SudokuCreatorContent(
			uiState = SudokuCreatorUiState(),
			onEvent = { },
			openDrawer = { },
			onNavigateToGameScreen = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@PreviewLightDark
@Composable
private fun SudokuCreatorScreenActiveGamePreview() {
	val savedGame = Game(
		grid = SudokuGrid(),
		difficulty = GameDifficulty.Medium,
		elapsedTime = 170,
		hintsUsed = 0,
		hintLogs = persistentListOf(),
		completed = false,
		solution = SolutionGrid(intArrayOf(), 0),
	)

	SudokuCreatorTheme {
		SudokuCreatorContent(
			uiState = SudokuCreatorUiState(
				savedGame = savedGame,
				hasActiveGame = true,
			),
			onEvent = { },
			openDrawer = { },
			onNavigateToGameScreen = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@PreviewLightDark
@Composable
private fun SudokuCreatorScreenActiveGameExpandedPreview() {
	val savedGame = Game(
		grid = SudokuGrid(),
		difficulty = GameDifficulty.Medium,
		elapsedTime = 170,
		hintsUsed = 0,
		hintLogs = persistentListOf(),
		completed = false,
		solution = SolutionGrid(intArrayOf(), 0),
	)

	SudokuCreatorTheme {
		SudokuCreatorContent(
			uiState = SudokuCreatorUiState(
				savedGame = savedGame,
				hasActiveGame = true,
				activeGameCardExpanded = true,
				advancedOptionsState = AdvancedOptionsState(
					expanded = true,
				),
			),
			onEvent = { },
			openDrawer = { },
			onNavigateToGameScreen = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@PreviewLightDark
@Composable
private fun SudokuCreatorScreenLoadingPreview() {
	SudokuCreatorTheme {
		SudokuCreatorContent(
			uiState = SudokuCreatorUiState(loadingState = ScreenState.LOADING),
			onEvent = { },
			openDrawer = { },
			onNavigateToGameScreen = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@PreviewLightDark
@Composable
private fun SudokuCreatorScreenLoadingBigPreview() {
	SudokuCreatorTheme {
		SudokuCreatorContent(
			uiState = SudokuCreatorUiState(
				loadingState = ScreenState.LOADING,
				selectedGridSize = SudokuGridSize.SIXTEEN,
			),
			onEvent = { },
			openDrawer = { },
			onNavigateToGameScreen = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}
