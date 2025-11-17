package io.github.mgrablo.sudokuslayer.feature.creator.components

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
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.feature.creator.R
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.SudokuSlayerTheme
import io.github.mgrablo.sudokuslayer.feature.uicore.toLocalizedString
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun GridSizeSelector(
	options: PersistentList<SudokuGridSize>,
	selectedSize: SudokuGridSize,
	onCheckedChange: (SudokuGridSize) -> Unit,
	modifier: Modifier = Modifier,
) {
	val selectedIndex = options.indexOf(selectedSize)
	Column(
		modifier = modifier,
	) {
		Text(
			text = stringResource(R.string.size),
			style = MaterialTheme.typography.labelLarge,
		)
		FlowRow(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
			verticalArrangement = Arrangement.spacedBy(2.dp),
		) {
			options.forEachIndexed { index, size ->
				ToggleButton(
					checked = index == selectedIndex,
					onCheckedChange = { onCheckedChange(size) },
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
					Text(size.toLocalizedString())
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLightDark
@Composable
private fun GridSizeSelectorPreview() {
	var selected by remember { mutableStateOf(SudokuGridSize.NINE) }
	SudokuSlayerTheme {
		Surface {
			GridSizeSelector(
				options = SudokuGridSize.entries.toPersistentList(),
				selectedSize = selected,
				onCheckedChange = { selected = it },
			)
		}
	}
}
