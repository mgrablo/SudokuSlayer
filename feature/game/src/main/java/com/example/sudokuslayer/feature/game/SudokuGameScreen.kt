package com.example.sudokuslayer.feature.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SheetValue
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
import androidx.compose.ui.tooling.preview.Devices.AUTOMOTIVE_1024p
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.composables.core.rememberDialogState
import com.example.sudoku.model.SolutionGrid
import com.example.sudoku.model.SudokuGrid
import com.example.sudokuslayer.domain.core.Game
import com.example.sudokuslayer.domain.core.GameDifficulty
import com.example.sudokuslayer.domain.core.SudokuGridSize
import com.example.sudokuslayer.feature.game.SudokuGameViewModel.Event
import com.example.sudokuslayer.feature.game.components.GameTopBar
import com.example.sudokuslayer.feature.game.components.HintBottomSheetScaffold
import com.example.sudokuslayer.feature.game.components.HintsDialog
import com.example.sudokuslayer.feature.game.components.KeyPad
import com.example.sudokuslayer.feature.game.components.PostGameActions
import com.example.sudokuslayer.feature.game.components.ResetDialog
import com.example.sudokuslayer.feature.game.components.VictoryDialog
import com.example.sudokuslayer.feature.game.components.board.BreathingBoardLoadingIndicator
import com.example.sudokuslayer.feature.game.components.board.SudokuBoard
import com.example.sudokuslayer.feature.game.model.GameState
import com.example.sudokuslayer.feature.game.model.SudokuGameUiState
import com.example.sudokuslayer.feature.game.theme.GameSharedElementKey
import com.example.sudokuslayer.feature.game.theme.SudokuGameTheme
import com.example.sudokuslayer.feature.uicore.theme.LocalPadding
import com.example.sudokuslayer.feature.uicore.theme.LocalSharedTransitionScope
import com.example.sudokuslayer.feature.uicore.theme.SharedElementKey
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
	game: Game?,
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
	var resetDialogVisible by remember { mutableStateOf(false) }
	var hintsDialogVisible by remember { mutableStateOf(false) }
	val victoryDialogState = rememberDialogState(false)

	val scaffoldState =
		rememberBottomSheetScaffoldState(
			bottomSheetState =
			rememberStandardBottomSheetState(
				initialValue = SheetValue.Hidden,
				skipHiddenState = false,
			),
		)

	LaunchedEffect(uiState.gameState) {
		if (uiState.gameState == GameState.VICTORY) {
			victoryDialogState.visible = true
		}
	}

	SharedTransitionLayout {
		HintBottomSheetScaffold(
			modifier = modifier,
			sheetScaffoldState = scaffoldState,
			snackbarState = uiState.snackbarState,
			hintLogs = game?.hintLogs ?: persistentListOf(),
			showNextHint = game?.hintLogs?.lastOrNull()?.isRevealed ?: true,
			explainHintClick = { onEvent(Event.ExplainHint) },
			nextHintClick = { onEvent(Event.ProvideHint) },
			onHighlightCellClick = { onEvent(Event.HighlightHintCells(it)) },
			onShowMistakes = { onEvent(Event.ShowMistakes) },
			onDismissSnackbar = { onEvent(Event.DismissSnackbar) },
			topBar = {
				GameTopBar(
					showTimer = uiState.timerVisible,
					elapsedTime = elapsedTime,
					isVictory = uiState.gameState == GameState.VICTORY,
					onMenuClick = openDrawer,
					onSummaryClick = { victoryDialogState.visible = true },
				)
			},
		) { innerPadding ->
			val sharedLoadingIndicatorModifier = with(LocalSharedTransitionScope.current) {
				Modifier.sharedElement(
					rememberSharedContentState(SharedElementKey.BoardLoadingIndicator),
					animatedVisibilityScope = LocalNavAnimatedContentScope.current,
				)
			}

			AnimatedContent(
				targetState = uiState.gameState == GameState.LOADING || game == null,
				modifier = Modifier.padding(innerPadding),
			) { loading ->
				if (loading) {
					Column(
						verticalArrangement = Arrangement.Center,
						horizontalAlignment = Alignment.CenterHorizontally,
						modifier = Modifier
							.fillMaxSize()
							.padding(LocalPadding.current.large),
					) {
						BreathingBoardLoadingIndicator(
							sudokuGridSize = uiState.sudokuGridSize,
							modifier = Modifier
								.size(300.dp)
								.aspectRatio(1f)
								.sharedBounds(
									sharedContentState = rememberSharedContentState(
										GameSharedElementKey.Board,
									),
									animatedVisibilityScope = this@AnimatedContent,
								)
								.then(sharedLoadingIndicatorModifier),
						)
					}
				} else {
					// Should not happen, but used to not use null-checks in the composables
					if (game == null) return@AnimatedContent

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
						isVisible = resetDialogVisible,
						onConfirmClick = {
							onEvent(Event.ResetGame)
							onEvent(Event.ResetNotes)
							resetDialogVisible = false
						},
						onDismissClick = { resetDialogVisible = false },
						onClearNotesClick = {
							onEvent(Event.ResetNotes)
							resetDialogVisible = false
						},
					)

					HintsDialog(
						isVisible = hintsDialogVisible,
						onDismissRequest = { hintsDialogVisible = false },
						onHintClick = {
							onEvent(Event.ProvideHint)
							hintsDialogVisible = false
							scope.launch {
								scaffoldState.bottomSheetState.expand()
							}
						},
						onFillNotesClick = {
							onEvent(Event.HintFillNotes)
							hintsDialogVisible = false
						},
						onFindMistakesClick = {
							onEvent(Event.FindMistakes)
							hintsDialogVisible = false
						},
						onShowLogsClick = {
							hintsDialogVisible = false
							scope.launch {
								scaffoldState.bottomSheetState.expand()
							}
						},
					)
					val actions: @Composable (Modifier) -> Unit = {
						GameActions(
							gameState = uiState.gameState,
							remainingDigitCounts = remainingDigitCounts,
							uiState = uiState,
							gridSize = game.grid.gridSize,
							summaryOpen = victoryDialogState.visible,
							onEvent = onEvent,
							onHintClick = { hintsDialogVisible = true },
							onResetClick = { resetDialogVisible = true },
							onPlayAgainClick = onPlayAgainClick,
							onNavigateToInsightsClick = onNavigateToInsightsClick,
							onViewSummary = { victoryDialogState.visible = true },
							modifier = it,
						)
					}

					if (isPortrait) {
						Column(
							modifier = Modifier.fillMaxSize(),
							horizontalAlignment = Alignment.CenterHorizontally,
						) {
							SudokuBoard(
								sudoku = game.grid,
								focusedCells = uiState.focusedCells,
								onCellClick = { row, col -> onEvent(Event.SelectCell(row, col)) },
								onCellLongClick = { row, col ->
									onEvent(
										Event.CellLongClick(
											row,
											col,
										),
									)
								},
								animateInitialReveal = uiState.gameState == GameState.PLAYING,
								modifier = Modifier
									.weight(1f)
									.aspectRatio(1f)
									.sharedBounds(
										sharedContentState = rememberSharedContentState(
											GameSharedElementKey.Board,
										),
										animatedVisibilityScope = this@AnimatedContent,
									)
									.then(sharedLoadingIndicatorModifier),
							)
							actions(Modifier.weight(1f))
						}
					} else {
						Row(
							modifier = Modifier.fillMaxSize(),
							horizontalArrangement = Arrangement.Center,
							verticalAlignment = Alignment.CenterVertically,
						) {
							SudokuBoard(
								sudoku = game.grid,
								focusedCells = uiState.focusedCells,
								onCellClick = { row, col -> onEvent(Event.SelectCell(row, col)) },
								onCellLongClick = { row, col ->
									onEvent(
										Event.CellLongClick(
											row,
											col,
										),
									)
								},
								animateInitialReveal = uiState.gameState == GameState.PLAYING,
								modifier = Modifier
									.weight(1f)
									.aspectRatio(1f)
									.sharedBounds(
										sharedContentState = rememberSharedContentState(
											GameSharedElementKey.Board,
										),
										animatedVisibilityScope = this@AnimatedContent,
									)
									.then(sharedLoadingIndicatorModifier),
							)
							actions(Modifier.weight(1f))
						}
					}
				}
			}
		}
	}
}

