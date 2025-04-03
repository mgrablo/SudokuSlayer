package com.example.sudokuslayer

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.feature.creator.SudokuCreator
import com.example.feature.creator.creatorRoute
import com.example.feature.game.SudokuGame
import com.example.feature.game.gameRoute
import com.example.feature.settings.settingsRoute

@Composable
fun SudokuNavHost(navController: NavHostController, openDrawer: () -> Unit) {
	NavHost(
		navController = navController,
		startDestination = SudokuCreator,
	) {
		creatorRoute(
			navigateToGameScreen = { navController.navigate(SudokuGame) },
			openDrawer = openDrawer,
		)
		settingsRoute(openDrawer)
		gameRoute(openDrawer)
	}
}
