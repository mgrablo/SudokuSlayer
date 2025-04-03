package com.example.feature.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object Settings : Destination(
	"Settings",
	AppIcon.VectorIcon(Icons.Default.Settings, "Settings icon"),
)

fun NavGraphBuilder.settingsRoute(openDrawer: () -> Unit) {
	composable<Settings> {
		val viewModel = koinViewModel<SettingsViewModel>()
		SettingsScreen(
			openDrawer = openDrawer,
			viewModel = viewModel,
			modifier = Modifier.fillMaxSize(),
		)
	}
}
