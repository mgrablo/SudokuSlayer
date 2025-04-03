package com.example.feature.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.domain.core.Game
import com.example.feature.creator.SudokuCreatorViewModel.Event
import com.example.feature.creator.components.HorizontalSelect
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel

private val PreviewBoxSize = 200.dp
private const val SELECTS_MAX_WIDTH = 0.8f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SudokuCreatorScreen(
	navigateToGameScreen: () -> Unit,
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SudokuCreatorViewModel = koinViewModel(),
) {
	val uiState by viewModel.uiState.collectAsState()

	SudokuCreatorContent(
		uiState = uiState,
		onEvent = viewModel::onEvent,
		openDrawer = openDrawer,
		navigateToGameScreen = navigateToGameScreen,
		difficultyOptions = viewModel.difficulties,
		gridSizeOptions = viewModel.gridSizeOptions,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SudokuCreatorContent(
	uiState: SudokuCreatorUiState,
	difficultyOptions: PersistentList<String>,
	gridSizeOptions: PersistentList<String>,
	onEvent: (Event) -> Unit,
	openDrawer: () -> Unit,
	navigateToGameScreen: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		modifier = modifier,
		topBar = {
			CenterAlignedTopAppBar(
				windowInsets = WindowInsets.displayCutout,
				title = { },
				navigationIcon = {
					IconButton(onClick = openDrawer) {
						Icon(Icons.Default.Menu, contentDescription = "Open menu")
					}
				},
			)
		},
	) { innerPadding ->
		Column(
			modifier =
			Modifier
				.fillMaxSize()
				.padding(innerPadding),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			PreviewBox()
			Spacer(Modifier.height(LocalPadding.current.big))

			Selects(
				gridSizeOptions = gridSizeOptions,
				difficultyOptions = difficultyOptions,
				onGridSizeChange = { onEvent(Event.ChangeGridSize(it)) },
				onDifficultyChange = { onEvent(Event.ChangeDifficulty(it)) },
				modifier = Modifier.fillMaxWidth(SELECTS_MAX_WIDTH),
			)

			GameControls(
				screenState = uiState.loadingState,
				savedGame = uiState.savedGame,
				onNewGame = { onEvent(Event.NewGame) },
				onLoadSudoku = { onEvent(Event.LoadSudoku) },
				onNavigateToGame = navigateToGameScreen,
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
		)
	}
}

@Composable
private fun GameControls(
	screenState: ScreenState,
	savedGame: Game?,
	onNewGame: () -> Unit,
	onLoadSudoku: () -> Unit,
	onNavigateToGame: () -> Unit,
) {
	val onNavigateToGame by rememberUpdatedState(onNavigateToGame)
	when (screenState) {
		ScreenState.INITIAL -> {
			if (savedGame != null) {
				GameButton(
					onClick = onLoadSudoku,
					text = "Continue ${savedGame.difficulty}",
				)
			}
			GameButton(
				onClick = onNewGame,
				text = "New game",
			)
		}

		ScreenState.LOADING -> {
			CircularProgressIndicator()
		}

		ScreenState.DONE -> {
			LaunchedEffect(Unit) {
				onNavigateToGame()
			}
		}
	}
}

@Composable
private fun GameButton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {
	Button(
		onClick = onClick,
		colors =
		ButtonDefaults.buttonColors(
			containerColor = MaterialTheme.colorScheme.primaryContainer,
		),
		modifier = modifier,
	) {
		Text(
			text = text,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
		)
	}
}

@Composable
private fun Selects(
	gridSizeOptions: PersistentList<String>,
	difficultyOptions: PersistentList<String>,
	onGridSizeChange: (Int) -> Unit,
	onDifficultyChange: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		HorizontalSelect(
			options = gridSizeOptions,
			onChange = onGridSizeChange,
			modifier = Modifier.fillMaxWidth(),
		)
		HorizontalSelect(
			options = difficultyOptions,
			onChange = onDifficultyChange,
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
			navigateToGameScreen = { },
			modifier = Modifier.fillMaxSize(),
		)
	}
}
