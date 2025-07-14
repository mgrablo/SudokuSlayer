package com.example.feature.statistics.insights.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.composables.core.Menu
import com.composables.core.MenuButton
import com.composables.core.MenuContent
import com.composables.core.MenuItem
import com.composables.core.MenuState
import com.composables.core.rememberMenuState
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun TopAppBarActions(
	menuState: MenuState,
	onClearClick: () -> Unit,
	clearActionEnabled: Boolean,
	modifier: Modifier = Modifier,
) {
	Menu(
		state = menuState,
		modifier = modifier,
	) {
		MenuButton(
			modifier = Modifier
				.size(IconButtonDefaults.smallContainerSize())
				.clip(CircleShape),
		) {
			Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.show_options))
		}
		MenuContent(
			enter =
			scaleIn(
				tween(durationMillis = 120, easing = LinearOutSlowInEasing),
				initialScale = 0.8f,
				transformOrigin = TransformOrigin(0f, 0f),
			) +
				fadeIn(tween(durationMillis = 30)),
			exit =
			scaleOut(
				tween(durationMillis = 1, delayMillis = 75),
				targetScale = 1f,
			) +
				fadeOut(tween(durationMillis = 75)),
			modifier = Modifier
				.widthIn(
					min = 112.dp,
					max = 280.dp,
				)
				.clip(MaterialTheme.shapes.extraSmall)
				.background(MaterialTheme.colorScheme.surfaceContainerHigh)
				.padding(
					horizontal = LocalPadding.current.small,
					vertical = LocalPadding.current.tiny,
				),
		) {
			MenuItem(
				onClick = onClearClick,
				enabled = clearActionEnabled,
				modifier = Modifier.height(48.dp).alpha(0.38f),
			) {
				Icon(
					painter = painterResource(R.drawable.delete),
					contentDescription = null,
					tint = if (clearActionEnabled) {
						MaterialTheme.colorScheme.error
					} else {
						MaterialTheme.colorScheme.onSurfaceVariant
					},
				)
				Spacer(Modifier.width(ButtonDefaults.IconSpacing))
				Text(stringResource(R.string.clear_data))
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun TopAppBarActionsCollapsedPreview() {
	SudokuSlayerTheme {
		Surface(
			color = MaterialTheme.colorScheme.surfaceContainer,
		) {
			TopAppBarActions(
				menuState = rememberMenuState(),
				onClearClick = {},
				clearActionEnabled = true,
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun TopAppBarActionsExpandedPreview() {
	val menuState = rememberMenuState(
		expanded = true,
	)
	SudokuSlayerTheme {
		Surface(
			color = MaterialTheme.colorScheme.surfaceContainer,
		) {
			TopAppBarActions(
				menuState = menuState,
				onClearClick = {},
				clearActionEnabled = true,
			)
		}
	}
}
