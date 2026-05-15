package io.github.mgrablo.sudokuslayer

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.mgrablo.sudokuslayer.domain.settings.models.ColorScheme
import io.github.mgrablo.sudokuslayer.domain.settings.models.DarkMode
import io.github.mgrablo.sudokuslayer.feature.creator.PuzzlePreset
import io.github.mgrablo.sudokuslayer.feature.creator.SudokuCreator
import io.github.mgrablo.sudokuslayer.feature.creator.sudokuCreatorEntry
import io.github.mgrablo.sudokuslayer.feature.game.SudokuGame
import io.github.mgrablo.sudokuslayer.feature.game.gameEntry
import io.github.mgrablo.sudokuslayer.feature.settings.Settings
import io.github.mgrablo.sudokuslayer.feature.settings.settingsEntry
import io.github.mgrablo.sudokuslayer.feature.statistics.Insights
import io.github.mgrablo.sudokuslayer.feature.statistics.insightsEntry
import io.github.mgrablo.sudokuslayer.feature.uicore.components.SudokuNavigationRail
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AppContent(viewModel: AppViewModel = koinViewModel()) {
	val backstack =
		rememberNavBackStack(SudokuCreator())
	val navigationRailState = rememberWideNavigationRailState(
		initialValue = WideNavigationRailValue.Collapsed,
	)
	val destinations =
		persistentListOf(
			SudokuGame(),
			SudokuCreator(),
			Insights,
			Settings,
		)
	val scope = rememberCoroutineScope()

	val view = LocalView.current
	val window = (view.context as Activity).window
	val insertsController = WindowCompat.getInsetsController(window, view)
	if (!view.isInEditMode) {
		insertsController.apply {
			hide(WindowInsetsCompat.Type.systemBars())
			systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
		}
	}

	val hasActiveGame by viewModel.hasActiveGame.collectAsStateWithLifecycle()
	val settingsDarkMode by viewModel.darkMode.collectAsStateWithLifecycle()
	val darkColorScheme by viewModel.darkModeColorScheme.collectAsStateWithLifecycle()
	val lightColorScheme by viewModel.lightModeColorScheme.collectAsStateWithLifecycle()
	val currentColorScheme by rememberCurrentColorScheme(
		settingsDarkMode = settingsDarkMode,
		darkColorScheme = darkColorScheme,
		lightColorScheme = lightColorScheme,
	)

	SudokuSlayerTheme(
		colorScheme = currentColorScheme,
	) {
		SudokuNavigationRail(
			state = navigationRailState,
			destinations = destinations,
			hasActiveGame = hasActiveGame,
			isSelected = { backstack.last() == it },
			navigateToScreen = {
				if (it == SudokuCreator) {
					backstack.clear()
					backstack.add(it)
				} else if (backstack.last() != it) {
					backstack.add(it)
				}
				scope.launch {
					navigationRailState.collapse()
				}
			},
			onCloseDrawer = { scope.launch { navigationRailState.collapse() } },
		)
		NavDisplay(
			modifier = Modifier
				.fillMaxSize()
				.background(
					MaterialTheme.colorScheme.background,
				),
			backStack = backstack,
			entryDecorators = listOf(
				rememberSaveableStateHolderNavEntryDecorator(),
				rememberViewModelStoreNavEntryDecorator(),
			),
			entryProvider = entryProvider {
				sudokuCreatorEntry(
					navigateToGameScreen = { gridSize ->
						backstack.apply {
							clear()
							add(SudokuCreator())
							add(SudokuGame(gridSize))
						}
					},
					openDrawer = {
						scope.launch {
							navigationRailState.expand()
						}
					},
				)
				gameEntry(
					openDrawer = {
						scope.launch {
							navigationRailState.expand()
						}
					},
					onPlayAgainClick = {
						backstack.apply {
							clear()
							add(SudokuCreator())
						}
					},
					onNavigateToInsightsClick = {
						backstack.apply {
							removeLastOrNull()
							add(Insights)
						}
					},
				)
				insightsEntry(
					onNavigateToGameScreen = { gridSize ->
						backstack.apply {
							clear()
							add(SudokuCreator())
							add(SudokuGame(gridSize))
						}
					},
					onNavigateToCreator = { seed, gridSize, difficulty ->
						backstack.add(SudokuCreator(PuzzlePreset(seed, difficulty, gridSize)))
					},
					openDrawer = {
						scope.launch {
							navigationRailState.expand()
						}
					},
				)
				settingsEntry(
					openDrawer = {
						scope.launch {
							navigationRailState.expand()
						}
					},
				)
			},
		)
	}
}

@Composable
private fun rememberCurrentColorScheme(
	settingsDarkMode: DarkMode,
	darkColorScheme: ColorScheme,
	lightColorScheme: ColorScheme,
): State<ColorScheme> {
	val isSystemDark = isSystemInDarkTheme()

	return remember(
		settingsDarkMode,
		isSystemDark,
		darkColorScheme,
		lightColorScheme,
	) {
		derivedStateOf {
			when (settingsDarkMode) {
				DarkMode.DARK -> darkColorScheme
				DarkMode.LIGHT -> lightColorScheme
				DarkMode.SYSTEM -> if (isSystemDark) darkColorScheme else lightColorScheme
			}
		}
	}
}
