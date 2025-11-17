package io.github.mgrablo.sudokuslayer.feature.creator.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.github.mgrablo.sudokuslayer.feature.creator.R
import io.github.mgrablo.sudokuslayer.feature.creator.theme.SudokuCreatorTheme
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding

@Composable
internal fun AdvancedOptions(
	expanded: Boolean,
	onToggle: () -> Unit,
	seed: String,
	onSeedChange: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.background(
				color = MaterialTheme.colorScheme.surfaceContainer,
				shape = MaterialTheme.shapes.small,
			),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.clickable {
					onToggle()
				}
				.padding(
					horizontal = LocalPadding.current.small,
					vertical = LocalPadding.current.tiny,
				),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Icon(
				Icons.Default.Settings,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
			)
			Spacer(Modifier.size(ButtonDefaults.IconSpacing))
			Text(
				text = stringResource(R.string.advanced_options),
				color = MaterialTheme.colorScheme.onSurface,
			)
			Spacer(Modifier.weight(1f))
			IconButton(onClick = onToggle) {
				AnimatedContent(expanded) { targetState ->
					if (targetState) {
						Icon(
							Icons.Default.KeyboardArrowUp,
							contentDescription = stringResource(R.string.content_desc_collapse),
							tint = MaterialTheme.colorScheme.onSurface,
						)
					} else {
						Icon(
							Icons.Default.KeyboardArrowDown,
							contentDescription = stringResource(R.string.content_desc_expand),
							tint = MaterialTheme.colorScheme.onSurface,
						)
					}
				}
			}
		}
		AnimatedVisibility(expanded) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = LocalPadding.current.small)
					.padding(top = LocalPadding.current.tiny, bottom = LocalPadding.current.normal),
				verticalArrangement = Arrangement.spacedBy(LocalPadding.current.tiny),
			) {
				OutlinedTextField(
					label = { Text(stringResource(R.string.puzzle_seed)) },
					placeholder = { Text(stringResource(R.string.puzzle_seed_placeholder)) },
					value = seed,
					onValueChange = onSeedChange,
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					singleLine = true,
					supportingText = {
						Text(
							text = stringResource(R.string.puzzle_seed_supporting_text),
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
						)
					},
					trailingIcon = {
						if (seed.isNotBlank()) {
							IconButton(onClick = { onSeedChange("") }) {
								Icon(
									Icons.Default.Clear,
									contentDescription = stringResource(R.string.clear_puzzle_seed_content_desc),
								)
							}
						}
					},
					modifier = Modifier.fillMaxWidth(),
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun AdvancedOptionsExpandedPreview() {
	SudokuCreatorTheme {
		AdvancedOptions(
			expanded = true,
			onToggle = { },
			seed = "",
			onSeedChange = { },
			Modifier.fillMaxWidth(),
		)
	}
}

@PreviewLightDark
@Composable
private fun AdvancedOptionsCollapsedPreview() {
	SudokuCreatorTheme {
		AdvancedOptions(
			expanded = false,
			onToggle = { },
			seed = "",
			onSeedChange = { },
			Modifier.fillMaxWidth(),
		)
	}
}
