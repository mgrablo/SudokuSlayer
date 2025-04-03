package com.example.feature.game

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import com.example.sudokuslayer.feature.game.R
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object SudokuGame : Destination("Current game", AppIcon.ResourceIcon(R.drawable.tag))

fun NavGraphBuilder.gameRoute(
	openDrawer: () -> Unit,
) {
	composable<SudokuGame> {
		val viewmodel = koinViewModel<SudokuGameViewModel>()
		SudokuGameScreen(
			openDrawer = openDrawer,
			modifier = Modifier.fillMaxSize(),
			viewModel = viewmodel
		)
	}
}
