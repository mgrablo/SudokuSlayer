package com.example.feature.uicore.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import kotlinx.serialization.Serializable

@Serializable
open class Destination(val routeName: String, val icon: AppIcon) {
	@Serializable
	object SudokuGame : Destination("Current game", AppIcon.ResourceIcon(R.drawable.tag))

	@Serializable
	object Settings : Destination(
		"Settings",
		AppIcon.VectorIcon(Icons.Default.Settings, "Settings icon"),
	)
}
