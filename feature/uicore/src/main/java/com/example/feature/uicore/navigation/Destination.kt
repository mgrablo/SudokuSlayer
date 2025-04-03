package com.example.feature.uicore.navigation

import kotlinx.serialization.Serializable

@Serializable
open class Destination(val routeName: String, val icon: AppIcon) {
	@Serializable
	object SudokuGame : Destination("Current game", AppIcon.ResourceIcon(R.drawable.tag))
}
