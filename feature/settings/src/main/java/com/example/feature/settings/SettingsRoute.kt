package com.example.feature.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.navigation.Destination
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object Settings : Destination(
	"Settings",
	AppIcon.VectorIcon(Icons.Default.Settings),
)

fun EntryProviderBuilder<NavKey>.settingsEntry(openDrawer: () -> Unit) {
	entry<Settings> {
		val viewModel = koinViewModel<SettingsViewModel>()
		SettingsScreen(
			openDrawer = openDrawer,
			viewModel = viewModel,
			modifier = Modifier.fillMaxSize(),
		)
	}
}
