package com.example.sudokuslayer

import android.app.Activity
import android.app.Application
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import com.example.feature.creator.SudokuCreator
import com.example.feature.game.SudokuGame
import com.example.feature.settings.Settings
import com.example.feature.statistics.Statistics
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.theme.ThemeProvider
import com.example.sudokuslayer.components.NavigationDrawer
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppContent() {
	val navController = rememberNavController()
	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
	val destinations =
		persistentListOf(
			SudokuGame,
			SudokuCreator,
			Statistics,
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
		NavigationDrawer(
			destinations = destinations,
			drawerState = drawerState,
			navController = navController,
			scope = scope,
			modifier = Modifier,
		) {
			SudokuNavHost(
				navController = navController,
				openDrawer = { scope.launch { drawerState.open() } },
				modifier = Modifier
					.fillMaxSize(),
			)
		}
	}
}
