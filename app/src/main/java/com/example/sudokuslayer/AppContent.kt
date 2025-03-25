package com.example.sudokuslayer

import android.app.Activity
import android.app.Application
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
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
import com.example.data.core.preferences.PreferenceStorageSingleton
import com.example.data.preferences.DataStorePreferenceStorageFactory
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import com.example.sudokuslayer.presentation.navigation.Destination
import com.example.sudokuslayer.presentation.navigation.SudokuNavHost
import com.example.sudokuslayer.presentation.navigation.components.NavigationDrawer
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme
import com.example.sudokuslayer.presentation.ui.theme.ThemeProvider
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

class MyApplication : Application() {
	override fun onCreate() {
		super.onCreate()

		PreferenceStorageSingleton.initialize(
			DataStorePreferenceStorageFactory(applicationContext),
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppContent() {
	val navController = rememberNavController()
	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
	val destinations =
		persistentListOf(
			Destination.SudokuGame,
			Destination.SudokuCreator,
			Destination.Settings,
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

	val darkScheme by ThemeProvider.getDarkColorScheme().collectAsState(initial = ColorScheme.Mocha())
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
		) {
			Column(modifier = Modifier.fillMaxSize()) {
				SudokuNavHost(
					navController = navController,
					openDrawer = { scope.launch { drawerState.open() } },
				)
			}
		}
	}
}
