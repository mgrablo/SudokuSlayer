package com.example.feature.statistics

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.serialization.Serializable

@Serializable
data object Statistics : Destination("Statistics", AppIcon.VectorIcon(Icons.Default.DateRange))

@Serializable
data object StatisticsTable :
	Destination("Statistics/Table", AppIcon.VectorIcon(Icons.Default.DateRange))

@Serializable
data object StatisticsFilter :
	Destination("Statistics/Filter", icon = AppIcon.ResourceIcon(R.drawable.filter))

const val STATISTICS_FAB_EXPLODE_BOUNDS = "STATISTICS_FAB_EXPLODE_BOUNDS"

@OptIn(ExperimentalSharedTransitionApi::class)
fun SharedTransitionScope.statisticsRoute(
	navGraphBuilder: NavGraphBuilder,
	openDrawer: () -> Unit,
	navigateToFilterScreen: () -> Unit,
) {
	navGraphBuilder.apply {
		navigation<Statistics>(startDestination = StatisticsTable) {
			composable<StatisticsTable> {
				StatisticsScreen(
					openDrawer = openDrawer,
					onFabClick = navigateToFilterScreen,
					animatedVisibilityScope = this,
					sharedTransitionScope = this@statisticsRoute,
					modifier = Modifier
						.fillMaxSize(),
				)
			}
			composable<StatisticsFilter> {
				FilterScreen(
					modifier = Modifier
						.fillMaxSize()
						.sharedBounds(
							sharedContentState = rememberSharedContentState(
								key = STATISTICS_FAB_EXPLODE_BOUNDS,
							),
							animatedVisibilityScope = this,
						),
				)
			}
		}
	}
}
