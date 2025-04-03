package com.example.feature.uicore.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class AppIcon {
	abstract val contentDescription: String?

	data class VectorIcon(
		val imageVector: ImageVector,
		override val contentDescription: String? = null,
	) : AppIcon()

	data class ResourceIcon(val resourceId: Int, override val contentDescription: String? = null) :
		AppIcon()
}
