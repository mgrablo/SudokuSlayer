package io.github.mgrablo.sudokuslayer.feature.game

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.feature.uicore.navigation.AppIcon
import io.github.mgrablo.sudokuslayer.feature.uicore.navigation.Destination
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class SudokuGame(val sudokuGridSize: SudokuGridSize = SudokuGridSize.NINE) : Destination {
	override val routeId: String = "sudoku_game"
	override val displayNameRes: Int = R.string.game_screen_title
	override val icon: AppIcon = AppIcon.ResourceIcon(R.drawable.tag)
}

fun EntryProviderScope<NavKey>.gameEntry(
	openDrawer: () -> Unit,
	onPlayAgainClick: () -> Unit,
	onNavigateToInsightsClick: () -> Unit,
) {
	entry<SudokuGame> {
		val viewmodel = koinViewModel<SudokuGameViewModel> {
			parametersOf(it.sudokuGridSize)
		}
		SudokuGameScreen(
			openDrawer = openDrawer,
			modifier = Modifier.fillMaxSize(),
			onPlayAgainClick = onPlayAgainClick,
			onNavigateToInsightsClick = onNavigateToInsightsClick,
			viewModel = viewmodel,
			animatedVisibilityScope = LocalNavAnimatedContentScope.current
		)
	}
}
