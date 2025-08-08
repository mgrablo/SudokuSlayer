package com.example.feature.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices.AUTOMOTIVE_1024p
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.core.rememberDialogState
import com.example.domain.core.Game
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.feature.game.SudokuGameViewModel.Event
import com.example.feature.game.components.HintBottomSheetScaffold
import com.example.feature.game.components.HintsDialog
import com.example.feature.game.components.KeyPad
import com.example.feature.game.components.PostGameActions
import com.example.feature.game.components.ResetDialog
import com.example.feature.game.components.SudokuBoard
import com.example.feature.game.components.TimerDisplay
import com.example.feature.game.components.VictoryDialog
import com.example.feature.game.model.GameState
import com.example.feature.game.model.SudokuGameUiState
import com.example.feature.game.theme.SudokuGameTheme
import com.example.sudoku.model.SolutionGrid
import com.example.sudoku.model.SudokuGrid
import com.example.sudokuslayer.feature.game.R
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SudokuGameScreen(
	openDrawer: () -> Unit,
	onPlayAgainClick: () -> Unit,
	onNavigateToInsightsClick: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SudokuGameViewModel = koinViewModel(),
) {
	val elapsedTime by viewModel.elapsedTime.collectAsStateWithLifecycle()
	val uiState: SudokuGameUiState by viewModel.uiState.collectAsStateWithLifecycle()
	val game by viewModel.game.collectAsStateWithLifecycle()
	val remainingDigitCounts by viewModel.remainingDigitCounts.collectAsStateWithLifecycle()

	SudokuGameTheme(useSudokuSlayerTheme = false) {
		LifecycleResumeEffect(Unit) {
			viewModel.onEvent(Event.StartTimer)
			onPauseOrDispose {
				viewModel.onEvent(Event.StopTimer)
			}
		}

		SudokuGameScreenContent(
			uiState = uiState,
			game = game,
			remainingDigitCounts = remainingDigitCounts,
			onEvent = {
				viewModel.onEvent(it)
			},
			onPlayAgainClick = onPlayAgainClick,
			modifier = modifier.fillMaxSize(),
			elapsedTime = { elapsedTime },
			onNavigateToInsightsClick = onNavigateToInsightsClick,
			openDrawer = openDrawer,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SudokuGameScreenContent(
	uiState: SudokuGameUiState,
	game: Game,
	remainingDigitCounts: PersistentMap<Int, Int>,
	onEvent: (Event) -> Unit,
	elapsedTime: () -> Long,
	openDrawer: () -> Unit,
	onPlayAgainClick: () -> Unit,
	onNavigateToInsightsClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val windowInfo = LocalWindowInfo.current
	val containerSize = windowInfo.containerSize
	val isPortrait = containerSize.height > containerSize.width

	val scope = rememberCoroutineScope()
	var resetDialogState by remember { mutableStateOf(false) }
	var hintsDialogState by remember { mutableStateOf(false) }
	val victoryDialogState = rememberDialogState(false)

	LaunchedEffect(uiState.gameState) {
		if (uiState.gameState == GameState.VICTORY) {
			victoryDialogState.visible = true
		}
	}

	val scaffoldState =
		rememberBottomSheetScaffoldState(
			bottomSheetState =
			rememberStandardBottomSheetState(
				initialValue = SheetValue.Hidden,
				skipHiddenState = false,
			),
		)

	VictoryDialog(
		dialogState = victoryDialogState,
		timeSpent = elapsedTime(),
		difficulty = game.difficulty,
		gridSize = SudokuGridSize.fromIntSize(game.grid.gridSize),
		hintsUsed = game.hintsUsed,
		bestTime = uiState.currentBestTime,
		isNewBest = uiState.isNewBestTime,
		onDismissRequest = {
			victoryDialogState.visible = false
		},
	)

	ResetDialog(
		isVisible = resetDialogState,
		onConfirmClick = {
			onEvent(Event.ResetGame)
			onEvent(Event.ResetNotes)
			resetDialogState = false
		},
		onDismissClick = { resetDialogState = false },
		onClearNotesClick = {
			onEvent(Event.ResetNotes)
			resetDialogState = false
		},
	)

	HintsDialog(
		isVisible = hintsDialogState,
		onDismissRequest = { hintsDialogState = false },
		onHintClick = {
			onEvent(Event.ProvideHint)
			hintsDialogState = false
			scope.launch {
				scaffoldState.bottomSheetState.expand()
			}
		},
		onFillNotesClick = {
			onEvent(Event.HintFillNotes)
			hintsDialogState = false
		},
		onShowLogsClick = {
			hintsDialogState = false
			scope.launch {
				scaffoldState.bottomSheetState.expand()
			}
		},
	)

	HintBottomSheetScaffold(
		modifier = modifier,
		sheetScaffoldState = scaffoldState,
		hintLogs = game.hintLogs,
		showNextHint = game.hintLogs.lastOrNull()?.isRevealed ?: true,
		explainHintClick = { onEvent(Event.ExplainHint) },
		nextHintClick = { onEvent(Event.ProvideHint) },
		onHighlightCellClick = { onEvent(Event.HighlightHintCells(it)) },
		topBar = {
			CenterAlignedTopAppBar(
				windowInsets = WindowInsets.displayCutout,
				title = {
					if (uiState.timerVisible) {
						TimerDisplay(
							elapsedTime = elapsedTime(),
						)
					}
				},
				colors =
				TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surfaceContainer,
				),
				navigationIcon = {
					IconButton(onClick = { openDrawer() }) {
						Icon(Icons.Default.Menu, "")
					}
				},
				actions = {
					if (uiState.gameState == GameState.VICTORY) {
						IconButton(
							onClick = {
								victoryDialogState.visible = true
							},
							modifier = Modifier.semantics {
								this.onClick(label = "View summary", action = null)
							},
						) {
							Icon(
								painter = painterResource(R.drawable.trophy),
								contentDescription = "View summary",
							)
						}
					}
				},
			)
		},
	) { innerPadding ->
		if (uiState.gameState == GameState.LOADING) {
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier.fillMaxSize(),
			) {
				CircularWavyProgressIndicator()
			}
		} else {
			if (isPortrait) {
				Column(
					modifier =
					Modifier
						.fillMaxSize(),
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					SudokuBoard(
						sudoku = game.grid,
						focusedCells = uiState.focusedCells,
						onCellClick = { row, col -> onEvent(Event.SelectCell(row, col)) },
						onCellLongClick = { row, col -> onEvent(Event.CellLongClick(row, col)) },
						modifier = Modifier.weight(1f),
					)
					AnimatedContent(
						targetState = uiState.gameState,
						contentAlignment = Alignment.Center,
						modifier = Modifier.weight(1f),
					) { state ->
						when (state) {
							GameState.LOADING -> {}
							GameState.PLAYING -> {
								KeyPad(
									remainingDigitCounts = remainingDigitCounts,
									onNumberClick = { onEvent(Event.InputNumber(it)) },
									onNumberLongClick = { onEvent(Event.LongInputNumber(it)) },
									onClearClick = { onEvent(Event.ClearCell) },
									onUndoClick = { onEvent(Event.Undo) },
									onRedoClick = { onEvent(Event.Redo) },
									onHintClick = { hintsDialogState = true },
									onResetClick = { resetDialogState = true },
									onSwitchInputMode = { onEvent(Event.SwitchInputMode(it)) },
									noteMode = uiState.isInNoteMode,
									gridSize = game.grid.gridSize,
									isLeftHandMode = uiState.isLeftHandMode,
									showActionButtonsOnTop = uiState.showActionButtonsOnTop,
									modifier = Modifier.weight(1f),
								)
							}

							GameState.VICTORY -> {
								PostGameActions(
									onViewSummary = {
										victoryDialogState.visible = true
									},
									onPlayAgainClick = onPlayAgainClick,
									onShowInsights = onNavigateToInsightsClick,
									summaryOpen = victoryDialogState.visible,
									modifier = Modifier.weight(1f),
								)
							}
						}
					}
				}
			} else {
				Row(
					modifier = Modifier,
					horizontalArrangement = Arrangement.Center,
					verticalAlignment = Alignment.CenterVertically,
				) {
					SudokuBoard(
						sudoku = game.grid,
						focusedCells = uiState.focusedCells,
						onCellClick = { row, col -> onEvent(Event.SelectCell(row, col)) },
						onCellLongClick = { row, col -> onEvent(Event.CellLongClick(row, col)) },
						modifier = Modifier.weight(1f),
					)
					AnimatedContent(
						targetState = uiState.gameState,
						modifier = Modifier.weight(0.8f),
						contentAlignment = Alignment.Center,
					) { state ->
						when (state) {
							GameState.LOADING -> {}
							GameState.PLAYING -> {
								KeyPad(
									remainingDigitCounts = remainingDigitCounts,
									onNumberClick = { onEvent(Event.InputNumber(it)) },
									onNumberLongClick = { onEvent(Event.LongInputNumber(it)) },
									onClearClick = { onEvent(Event.ClearCell) },
									onUndoClick = { onEvent(Event.Undo) },
									onRedoClick = { onEvent(Event.Redo) },
									onHintClick = { hintsDialogState = true },
									onResetClick = { resetDialogState = true },
									onSwitchInputMode = { onEvent(Event.SwitchInputMode(it)) },
									noteMode = uiState.isInNoteMode,
									gridSize = game.grid.gridSize,
									isLeftHandMode = uiState.isLeftHandMode,
									showActionButtonsOnTop = uiState.showActionButtonsOnTop,
									modifier = Modifier.weight(1f).fillMaxHeight(),
								)
							}

							GameState.VICTORY -> {
								PostGameActions(
									onViewSummary = {
										victoryDialogState.visible = true
									},
									onPlayAgainClick = onPlayAgainClick,
									onShowInsights = onNavigateToInsightsClick,
									summaryOpen = victoryDialogState.visible,
									modifier = Modifier.weight(1f).fillMaxHeight(),
								)
							}
						}
					}
				}
			}
		}
	}
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun SudokuGameScreenPreview() {
	SudokuGameTheme {
		SudokuGameScreenContent(
			uiState = SudokuGameUiState(
				selectedCell = 1 to 1,
				gameState = GameState.VICTORY,
			),
			game =
			Game(
				grid = createFilledSudokuGrid(9),
				elapsedTime = 0,
				hintLogs = persistentListOf(),
				hintsUsed = 0,
				difficulty = GameDifficulty.Easy,
				solution = SolutionGrid(intArrayOf(), 0),
			),
			remainingDigitCounts = persistentMapOf(1 to 1, 2 to 1, 3 to 1, 4 to 1),
			onEvent = {},
			elapsedTime = { 1 },
			openDrawer = {},
			onPlayAgainClick = { },
			onNavigateToInsightsClick = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@Preview
@Composable
private fun SudokuGameScreenSixteenPreview() {
	SudokuGameTheme {
		SudokuGameScreenContent(
			uiState = SudokuGameUiState(),
			game =
			Game(
				grid = createFilledSudokuGrid(16),
				elapsedTime = 0,
				hintLogs = persistentListOf(),
				hintsUsed = 0,
				difficulty = GameDifficulty.Easy,
				solution = SolutionGrid(intArrayOf(), 0),
			),
			remainingDigitCounts = persistentMapOf(1 to 1, 2 to 1, 3 to 1, 4 to 1),
			onEvent = {},
			elapsedTime = { 1 },
			openDrawer = {},
			onPlayAgainClick = { },
			onNavigateToInsightsClick = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@Preview(
	device = AUTOMOTIVE_1024p,
)
@Composable
private fun SudokuGameScreenFourPreview() {
	SudokuGameTheme {
		SudokuGameScreenContent(
			uiState =
			SudokuGameUiState(
				isLeftHandMode = true,
				gameState = GameState.PLAYING,
			),
			game =
			Game(
				grid = createFilledSudokuGrid(4),
				elapsedTime = 0,
				hintLogs = persistentListOf(),
				hintsUsed = 0,
				difficulty = GameDifficulty.Easy,
				solution = SolutionGrid(intArrayOf(), 0),
			),
			remainingDigitCounts = persistentMapOf(1 to 1, 2 to 1, 3 to 1, 4 to 1),
			onEvent = {},
			elapsedTime = { 1 },
			openDrawer = {},
			onPlayAgainClick = { },
			onNavigateToInsightsClick = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

private fun createFilledSudokuGrid(gridSize: Int): SudokuGrid {
	val list = mutableListOf<IntArray>()
	repeat(gridSize) {
		list += IntArray(gridSize) { Random.nextInt(0, gridSize + 1) }
	}
	return SudokuGrid.fromIntArray(list, gridSize)
}
