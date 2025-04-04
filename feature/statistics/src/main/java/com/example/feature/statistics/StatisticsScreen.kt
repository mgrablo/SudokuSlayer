package com.example.feature.statistics

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.feature.uicore.theme.SudokuSlayerTheme

@Composable
internal fun StatisticsScreen(modifier: Modifier = Modifier) {
	Scaffold(
		modifier = modifier
	) { paddingValues ->
		LazyColumn(
			modifier = Modifier.padding(paddingValues)
		) {

		}
	}
}

@Preview
@Composable
private fun StatisticsScreenPreview() {
	SudokuSlayerTheme {
		StatisticsScreen(
			modifier = Modifier.fillMaxSize()
		)
	}
}
