package com.example.feature.creator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.domain.core.GameDifficulty
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.feature.uicore.toLocalizedString
import com.example.sudokuslayer.feature.creator.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DifficultySelector(
	options: PersistentList<GameDifficulty>,
	selectedDifficulty: GameDifficulty,
	onCheckedChange: (GameDifficulty) -> Unit,
	modifier: Modifier = Modifier,
) {
	val selectedIndex = options.indexOf(selectedDifficulty)
	Column(modifier) {
		Text(
			text = stringResource(R.string.difficulty),
			style = MaterialTheme.typography.labelLarge,
		)
		FlowRow(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
			verticalArrangement = Arrangement.spacedBy(2.dp),
		) {
			options.forEachIndexed { index, difficulty ->
				ToggleButton(
					checked = index == selectedIndex,
					onCheckedChange = { onCheckedChange(difficulty) },
					shapes =
					when (index) {
						0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
						options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
						else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
					},
					colors = ToggleButtonDefaults.toggleButtonColors(
						containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
					),
				) {
					Text(difficulty.toLocalizedString())
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLightDark
@Composable
private fun DifficultySelectorPreview() {
	var selectedDifficulty by remember { mutableStateOf(GameDifficulty.Easy) }
	SudokuSlayerTheme {
		Surface(
			color = MaterialTheme.colorScheme.background,
		) {
			DifficultySelector(
				options = GameDifficulty.entries.toPersistentList(),
				selectedDifficulty = selectedDifficulty,
				onCheckedChange = { selectedDifficulty = it },
			)
		}
	}
}
