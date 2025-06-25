package com.example.feature.game

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import com.example.sudokuslayer.feature.game.R
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object SudokuGame : Destination {
	override val routeId: String = "sudoku_game"
	override val displayNameRes: Int = R.string.game_screen_title
	override val icon: AppIcon = AppIcon.ResourceIcon(R.drawable.tag)
}

fun EntryProviderBuilder<NavKey>.gameEntry(openDrawer: () -> Unit, onPlayAgainClick: () -> Unit) {
	entry<SudokuGame> {
		val viewmodel = koinViewModel<SudokuGameViewModel>()
		SudokuGameScreen(
			openDrawer = openDrawer,
			modifier = Modifier.fillMaxSize(),
			onPlayAgainClick = onPlayAgainClick,
			viewModel = viewmodel,
		)
	}
}
