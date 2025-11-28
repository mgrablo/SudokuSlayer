package io.github.mgrablo.sudokuslayer.feature.creator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.feature.uicore.navigation.AppIcon
import io.github.mgrablo.sudokuslayer.feature.uicore.navigation.Destination
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class SudokuCreator(val args: PuzzlePreset? = null) : Destination {
	override val routeId: String = "sudoku_creator"
	override val displayNameRes: Int = R.string.sudoku_creator_title
	override val icon: AppIcon = AppIcon.VectorIcon(Icons.Default.Add)
}

fun EntryProviderScope<NavKey>.sudokuCreatorEntry(
	navigateToGameScreen: (SudokuGridSize) -> Unit,
	openDrawer: () -> Unit,
) {
	entry<SudokuCreator> {
		val viewModel = koinViewModel<SudokuCreatorViewModel> {
			parametersOf(it.args)
		}
		SudokuCreatorScreen(
			onNavigateToGameScreen = navigateToGameScreen,
			navAnimatedContentScope = LocalNavAnimatedContentScope.current,
			openDrawer = openDrawer,
			viewModel = viewModel,
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@Serializable
data class PuzzlePreset(
	val seed: Long,
	val difficulty: GameDifficulty,
	val gridSize: SudokuGridSize,
)
