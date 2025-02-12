package com.example.sudokuslayer.presentation.screen.game.components.keypadparts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme

data class ActionPadItem(
	val icon: @Composable () -> Unit,
	val onClick: () -> Unit,
	val contentDescription: String,
	val backgroundColor: Color,
	val iconColor: Color,
)

enum class ActionPadOrientation {
	HORIZONTAL, VERTICAL
}

@Composable
fun ActionPad(
	items: List<ActionPadItem>,
	orientation: ActionPadOrientation = ActionPadOrientation.HORIZONTAL,
	modifier: Modifier = Modifier,
) {
	when (orientation) {
		ActionPadOrientation.HORIZONTAL -> {
			Row(
				modifier = modifier
					.padding(horizontal = 8.dp),
//					.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				items.forEach { item ->
					KeyPadItem(
						text = "",
						icon = item.icon,
						onClick = item.onClick,
						bgColor = item.backgroundColor,
						textColor = item.iconColor,
						modifier = Modifier.size(60.dp)
					)
				}
			}
		}

		ActionPadOrientation.VERTICAL -> {
			Column(
				modifier = modifier
					.padding(vertical = 8.dp),
//					.fillMaxHeight(),
				verticalArrangement = Arrangement.spacedBy(8.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				items.forEach { item ->
					KeyPadItem(
						text = "",
						icon = item.icon,
						onClick = item.onClick,
						bgColor = item.backgroundColor,
						textColor = item.iconColor,
						modifier = Modifier.size(60.dp)
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun ActionPadHorizontalPreview() {
	SudokuSlayerTheme {
		val items = listOf(
			ActionPadItem(
				icon = { Icon(Icons.AutoMirrored.Default.ArrowBack, "") },
				onClick = { },
				contentDescription = "",
				backgroundColor = MaterialTheme.colorScheme.background,
				iconColor = MaterialTheme.colorScheme.onBackground
			),
			ActionPadItem(
				icon = { Icon(Icons.Default.Clear, "") },
				onClick = { },
				contentDescription = "",
				backgroundColor = MaterialTheme.colorScheme.primaryContainer,
				iconColor = MaterialTheme.colorScheme.onPrimaryContainer
			),
			ActionPadItem(
				icon = { Icon(Icons.AutoMirrored.Default.ArrowForward, "") },
				onClick = { },
				contentDescription = "",
				backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
				iconColor = MaterialTheme.colorScheme.onTertiaryContainer
			)

		)
		ActionPad(
			items = items,
			orientation = ActionPadOrientation.HORIZONTAL,
		)
	}
}