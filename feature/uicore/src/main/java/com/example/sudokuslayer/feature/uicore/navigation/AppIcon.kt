package com.example.sudokuslayer.feature.uicore.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
internal fun DestinationIcon(
	icon: AppIcon,
	modifier: Modifier = Modifier,
	tint: Color = LocalContentColor.current,
) {
	when (icon) {
		is AppIcon.ResourceIcon -> Icon(
			painter = painterResource(icon.resourceId),
			contentDescription = icon.contentDescription,
			modifier = modifier,
			tint = tint,
		)

		is AppIcon.VectorIcon -> Icon(
			imageVector = icon.imageVector,
			contentDescription = icon.contentDescription,
			modifier = modifier,
			tint = tint,
		)
	}
}
