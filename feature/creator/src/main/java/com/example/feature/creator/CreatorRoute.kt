package com.example.feature.creator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object SudokuCreator : Destination("New game", AppIcon.VectorIcon(Icons.Default.Add, "Clear"))

fun NavGraphBuilder.creatorRoute(navigateToGameScreen: () -> Unit, openDrawer: () -> Unit) {
	composable<SudokuCreator> {
		val viewModel = koinViewModel<SudokuCreatorViewModel>()
		SudokuCreatorScreen(
			navigateToGameScreen = navigateToGameScreen,
			openDrawer = openDrawer,
			viewModel = viewModel,
			modifier = Modifier.fillMaxSize(),
		)
	}
}
