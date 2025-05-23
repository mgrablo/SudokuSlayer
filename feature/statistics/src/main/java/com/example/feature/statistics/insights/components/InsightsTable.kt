package com.example.feature.statistics.insights.components

import android.content.ClipData
import android.widget.Toast
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.composables.core.HorizontalScrollbar
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility
import com.composables.core.rememberScrollAreaState
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.feature.statistics.model.ColumnDisplayState
import com.example.feature.statistics.model.InsightsTableColumn
import com.example.feature.statistics.model.SortDirection
import com.example.feature.statistics.model.SortState
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.statistics.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun InsightsTable(
	gameResults: PersistentList<GameResult>,
	tableColumnsState: PersistentList<ColumnDisplayState>,
	sortState: SortState,
	onColumnHeaderClick: (InsightsTableColumn) -> Unit,
	onPlayClick: (Long) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollState = rememberScrollState()
	val scrollAreaState = rememberScrollAreaState(scrollState)
	val visibleColumns = remember(tableColumnsState) {
		tableColumnsState.filter { it.visible }.map(ColumnDisplayState::column).toPersistentList()
	}
	val clipboard = LocalClipboard.current
	val coroutineScope = rememberCoroutineScope()
	val context = LocalContext.current

	ScrollArea(state = scrollAreaState) {
		HorizontalScrollbar(
			modifier = Modifier
				.fillMaxWidth(),
		) {
			Thumb(
				modifier = Modifier.background(
					MaterialTheme.colorScheme.onSurface.copy(0.2f),
					RoundedCornerShape(100),
				),
				thumbVisibility = ThumbVisibility.HideWhileIdle(
					enter = fadeIn(),
					exit = fadeOut(),
					hideDelay = 0.5.seconds,
				),
			)
		}
		LazyColumn(
			modifier = modifier.height(600.dp)
				.fillMaxWidth(),
		) {
			item {
				TableHeader(
					sortState = sortState,
					columns = visibleColumns,
					onSortChange = onColumnHeaderClick,
					scrollStateProvider = { scrollState },
				)
			}
			items(
				items = gameResults,
				key = { it.id },
			) { entry ->
				TableRow(
					gameResult = entry,
					visibleColumns = visibleColumns,
					scrollStateProvider = { scrollState },
					onPlayClick = onPlayClick,
					onCopySeedClick = { seed ->
						coroutineScope.launch {
							val data = ClipData.newPlainText("game seed", seed.toString())
							clipboard.setClipEntry(ClipEntry(data))
							Toast.makeText(
								context,
								context.getString(R.string.copied_seed_to_clipboard),
								Toast.LENGTH_SHORT,
							).show()
						}
					},
				)
			}
		}
	}
}

internal fun LazyListScope.insightsTableContent(
	gameResults: PersistentList<GameResult>,
	visibleColumns: PersistentList<InsightsTableColumn>,
	sortState: SortState,
	scrollState: ScrollState,
	onColumnHeaderClick: (InsightsTableColumn) -> Unit,
	onCopySeedClick: (Long) -> Unit,
	onPlayClick: (Long) -> Unit,
) {
	item {
		TableHeader(
			sortState = sortState,
			columns = visibleColumns,
			onSortChange = onColumnHeaderClick,
			scrollStateProvider = { scrollState },
		)
	}
	items(
		items = gameResults,
		key = { it.id },
	) { entry ->
		TableRow(
			gameResult = entry,
			visibleColumns = visibleColumns,
			scrollStateProvider = { scrollState },
			onPlayClick = onPlayClick,
			onCopySeedClick = onCopySeedClick,
		)
	}
}

@PreviewLightDark
@Composable
private fun InsightsTablePreview() {
	SudokuSlayerTheme {
		Scaffold { paddingValues ->
			Column(
				modifier = Modifier.padding(paddingValues),
			) {
				InsightsTable(
					gameResults = createTestGameResults(30),
					tableColumnsState = InsightsTableColumn.ALL.map { ColumnDisplayState(it, true) }
						.toPersistentList(),
					sortState = SortState(InsightsTableColumn.Date, SortDirection.DESC),
					onColumnHeaderClick = { },
					onPlayClick = { },
				)
			}
		}
	}
}

private fun createTestGameResults(count: Int = 10): PersistentList<GameResult> {
	val results = mutableListOf<GameResult>()
	val difficulties = GameDifficulty.entries
	val gridSizes = SudokuGridSize.entries

	repeat(count) { i ->
		results.add(
			GameResult(
				id = "game_id_${i + 1}",
				timeInSeconds = Random.nextLong(60, 1200), // Between 1 minute and 20 minutes
				difficulty = difficulties.random(),
				gridSize = gridSizes.random(),
				hintsUsed = Random.nextInt(0, 6),
				completionDate = LocalDateTime(2025, 1, 1, 1, 1),
				seed = i.toLong(),
			),
		)
	}
	return results.toPersistentList()
}
