package com.example.sudokuslayer

import android.app.Activity
import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.example.data.core.preferences.DataStoreProvider
import com.example.data.core.preferences.PreferencesManager
import com.example.sudokuslayer.presentation.navigation.Destination
import com.example.sudokuslayer.presentation.navigation.SudokuNavHost
import com.example.sudokuslayer.presentation.navigation.components.NavigationDrawer
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme
import kotlinx.coroutines.launch

class App : Application() {
	lateinit var preferencesManager: PreferencesManager
		private set

	override fun onCreate() {
		super.onCreate()
		preferencesManager = DataStoreProvider.providePreferencesManager(this)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppContent() {
	val navController = rememberNavController()
	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
	val destinations =
		listOf(
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

	SudokuSlayerTheme {
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
