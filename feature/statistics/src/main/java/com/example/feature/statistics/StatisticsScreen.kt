package com.example.feature.statistics

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.domain.statistics.FinishedGame
import com.example.feature.statistics.components.TableHeader
import com.example.feature.statistics.components.TableRow
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDateTime

@Composable
fun StatisticsScreen(
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier
) {
	StatisticsScreenContent(
		openDrawer = openDrawer,
		statEntries = persistentListOf(),
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsScreenContent(
	openDrawer: () -> Unit,
	statEntries: PersistentList<FinishedGame>,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		modifier = modifier,
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text("Statistics") },
				modifier = Modifier,
				windowInsets = WindowInsets.displayCutout,
				navigationIcon = {
					IconButton(onClick = openDrawer) {
						Icon(Icons.Default.Menu, contentDescription = "Open menu")
					}
				},
			)
		},
	) { paddingValues ->
		LazyColumn(
			modifier = Modifier.padding(paddingValues),
		) {
			item {
				TableHeader()
			}
			items(
				items = statEntries,
				key = { it.id },
			) { entry ->
				TableRow(entry)
				HorizontalDivider()
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun StatisticsScreenPreview() {
	val entries = persistentListOf<FinishedGame>(
		FinishedGame(
			id = "1",
			timeInSeconds = 124,
			difficulty = GameDifficulty.Medium,
			gridSize = SudokuGridSize.NINE,
			hintsUsed = 4,
			completedAt = LocalDateTime.parse("2010-06-21T22:19:44"),
		),
		FinishedGame(
			id = "2",
			timeInSeconds = 32,
			difficulty = GameDifficulty.Easy,
			gridSize = SudokuGridSize.FOUR,
			hintsUsed = 0,
			completedAt = LocalDateTime.parse("2010-06-01T22:19:44"),
		),
		FinishedGame(
			id = "3",
			timeInSeconds = 1268,
			difficulty = GameDifficulty.Expert,
			gridSize = SudokuGridSize.SIXTEEN,
			hintsUsed = 45,
			completedAt = LocalDateTime.parse("2010-12-29T22:19:44"),
		),
	)
	SudokuSlayerTheme {
		StatisticsScreenContent(
			openDrawer = { },
			statEntries = entries,
			modifier = Modifier.fillMaxSize(),
		)
	}
}
