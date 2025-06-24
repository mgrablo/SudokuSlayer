package com.example.feature.statistics

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
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
fun EntryProviderBuilder<NavKey>.insightsEntry(openDrawer: () -> Unit) {
	entry<Insights> {
		val viewModel = koinViewModel<StatisticsViewModel>()
		InsightsScreen(
			viewModel = viewModel,
			openDrawer = openDrawer,
			modifier = Modifier
				.fillMaxSize(),
		)
	}
}
