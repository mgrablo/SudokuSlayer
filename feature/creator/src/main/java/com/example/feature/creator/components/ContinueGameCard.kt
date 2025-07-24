package com.example.feature.creator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.feature.uicore.rememberFormattedTime
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.toLocalizedString
import com.example.sudokuslayer.feature.creator.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ContinueGameCard(
	difficulty: GameDifficulty,
	gridSize: SudokuGridSize,
	elapsedTime: Long,
	completed: Boolean,
	onContinueClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val formattedTime = rememberFormattedTime(elapsedTime.toFloat())
	val painter = if (completed) {
		painterResource(R.drawable.trophy_24px)
	} else {
		painterResource(R.drawable.pause)
	}
	ElevatedCard(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant,
		),
		modifier = modifier,
	) {
		Column(
			modifier = Modifier.padding(LocalPadding.current.big),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = "Active Game",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
				)
				Spacer(Modifier.weight(1f))
				Icon(
					painter = painter,
					contentDescription = if (completed) "Puzzle completed" else "Game is paused",
					tint = if (completed) {
						MaterialTheme.colorScheme.primary
					} else {
						MaterialTheme.colorScheme.secondary
					},
				)
			}
			Spacer(Modifier.height(LocalPadding.current.small))
			DataRow("Difficulty:", difficulty.toLocalizedString())
			DataRow("Size:", gridSize.toLocalizedString())
			DataRow("Time:", formattedTime)
			Spacer(Modifier.height(LocalPadding.current.big))
			Button(
				onClick = onContinueClick,
				shapes = ButtonShapes(
					shape = ButtonDefaults.squareShape,
					pressedShape = ButtonDefaults.shape,
				),
				modifier = Modifier.fillMaxWidth(),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.secondary,
					contentColor = MaterialTheme.colorScheme.onSecondary,
				),
			) {
				Text(text = if (completed) "View Board" else "Continue")
			}
		}
	}
}

@Composable
private fun DataRow(
	label: String,
	value: String,
	modifier: Modifier = Modifier,
	labelTextStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
		color = MaterialTheme.colorScheme.onSurfaceVariant,
	),
	valueTextStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
		fontWeight = FontWeight.Medium,
		color = MaterialTheme.colorScheme.onSurface,
	),
) {
	Row(modifier) {
		Text(label, style = labelTextStyle)
		Spacer(Modifier.weight(1f))
		Text(value, style = valueTextStyle)
	}
}

@PreviewLightDark
@Composable
private fun ContinueGameCardPreview() {
	SudokuSlayerTheme {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			ContinueGameCard(
				difficulty = GameDifficulty.Easy,
				gridSize = SudokuGridSize.NINE,
				elapsedTime = 1000,
				completed = false,
				onContinueClick = { },
				modifier = Modifier,
			)
			ContinueGameCard(
				difficulty = GameDifficulty.Medium,
				gridSize = SudokuGridSize.SIXTEEN,
				elapsedTime = 100,
				completed = true,
				onContinueClick = { },
				modifier = Modifier,
			)
		}
	}
}
