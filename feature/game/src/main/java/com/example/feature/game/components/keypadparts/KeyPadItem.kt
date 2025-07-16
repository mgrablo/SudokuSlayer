package com.example.feature.game.components.keypadparts

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.game.theme.SudokuGameTheme
import com.example.feature.uicore.navigation.AppIcon

@Composable
fun KeyPadItem(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	onLongClick: (() -> Unit)? = null,
	icon: AppIcon? = null,
	textStyle: TextStyle = TextStyle(),
	containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
	textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
	val mutableInteractionSource = remember { MutableInteractionSource() }
	Box(
		modifier =
		modifier
			.aspectRatio(1f)
			.clip(CircleShape)
			.background(containerColor)
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick,
				interactionSource = mutableInteractionSource,
				indication = ripple(
					bounded = true,
					color = textColor.copy(alpha = 0.7f),
				),
			),
		contentAlignment = Alignment.Center,
	) {
		if (icon != null) {
			when (icon) {
				is AppIcon.ResourceIcon -> {
					Icon(
						painter = painterResource(id = icon.resourceId),
						contentDescription = icon.contentDescription,
						tint = textColor,
						modifier = Modifier.size(textStyle.fontSize.value.dp),
					)
				}

				is AppIcon.VectorIcon -> {
					Icon(
						imageVector = icon.imageVector,
						contentDescription = icon.contentDescription,
						tint = textColor,
						modifier = Modifier.size(textStyle.fontSize.value.dp),
					)
				}
			}
		} else {
			Text(
				text = text,
				color = textColor,
				style = textStyle,
				textAlign = TextAlign.Center,
				maxLines = 1,
				modifier =
				Modifier
					.fillMaxSize()
					.wrapContentHeight(Alignment.CenterVertically),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun KeyboardItemNumberPreview() {
	SudokuGameTheme {
		KeyPadItem(
			text = "5",
			onClick = { },
			textStyle = TextStyle(fontSize = 24.sp),
		)
	}
}

@PreviewLightDark
@Composable
private fun KeyboardItemIconPreview() {
	SudokuGameTheme {
		KeyPadItem(
			text = "5",
			icon = AppIcon.VectorIcon(Icons.Default.Clear, "Clear"),
			onClick = { },
			textStyle = TextStyle(fontSize = 24.sp),
		)
	}
}
