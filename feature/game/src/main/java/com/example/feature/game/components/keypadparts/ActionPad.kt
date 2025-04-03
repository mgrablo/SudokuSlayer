package com.example.feature.game.components.keypadparts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.navigation.AppIcon
import com.example.feature.uicore.theme.LocalKeyPadColors
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class ActionPadItem(val icon: AppIcon, val onClick: () -> Unit, val contentDescription: String)

enum class ActionPadOrientation {
	HORIZONTAL,
	VERTICAL,
}

@Composable
fun ActionPad(
	items: PersistentList<ActionPadItem>,
	modifier: Modifier = Modifier,
	orientation: ActionPadOrientation = ActionPadOrientation.HORIZONTAL,
	itemSize: Dp = 60.dp,
	textStyle: TextStyle = TextStyle(),
	itemBackgroundColor: Color = LocalKeyPadColors.current.actionPadBackground,
	itemOnBackgroundColor: Color = LocalKeyPadColors.current.actionPadOnBackground,
) {
	LayoutWithOrientation(
		orientation = orientation,
		modifier = modifier,
	) {
		items.forEach { item ->
			KeyPadItem(
				text = "",
				icon = item.icon,
				onClick = item.onClick,
				bgColor = itemBackgroundColor,
				textColor = itemOnBackgroundColor,
				textStyle = textStyle,
				modifier =
				Modifier
					.size(itemSize),
			)
		}
	}
}

@Composable
private fun LayoutWithOrientation(
	orientation: ActionPadOrientation,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	val movableContent = remember { movableContentOf { content() } }

	when (orientation) {
		ActionPadOrientation.HORIZONTAL -> {
			Row(
				modifier = modifier,
				horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
				verticalAlignment = Alignment.CenterVertically,
			) {
				movableContent()
			}
		}

		ActionPadOrientation.VERTICAL -> {
			Column(
				modifier = modifier.padding(LocalPadding.current.tiny),
				verticalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				movableContent()
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun ActionPadHorizontalPreview() {
	SudokuSlayerTheme {
		val items = getPreviewItems()

		ActionPad(
			items = items,
			orientation = ActionPadOrientation.HORIZONTAL,
		)
	}
}

@Preview
@Composable
private fun ActionPadVerticalPreview() {
	SudokuSlayerTheme {
		val items = getPreviewItems()

		ActionPad(
			items = items,
			orientation = ActionPadOrientation.VERTICAL,
		)
	}
}

@Composable
private fun getPreviewItems() = persistentListOf(
	ActionPadItem(
		icon = AppIcon.VectorIcon(Icons.AutoMirrored.Default.ArrowBack, ""),
		onClick = { },
		contentDescription = "",
	),
	ActionPadItem(
		icon = AppIcon.VectorIcon(Icons.Default.Clear, ""),
		onClick = { },
		contentDescription = "",
	),
	ActionPadItem(
		icon = AppIcon.VectorIcon(Icons.AutoMirrored.Default.ArrowForward, ""),
		onClick = { },
		contentDescription = "",
	),
)
