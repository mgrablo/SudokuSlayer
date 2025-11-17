package com.example.sudokuslayer.feature.game.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.sudokuslayer.feature.game.R
import com.example.sudokuslayer.feature.game.theme.SudokuGameTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GameTopBar(
	showTimer: Boolean,
	elapsedTime: () -> Long,
	isVictory: Boolean,
	onMenuClick: () -> Unit,
	onSummaryClick: () -> Unit,
) {
	val viewSummaryString = stringResource(R.string.view_summary)
	CenterAlignedTopAppBar(
		windowInsets = WindowInsets.displayCutout,
		title = {
			if (showTimer) {
				TimerDisplay(
					elapsedTime = elapsedTime(),
				)
			}
		},
		colors =
		TopAppBarDefaults.topAppBarColors(
			containerColor = MaterialTheme.colorScheme.surfaceContainer,
		),
		navigationIcon = {
			IconButton(onClick = onMenuClick) {
				Icon(Icons.Default.Menu, "")
			}
		},
		actions = {
			if (isVictory) {
				IconButton(
					onClick = onSummaryClick,
					modifier = Modifier.semantics {
						this.onClick(label = viewSummaryString, action = null)
					},
				) {
					Icon(
						painter = painterResource(R.drawable.trophy),
						contentDescription = stringResource(R.string.view_summary),
					)
				}
			}
		},
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
internal fun GameTopBarPreview() {
	SudokuGameTheme {
		GameTopBar(
			showTimer = true,
			elapsedTime = { 0L },
			isVictory = true,
			onMenuClick = {},
			onSummaryClick = {},
		)
	}
}
