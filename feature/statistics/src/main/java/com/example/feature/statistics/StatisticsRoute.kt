package com.example.feature.statistics

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.example.feature.statistics.insights.InsightsScreen
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object Insights : Destination("Insights", AppIcon.VectorIcon(Icons.Default.DateRange))

@OptIn(ExperimentalSharedTransitionApi::class)
fun EntryProviderBuilder<NavKey>.insightsEntry(
	openDrawer: () -> Unit,
	navigateToStatisticsFilter: () -> Unit,
) {
	entry<Insights> {
		val viewModel = koinViewModel<StatisticsViewModel>()
		InsightsScreen(
			viewModel = viewModel,
			openDrawer = openDrawer,
			onFabClick = navigateToStatisticsFilter,
			modifier = Modifier
				.fillMaxSize(),
		)
	}
}
