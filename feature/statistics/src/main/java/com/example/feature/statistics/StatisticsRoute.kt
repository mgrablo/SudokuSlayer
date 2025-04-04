package com.example.feature.statistics

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable
data object Statistics : Destination("Statistics", AppIcon.VectorIcon(Icons.Default.DateRange))

fun NavGraphBuilder.statisticsRoute(openDrawer: () -> Unit) {
	composable<Statistics> {
		StatisticsScreen(
			modifier = Modifier.fillMaxSize()
		)
	}
}
