package com.example.sudokuslayer.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sudokuslayer.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination(
	val routeName: String,
	val icon: DestinationIcon,
) {
	@Serializable
	object SudokuGame : Destination("Current game", DestinationIcon.ResourceIcon(R.drawable.tag))

	@Serializable
	object SudokuCreator : Destination("New game", DestinationIcon.VectorIcon(Icons.Default.Add))
}

@Serializable
sealed class DestinationIcon {
	data class VectorIcon(val imageVector: ImageVector) : DestinationIcon()

	data class ResourceIcon(val resourceId: Int) : DestinationIcon()
}
