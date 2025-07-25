package com.example.feature.creator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.feature.creator.components.DifficultySelector
import com.example.feature.creator.components.HorizontalSelect
import com.example.feature.creator.components.NewGameButton
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudoku.model.SudokuGrid
import com.example.sudokuslayer.feature.creator.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

private val PreviewBoxSize = 200.dp
private const val SELECTS_MAX_WIDTH = 0.8f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SudokuCreatorScreen(
	onNavigateToGameScreen: () -> Unit,
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SudokuCreatorViewModel = koinViewModel(),
) {
	val uiState by viewModel.uiState.collectAsState()

	SudokuCreatorContent(
		uiState = uiState,
		onEvent = viewModel::onEvent,
		openDrawer = openDrawer,
		onNavigateToGameScreen = onNavigateToGameScreen,
		difficultyOptions = viewModel.difficulties,
		gridSizeOptions = viewModel.gridSizeOptions,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SudokuCreatorContent(
	uiState: SudokuCreatorUiState,
	difficultyOptions: PersistentList<String>,
	gridSizeOptions: PersistentList<String>,
	onEvent: (Event) -> Unit,
	openDrawer: () -> Unit,
	onNavigateToGameScreen: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var gameCreationInProgress by rememberSaveable { mutableStateOf(false) }
	val hasActiveGame by remember(uiState) {
		derivedStateOf {
			uiState.hasActiveGame &&
				uiState.savedGame != null
		}
	}

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
			NewGameButton(
				modifier = Modifier.fillMaxWidth(0.8f),
				onClick = {
					onEvent(Event.NewGame)
					gameCreationInProgress = true
				},
			)
		},
	) { innerPadding ->
		Column(
			modifier = Modifier.padding(innerPadding)
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			AnimatedVisibility(visible = hasActiveGame) {
				ActiveGameCard(
					isExpanded = uiState.activeGameCardExpanded,
					difficulty = uiState.savedGame!!.difficulty,
					gridSize = SudokuGridSize.fromIntSize(uiState.savedGame.grid.gridSize),
					elapsedTime = uiState.savedGame.elapsedTime,
					completed = uiState.savedGame.completed,
					onContinueClick = {
						onEvent(Event.LoadSudoku)
						gameCreationInProgress = true
					},
					onToggle = { onEvent(Event.ToggleActiveGameCard) },
					modifier = Modifier.padding(LocalPadding.current.small),
				)
			}
			PreviewBox()
			Spacer(Modifier.height(LocalPadding.current.big))

			Selects(
				gridSizeOptions = gridSizeOptions,
				onGridSizeChange = { onEvent(Event.ChangeGridSize(it)) },
				modifier = Modifier.fillMaxWidth(SELECTS_MAX_WIDTH),
			)
			DifficultySelector(
				options = GameDifficulty.entries.toPersistentList(),
				selectedDifficulty = uiState.selectedDifficulty,
				onCheckedChange = { onEvent(Event.ChangeDifficulty(it.ordinal)) },
				modifier = Modifier.padding(LocalPadding.current.small).fillMaxWidth(),
			)
		}
	}
}

@Composable
private fun PreviewBox() {
	Box(
		contentAlignment = Alignment.Center,
		modifier =
		Modifier
			.size(PreviewBoxSize)
			.background(color = MaterialTheme.colorScheme.error),
	) {
		Text(
			"PREVIEW",
			style = MaterialTheme.typography.displayMedium,
			color = MaterialTheme.colorScheme.onError,
		)
	}
}

@Composable
private fun Selects(
	gridSizeOptions: PersistentList<String>,
	onGridSizeChange: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		HorizontalSelect(
			options = gridSizeOptions,
			onChange = onGridSizeChange,
			modifier = Modifier.fillMaxWidth(),
		)
	}
}

@PreviewLightDark
@Composable
private fun SudokuCreatorScreenPreview() {
	SudokuSlayerTheme {
		SudokuCreatorContent(
			uiState = SudokuCreatorUiState(),
			difficultyOptions = persistentListOf("Easy", "Medium", "Hard"),
			gridSizeOptions = persistentListOf("4x4", "9x9", "16x16"),
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
	)

	SudokuSlayerTheme {
		SudokuCreatorContent(
			uiState = SudokuCreatorUiState(
				savedGame = savedGame,
				hasActiveGame = true,
			),
			difficultyOptions = persistentListOf("Easy", "Medium", "Hard"),
			gridSizeOptions = persistentListOf("4x4", "9x9", "16x16"),
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
	)

	SudokuSlayerTheme {
		SudokuCreatorContent(
			uiState = SudokuCreatorUiState(
				savedGame = savedGame,
				hasActiveGame = true,
				activeGameCardExpanded = true,
			),
			difficultyOptions = persistentListOf("Easy", "Medium", "Hard"),
			gridSizeOptions = persistentListOf("4x4", "9x9", "16x16"),
			onEvent = { },
			openDrawer = { },
			onNavigateToGameScreen = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}
