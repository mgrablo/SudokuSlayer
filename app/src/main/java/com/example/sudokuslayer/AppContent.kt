package com.example.sudokuslayer

import android.app.Activity
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import com.example.feature.creator.SudokuCreator
import com.example.feature.creator.sudokuCreatorEntry
import com.example.feature.game.SudokuGame
import com.example.feature.game.gameEntry
import com.example.feature.settings.Settings
import com.example.feature.settings.settingsEntry
import com.example.feature.statistics.Insights
import com.example.feature.statistics.insightsEntry
import com.example.feature.uicore.components.SudokuNavigationRail
import com.example.feature.uicore.navigation.Destination
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.theme.ThemeProvider
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin

class MyApplication : Application() {
	override fun onCreate() {
		super.onCreate()

		startKoin {
			modules(appModule)
			androidContext(this@MyApplication)
			androidLogger()
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AppContent(viewModel: AppViewModel = koinViewModel()) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val backstack = rememberNavBackStack<Destination>(SudokuCreator)
	val navigationRailState = rememberWideNavigationRailState(
		initialValue = WideNavigationRailValue.Collapsed,
	)
	val destinations =
		persistentListOf(
			SudokuGame,
			SudokuCreator,
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

	val themeMode by ThemeProvider.getTheme().collectAsState(initial = DarkMode.SYSTEM)

	val darkScheme by ThemeProvider.getDarkColorScheme()
		.collectAsState(initial = ColorScheme.Mocha())
	val lightScheme by ThemeProvider.getLightColorScheme().collectAsState(
		initial = ColorScheme.Latte(),
	)

	SudokuSlayerTheme(
		darkTheme =
		when (themeMode) {
			DarkMode.DARK -> true
			DarkMode.LIGHT -> false
			DarkMode.SYSTEM -> isSystemInDarkTheme()
		},
		lightScheme = lightScheme,
		darkScheme = darkScheme,
	) {
		SudokuNavigationRail(
			state = navigationRailState,
			destinations = destinations,
			hasActiveGame = uiState.hasActiveGame,
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
			modifier = Modifier.fillMaxSize().background(
				MaterialTheme.colorScheme.background,
			),
			backStack = backstack,
			entryDecorators = listOf(
				rememberSceneSetupNavEntryDecorator(),
				rememberSavedStateNavEntryDecorator(),
				rememberViewModelStoreNavEntryDecorator(),
			),
			entryProvider = entryProvider {
				sudokuCreatorEntry(
					navigateToGameScreen = {
						scope.launch {
							backstack.clear()
							backstack.addAll(listOf(SudokuCreator, SudokuGame))
							println(backstack)
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
						scope.launch {
							backstack.clear()
							backstack.add(SudokuCreator)
						}
					},
				)
				insightsEntry(
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
