package io.github.mgrablo.sudokuslayer.feature.creator

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import io.github.mgrablo.sudokucore.model.SolutionGrid
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokuslayer.domain.core.Game
import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.feature.creator.SudokuCreatorViewModel.Event
import io.github.mgrablo.sudokuslayer.feature.creator.components.ActiveGameCard
import io.github.mgrablo.sudokuslayer.feature.creator.components.AdvancedOptions
import io.github.mgrablo.sudokuslayer.feature.creator.components.DifficultySelector
import io.github.mgrablo.sudokuslayer.feature.creator.components.GridSizeSelector
import io.github.mgrablo.sudokuslayer.feature.creator.components.NewGameButton
import io.github.mgrablo.sudokuslayer.feature.creator.components.preview.BoardLoadingIndicator
import io.github.mgrablo.sudokuslayer.feature.creator.components.preview.BoardPreview
import io.github.mgrablo.sudokuslayer.feature.creator.components.preview.BoardPreviewState
import io.github.mgrablo.sudokuslayer.feature.creator.components.preview.rememberBoardPreviewState
import io.github.mgrablo.sudokuslayer.feature.creator.theme.CreatorSharedElementKey
import io.github.mgrablo.sudokuslayer.feature.creator.theme.SudokuCreatorTheme
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalSharedTransitionScope
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalSudokuTypography
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SharedElementKey
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

private val PreviewBoxSize = 200.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SudokuCreatorScreen(
	onNavigateToGameScreen: (SudokuGridSize) -> Unit,
	openDrawer: () -> Unit,
	navAnimatedContentScope: AnimatedVisibilityScope,
	modifier: Modifier = Modifier,
	viewModel: SudokuCreatorViewModel = koinViewModel(),
) {
	val uiState by viewModel.uiState.collectAsState()

	SudokuCreatorTheme(false) {
		SudokuCreatorContent(
			navAnimatedContentScope = navAnimatedContentScope,
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
	navAnimatedContentScope: AnimatedVisibilityScope,
	onEvent: (Event) -> Unit,
	openDrawer: () -> Unit,
	onNavigateToGameScreen: (SudokuGridSize) -> Unit,
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
					currentNavigateToGameScreen(uiState.selectedGridSize)
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
								animatedVisibilityScope = this,
								navAnimatedContentScope = navAnimatedContentScope,
								modifier = Modifier
									.fillMaxWidth()
									.sharedBounds(
										rememberSharedContentState(CreatorSharedElementKey.CreatorScreenContent),
										animatedVisibilityScope = this@AnimatedContent,
									),
							)
						}

						ScreenState.LOADING -> {
							LoadingContent(
								selectedGridSize = uiState.selectedGridSize,
								sharedTransitionScope = this@SharedTransitionLayout,
								animatedVisibilityScope = this,
								navAnimatedContentScope = navAnimatedContentScope,
								modifier = Modifier
									.weight(1f)
									.sharedBounds(
										rememberSharedContentState(CreatorSharedElementKey.CreatorScreenContent),
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
	navAnimatedContentScope: AnimatedVisibilityScope,
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
		verticalArrangement = Arrangement.spacedBy(LocalPadding.current.small),
		contentPadding = PaddingValues(LocalPadding.current.small)
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
					modifier = Modifier,
				)
			}
		}
		item {
			with(sharedTransitionScope) {
				val sharedLoadingIndicatorModifier = with(LocalSharedTransitionScope.current) {
					Modifier.sharedElement(
						rememberSharedContentState(SharedElementKey.BoardLoadingIndicator),
						animatedVisibilityScope = navAnimatedContentScope,
					)
				}
				BoardPreview(
					modifier = Modifier
						.size(PreviewBoxSize)
						.sharedBounds(
							rememberSharedContentState(CreatorSharedElementKey.BoardPreview),
							animatedVisibilityScope = animatedVisibilityScope,
						)
						.then(sharedLoadingIndicatorModifier),
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
					.fillMaxWidth(),
			)
		}
		item {
			DifficultySelector(
				options = GameDifficulty.entries.toPersistentList(),
				selectedDifficulty = uiState.selectedDifficulty,
				onCheckedChange = onDifficulyChange,
				modifier = Modifier
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
					.fillMaxWidth(),
			)
		}
	}
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingContent(
	selectedGridSize: SudokuGridSize,
	sharedTransitionScope: SharedTransitionScope,
	animatedVisibilityScope: AnimatedVisibilityScope,
	navAnimatedContentScope: AnimatedVisibilityScope,
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
				val sharedLoadingIndicatorModifier =
					with(LocalSharedTransitionScope.current) {
						Modifier.sharedElement(
							rememberSharedContentState(SharedElementKey.BoardLoadingIndicator),
							animatedVisibilityScope = navAnimatedContentScope,
						)
					}

				Text(
					text = stringResource(R.string.good_luck),
					autoSize = TextAutoSize.StepBased(),
					maxLines = 1,
					style = LocalSudokuTypography.current.displayLargeEmphasized,
				)
				Spacer(Modifier.height(LocalPadding.current.large))
				BoardLoadingIndicator(
					gridSize = selectedGridSize,
					modifier = Modifier
						.size(300.dp)
						.sharedBounds(
							rememberSharedContentState(CreatorSharedElementKey.BoardPreview),
							animatedVisibilityScope = animatedVisibilityScope,
						)
						.then(sharedLoadingIndicatorModifier),
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

//region Previews
@Composable
private fun SudokuCreatorScreenPreview(
	uiState: SudokuCreatorUiState,
) {
	SudokuCreatorTheme {
		AnimatedVisibility(visible = true) {
			SudokuCreatorContent(
				uiState = uiState,
				onEvent = { },
				openDrawer = { },
				onNavigateToGameScreen = { },
				navAnimatedContentScope = this,
				modifier = Modifier.fillMaxSize(),
			)
		}
	}
}

@Preview(name = "Initial", showBackground = true)
@Preview(name = "Initial Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SudokuCreatorScreenInitialPreview() {
	SudokuCreatorScreenPreview(uiState = SudokuCreatorUiState())
}

@Preview(name = "Active Game", showBackground = true)
@Preview(name = "Active Game Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
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

	SudokuCreatorScreenPreview(
		uiState = SudokuCreatorUiState(
			savedGame = savedGame,
			hasActiveGame = true,
		),
	)
}

@Preview(name = "Active Game Expanded", showBackground = true)
@Preview(
	name = "Active Game Expanded Dark",
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES,
)
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

	SudokuCreatorScreenPreview(
		uiState = SudokuCreatorUiState(
			savedGame = savedGame,
			hasActiveGame = true,
			activeGameCardExpanded = true,
			advancedOptionsState = AdvancedOptionsState(
				expanded = true,
			),
		),
	)
}

@Preview(name = "Loading", showBackground = true)
@Preview(name = "Loading Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SudokuCreatorScreenLoadingPreview() {
	SudokuCreatorScreenPreview(
		uiState = SudokuCreatorUiState(loadingState = ScreenState.LOADING),
	)
}

@Preview(name = "Loading Big", showBackground = true)
@Preview(name = "Loading Big Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SudokuCreatorScreenLoadingBigPreview() {
	SudokuCreatorScreenPreview(
		uiState = SudokuCreatorUiState(
			loadingState = ScreenState.LOADING,
			selectedGridSize = SudokuGridSize.SIXTEEN,
		),
	)
}
//endregion
