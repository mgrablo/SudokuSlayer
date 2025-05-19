package com.example.feature.statistics.filter.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import com.example.feature.statistics.model.ColumnDisplayState
import com.example.feature.statistics.model.InsightsTableColumn
import com.example.feature.statistics.model.getDisplayText
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentList
import sh.calvin.reorderable.ReorderableColumn

@Composable
internal fun VisibleColumnsView(
	allTableColumns: PersistentList<ColumnDisplayState>,
	toggleVisibility: (InsightsTableColumn) -> Unit,
	onReorder: (from: Int, to: Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	ReorderableColumn(
		modifier = modifier.clipToBounds(),
		list = allTableColumns,
		onSettle = { from, to ->
			onReorder(from, to)
		},
	) { index, item, isDragging ->
		key(item.column) {
			GenericFilterChip(
				isSelected = item.visible,
				isDraggable = true,
				label = item.column.getDisplayText(),
				onClick = { toggleVisibility(item.column) },
				modifier = Modifier.fillMaxWidth().draggableHandle(),
			)
		}
	}
}

@Preview
@Composable
private fun VisibleColumnsPreview() {
	val allColumns = ColumnDisplayState.getAll()
	SudokuSlayerTheme {
		Surface {
			VisibleColumnsView(
				allTableColumns = allColumns,
				toggleVisibility = { },
				onReorder = { from, to -> },
			)
		}
	}
}