@Composable
private fun GameActions(
	gameState: GameState,
	remainingDigitCounts: PersistentMap<Int, Int>,
	uiState: SudokuGameUiState,
	gridSize: Int,
	summaryOpen: Boolean,
	onEvent: (Event) -> Unit,
	onHintClick: () -> Unit,
	onResetClick: () -> Unit,
	onPlayAgainClick: () -> Unit,
	onNavigateToInsightsClick: () -> Unit,
	onViewSummary: () -> Unit,
	modifier: Modifier = Modifier,
) {
	AnimatedContent(
		targetState = gameState,
		modifier = modifier,
		contentAlignment = Alignment.Center,
	) { state ->
		when (state) {
			GameState.LOADING -> Unit
			GameState.PLAYING -> {
				KeyPad(
					remainingDigitCounts = remainingDigitCounts,
					onNumberClick = { onEvent(Event.InputNumber(it)) },
					onNumberLongClick = { onEvent(Event.LongInputNumber(it)) },
					onClearClick = { onEvent(Event.ClearCell) },
					onUndoClick = { onEvent(Event.Undo) },
					onRedoClick = { onEvent(Event.Redo) },
					onHintClick = onHintClick,
					onResetClick = onResetClick,
					onSwitchInputMode = { onEvent(Event.SwitchInputMode(it)) },
					noteMode = uiState.isInNoteMode,
					gridSize = gridSize,
					isLeftHandMode = uiState.isLeftHandMode,
					showActionButtonsOnTop = uiState.showActionButtonsOnTop,
				)
			}

			GameState.VICTORY -> {
				PostGameActions(
					onViewSummary = onViewSummary,
					onPlayAgainClick = onPlayAgainClick,
					onShowInsights = onNavigateToInsightsClick,
					summaryOpen = summaryOpen,
					modifier = Modifier
						.fillMaxHeight(),
				)
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

@PreviewLightDark
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
	val gridRows = mutableListOf<IntArray>()
	repeat(gridSize) {
		gridRows += IntArray(gridSize) { Random.nextInt(0, gridSize + 1) }
	}
	return SudokuGrid.fromIntArray(gridRows, gridSize)
}
