package com.example.sudokuslayer

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.feature.creator.SudokuCreator
import com.example.feature.creator.creatorRoute
import com.example.feature.game.SudokuGame
import com.example.feature.game.gameRoute
import com.example.feature.settings.settingsRoute
import com.example.feature.statistics.StatisticsFilter
import com.example.feature.statistics.statisticsRoute

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SudokuNavHost(
	navController: NavHostController,
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
) {
	SharedTransitionLayout {
		NavHost(
			navController = navController,
			startDestination = SudokuCreator,
			modifier = modifier,
		) {
			creatorRoute(
				navigateToGameScreen = { navController.navigate(SudokuGame) },
				openDrawer = openDrawer,
			)
			settingsRoute(openDrawer)
			gameRoute(openDrawer)
			statisticsRoute(
				navGraphBuilder = this@NavHost,
				openDrawer = openDrawer,
				navigateToFilterScreen = { navController.navigate(StatisticsFilter) },
			)
		}
	}
}
