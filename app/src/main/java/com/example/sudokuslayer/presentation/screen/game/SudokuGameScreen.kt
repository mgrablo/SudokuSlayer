package com.example.sudokuslayer.presentation.screen.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.core.Game
import com.example.domain.core.GameDifficulty
import com.example.sudoku.model.SudokuGrid
import com.example.sudokuslayer.presentation.screen.game.SudokuGameViewModel.Event
import com.example.sudokuslayer.presentation.screen.game.components.HintBottomSheetScaffold
import com.example.sudokuslayer.presentation.screen.game.components.HintsDialog
import com.example.sudokuslayer.presentation.screen.game.components.KeyPad
import com.example.sudokuslayer.presentation.screen.game.components.ResetDialog
import com.example.sudokuslayer.presentation.screen.game.components.SudokuBoard
import com.example.sudokuslayer.presentation.screen.game.components.TimerDisplay
import com.example.sudokuslayer.presentation.screen.game.components.VictoryDialog
import com.example.sudokuslayer.presentation.screen.game.model.GameState
import com.example.sudokuslayer.presentation.screen.game.model.SudokuGameUiState
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme
import kotlin.random.Random
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuGameScreen(
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SudokuGameViewModel = koinViewModel()
) {
	val elapsedTime by viewModel.elapsedTime.collectAsStateWithLifecycle()
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val game by viewModel.game.collectAsStateWithLifecycle()
	SudokuGameContent(
		uiState = uiState,
		game = game,
		onEvent = {
			viewModel.onEvent(it)
		},
		modifier = modifier,
		elapsedTime = { elapsedTime },
		openDrawer = openDrawer,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuGameContent(
	uiState: SudokuGameUiState,
	game: Game,
	onEvent: (Event) -> Unit,
	elapsedTime: () -> Long,
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier
) {
	val configuration = LocalConfiguration.current
	val isPortrait = configuration.screenHeightDp > configuration.screenWidthDp

	val scope = rememberCoroutineScope()
	var resetDialogState by remember { mutableStateOf(false) }
	var hintsDialogState by remember { mutableStateOf(false) }

	val scaffoldState =
		rememberBottomSheetScaffoldState(
			bottomSheetState =
			rememberStandardBottomSheetState(
				initialValue = SheetValue.Hidden,
				skipHiddenState = false,
			),
		)

	VictoryDialog(
		isVisible = uiState.gameState == GameState.VICTORY,
		onDismissRequest = { onEvent(Event.DismissVictoryDialog) },
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
		sheetScaffoldState = scaffoldState,
		hintLogs = game.hintLogs,
		showNextHint = uiState.lastHint == null,
		explainHintClick = { onEvent(Event.ExplainHint) },
		nextHintClick = { onEvent(Event.ProvideHint) },
		topBar = {
			CenterAlignedTopAppBar(
				windowInsets = WindowInsets.displayCutout,
				title = {
					TimerDisplay(
						elapsedTime = { elapsedTime() },
						onPause = { onEvent(Event.StopTimer) },
					)
				},
				colors =
				TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.surfaceContainer,
				),
				navigationIcon = {
					IconButton(onClick = { openDrawer() }) {
						Icon(Icons.Default.Menu, "")
					}
				},
			)
		},
	) { innerPadding ->
		if (uiState.gameState == GameState.LOADING) {
			CircularProgressIndicator()
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
						onCellClick = { row, col -> onEvent(Event.SelectCell(row, col)) },
						modifier = Modifier.weight(1f),
					)
					KeyPad(
						onNumberClick = { onEvent(Event.InputNumber(it)) },
						onClearClick = { onEvent(Event.ClearCell) },
						onUndoClick = { onEvent(Event.Undo) },
						onRedoClick = { onEvent(Event.Redo) },
						onHintClick = { hintsDialogState = true },
						onShowMistakesClick = { onEvent(Event.ShowMistakes) },
						onResetClick = { resetDialogState = true },
						onSwitchInputMode = { onEvent(Event.SwitchInputMode) },
						noteMode = uiState.isInNoteMode,
						gridSize = game.grid.gridSize,
						isLeftHandMode = uiState.isLeftHandMode,
						showActionButtonsOnTop = uiState.showActionButtonsOnTop,
						modifier = Modifier.weight(1f),
					)
				}
			} else {
				Row {
					SudokuBoard(
						sudoku = game.grid,
						onCellClick = { row, col -> onEvent(Event.SelectCell(row, col)) },
						modifier = Modifier.weight(1f),
					)
					KeyPad(
						onNumberClick = { onEvent(Event.InputNumber(it)) },
						onClearClick = { onEvent(Event.ClearCell) },
						onUndoClick = { onEvent(Event.Undo) },
						onRedoClick = { onEvent(Event.Redo) },
						onHintClick = { hintsDialogState = true },
						onShowMistakesClick = { onEvent(Event.ShowMistakes) },
						onResetClick = { resetDialogState = true },
						onSwitchInputMode = { onEvent(Event.SwitchInputMode) },
						noteMode = uiState.isInNoteMode,
						gridSize = game.grid.gridSize,
						isLeftHandMode = uiState.isLeftHandMode,
						showActionButtonsOnTop = uiState.showActionButtonsOnTop,
						modifier = Modifier.weight(1f),
					)
				}
			}
		}
	}
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun SudokuGameScreenPreview() {
	SudokuSlayerTheme {
		SudokuGameContent(
			uiState = SudokuGameUiState(),
			game =
			Game(
				grid = createFilledSudokuGrid(9),
				elapsedTime = 0,
				hintLogs = persistentListOf(),
				hintsUsed = 0,
				difficulty = GameDifficulty.Easy,
			),
			onEvent = {},
			elapsedTime = { 1 },
			openDrawer = {},
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@Preview
@Composable
private fun SudokuGameScreenSixteenPreview() {
	SudokuSlayerTheme {
		SudokuGameContent(
			uiState = SudokuGameUiState(),
			game =
			Game(
				grid = createFilledSudokuGrid(16),
				elapsedTime = 0,
				hintLogs = persistentListOf(),
				hintsUsed = 0,
				difficulty = GameDifficulty.Easy,
			),
			onEvent = {},
			elapsedTime = { 1 },
			openDrawer = {},
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@Preview
@Composable
private fun SudokuGameScreenFourPreview() {
	SudokuSlayerTheme {
		SudokuGameContent(
			uiState =
			SudokuGameUiState(
				isLeftHandMode = true,
			),
			game =
			Game(
				grid = createFilledSudokuGrid(4),
				elapsedTime = 0,
				hintLogs = persistentListOf(),
				hintsUsed = 0,
				difficulty = GameDifficulty.Easy,
			),
			onEvent = {},
			elapsedTime = { 1 },
			openDrawer = {},
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
