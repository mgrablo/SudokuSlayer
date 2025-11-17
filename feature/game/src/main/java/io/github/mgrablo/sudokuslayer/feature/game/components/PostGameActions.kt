package io.github.mgrablo.sudokuslayer.feature.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.mgrablo.sudokuslayer.feature.game.R
import io.github.mgrablo.sudokuslayer.feature.game.theme.SudokuGameTheme
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalSudokuTypography

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun PostGameActions(
	onViewSummary: () -> Unit,
	onPlayAgainClick: () -> Unit,
	onShowInsights: () -> Unit,
	summaryOpen: Boolean,
	modifier: Modifier = Modifier,
) {
	Column(
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.widthIn(max = 300.dp)
				.clip(
					MaterialTheme.shapes.medium,
				)
				.background(
					MaterialTheme.colorScheme.surface,
				)
				.padding(LocalPadding.current.large),
		) {
			Text(
				text = "Puzzle Complete!",
				style = LocalSudokuTypography.current.displayMediumEmphasized,
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 24.sp,
			)
			Spacer(Modifier.height(LocalPadding.current.normal))
			Button(
				onClick = onPlayAgainClick,
				shape = ButtonDefaults.mediumPressedShape,
				modifier = Modifier.fillMaxWidth(),
			) {
				Icon(
					Icons.Default.PlayArrow,
					contentDescription = "",
					modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
				)
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text(text = "Play Again", color = MaterialTheme.colorScheme.onPrimary)
			}
			Spacer(Modifier.size(LocalPadding.current.small))
			SplitButtonLayout(
				modifier = Modifier.fillMaxWidth(),
				leadingButton = {
					SplitButtonDefaults.LeadingButton(
						onClick = onShowInsights,
						modifier = Modifier.fillMaxWidth(0.75f),
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.secondary,
						),
					) {
						Icon(
							Icons.Default.DateRange,
							contentDescription = "",
							modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
						)
						Spacer(Modifier.size(ButtonDefaults.IconSpacing))
						Text(
							text = "Insights",
							color = MaterialTheme.colorScheme.onSecondary,
						)
					}
				},
				trailingButton = {
					SplitButtonDefaults.TrailingButton(
						checked = summaryOpen,
						modifier = Modifier.fillMaxWidth(1f),
						onCheckedChange = { onViewSummary() },
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.secondary,
						),
					) {
						Icon(
							painter = painterResource(R.drawable.trophy),
							contentDescription = "View summary",
						)
					}
				},
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun PostGameActionsPreview() {
	SudokuGameTheme {
		PostGameActions(
			onViewSummary = { },
			onPlayAgainClick = { },
			onShowInsights = { },
			summaryOpen = false,
		)
	}
}
