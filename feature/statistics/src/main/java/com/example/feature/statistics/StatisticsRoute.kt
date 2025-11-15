package com.example.feature.statistics

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.feature.statistics.insights.InsightsScreen
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object Insights : Destination {
	override val routeId: String = "insights"
	override val displayNameRes: Int = R.string.insights_screen_title
	override val icon: AppIcon = AppIcon.ResourceIcon(R.drawable.trophy)
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun EntryProviderScope<NavKey>.insightsEntry(
	openDrawer: () -> Unit,
	onNavigateToGameScreen: (SudokuGridSize) -> Unit,
	onNavigateToCreator: (Long, SudokuGridSize, GameDifficulty) -> Unit,
) {
	entry<Insights> {
		val viewModel = koinViewModel<StatisticsViewModel>()
		InsightsScreen(
			viewModel = viewModel,
			openDrawer = openDrawer,
			onNavigateToGameScreen = onNavigateToGameScreen,
			onNavigateToCreator = onNavigateToCreator,
			modifier = Modifier
				.fillMaxSize(),
		)
	}
}
