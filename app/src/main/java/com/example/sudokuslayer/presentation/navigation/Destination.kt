package com.example.sudokuslayer.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sudokuslayer.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination(val routeName: String, val icon: AppIcon) {
	@Serializable
	object SudokuGame : Destination("Current game", AppIcon.ResourceIcon(R.drawable.tag))

	@Serializable
	object SudokuCreator : Destination("New game", AppIcon.VectorIcon(Icons.Default.Add, "Clear"))

	@Serializable
	object Settings : Destination(
		"Settings",
		AppIcon.VectorIcon(Icons.Default.Settings, "Settings icon"),
	)
}

@Serializable
sealed class AppIcon {
	abstract val contentDescription: String?

	data class VectorIcon(
		val imageVector: ImageVector,
		override val contentDescription: String? = null
	) : AppIcon()

	data class ResourceIcon(val resourceId: Int, override val contentDescription: String? = null) :
		AppIcon()
}
