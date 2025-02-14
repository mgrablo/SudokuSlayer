package com.example.sudokuslayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sudokuslayer.presentation.navigation.Destination.SudokuCreator
import com.example.sudokuslayer.presentation.navigation.Destination.SudokuGame
import com.example.sudokuslayer.presentation.screen.game.SudokuGameScreen
import com.example.sudokuslayer.presentation.screen.sudokucreator.SudokuCreatorScreen

@Composable
fun SudokuNavHost(
	navController: NavHostController,
	openDrawer: () -> Unit,
) {
	NavHost(
		navController = navController,
		startDestination = SudokuCreator,
	) {
		composable<SudokuGame> {
			SudokuGameScreen(
				context = LocalContext.current,
				openDrawer = openDrawer,
			)
		}
		composable<SudokuCreator> {
			SudokuCreatorScreen(
				context = LocalContext.current,
				navController = navController,
				openDrawer = openDrawer,
			)
		}
	}
}
