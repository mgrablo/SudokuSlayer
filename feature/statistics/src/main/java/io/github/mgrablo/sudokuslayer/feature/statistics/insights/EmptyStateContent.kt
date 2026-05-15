package io.github.mgrablo.sudokuslayer.feature.statistics.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokuslayer.feature.statistics.R
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme

@Composable
internal fun EmptyStateContent(onPlayFirstGameClick: () -> Unit, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(
			LocalPadding.current.small,
			Alignment.CenterVertically,
		),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Icon(
			painter = painterResource(R.drawable.sentiment_dissatisfied),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.size(60.dp),
		)
		Spacer(Modifier.height(LocalPadding.current.small))
		Text(
			text = stringResource(R.string.no_games_found),
			autoSize = TextAutoSize.StepBased(),
			color = MaterialTheme.colorScheme.onSurface,
			fontWeight = FontWeight.Bold,
			maxLines = 1,
		)
		Text(
			text = stringResource(R.string.no_data_message),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			style = MaterialTheme.typography.bodyLarge,
		)
		Spacer(Modifier.height(LocalPadding.current.normal))
		Button(onClick = onPlayFirstGameClick) {
			Icon(
				Icons.Default.PlayArrow,
				contentDescription = null,
			)
			Spacer(Modifier.size(ButtonDefaults.IconSpacing))
			Text(stringResource(R.string.play_first_game))
		}
	}
}

@PreviewLightDark
@Composable
internal fun EmptyStateContentPreview() {
	SudokuSlayerTheme {
		EmptyStateContent(
			onPlayFirstGameClick = { },
			modifier = Modifier
				.background(MaterialTheme.colorScheme.background)
				.fillMaxSize()
				.padding(LocalPadding.current.normal),
		)
	}
}
