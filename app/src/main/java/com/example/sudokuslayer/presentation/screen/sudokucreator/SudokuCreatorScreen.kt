package com.example.sudokuslayer.presentation.screen.sudokucreator

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sudokuslayer.presentation.navigation.Destination
import com.example.sudokuslayer.presentation.screen.sudokucreator.SudokuCreatorViewModel.Event
import com.example.sudokuslayer.presentation.screen.sudokucreator.components.HorizontalSelect
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import org.koin.androidx.compose.koinViewModel

private val PreviewBoxSize = 200.dp
private const val SELECTSMAXWIDTH = 0.8f
private val SpacerHeight = 50.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuCreatorScreen(
	context: Context,
	navController: NavController,
	openDrawer: () -> Unit,
	viewModel: SudokuCreatorViewModel = koinViewModel(),
) {
	val uiState by viewModel.uiState.collectAsState()

	SudokuCreatorContent(
		uiState = uiState,
		onEvent = viewModel::onEvent,
		openDrawer = openDrawer,
		navigateToGame = { navController.navigate(Destination.SudokuGame) },
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SudokuCreatorContent(
	uiState: SudokuCreatorUiState,
	onEvent: (Event) -> Unit,
	openDrawer: () -> Unit,
	navigateToGame: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val difficultyOptions =
		remember {
			SudokuDifficulty.entries
				.map {
					it.name.lowercase().replaceFirstChar { it.uppercase() }
				}.toPersistentList()
		}
	val gridOptions =
		remember {
			SudokuGridSize.entries
				.map {
					when (it) {
						SudokuGridSize.FOUR -> "4x4"
						SudokuGridSize.NINE -> "9x9"
						SudokuGridSize.SIXTEEN -> "16x16"
					}
				}.toPersistentList()
		}

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			CenterAlignedTopAppBar(
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
			Spacer(Modifier.height(SpacerHeight))

			Selects(
				gridSizeOptions = gridOptions,
				difficultyOptions = difficultyOptions,
				onGridSizeChange = { onEvent(Event.ChangeGridSize(it)) },
				onDifficultyChange = { onEvent(Event.ChangeDifficulty(it)) },
				modifier = Modifier.fillMaxWidth(SELECTSMAXWIDTH),
			)

			GameControls(
				screenState = uiState.loadingState,
				savedGameData = uiState.savedGameData,
				onNewGame = { onEvent(Event.NewGame) },
				onLoadSudoku = { onEvent(Event.LoadSudoku) },
				onNavigateToGame = navigateToGame,
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
	savedGameData: SavedGameData?,
	onNewGame: () -> Unit,
	onLoadSudoku: () -> Unit,
	onNavigateToGame: () -> Unit,
) {
	val onNavigateToGame by rememberUpdatedState(onNavigateToGame)
	when (screenState) {
		ScreenState.INITIAL -> {
			if (savedGameData != null) {
				GameButton(
					onClick = onLoadSudoku,
					text = "Continue ${savedGameData.difficulty}",
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
private fun GameButton(
	onClick: () -> Unit,
	text: String,
	modifier: Modifier = Modifier,
) {
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

@Preview
@Composable
private fun SudokuCreatorScreenPreview() {
	SudokuCreatorScreen(
		context = LocalContext.current,
		navController = rememberNavController(),
		openDrawer = { },
	)
}
