package com.example.feature.uicore.navigation

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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

@Composable
internal fun DestinationIcon(icon: AppIcon) {
	when (icon) {
		is AppIcon.ResourceIcon -> Icon(painterResource(icon.resourceId), "")
		is AppIcon.VectorIcon -> Icon(icon.imageVector, "")
	}
}
