package com.example.sudokuslayer.feature.uicore.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey

@Stable
interface Destination : NavKey {
	val routeId: String

	@get:StringRes
	val displayNameRes: Int
	val icon: AppIcon
}
