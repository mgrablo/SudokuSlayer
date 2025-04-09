package com.example.feature.statistics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.feature.uicore.theme.SudokuSlayerTheme

@Composable
internal fun FilterScreen(modifier: Modifier = Modifier) {
	Scaffold(
		modifier = modifier,
	) { paddingValues ->
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues),
		) {
			Text("Filter screen")
		}
	}
}

@Preview
@Composable
private fun FilterScreenPreview() {
	SudokuSlayerTheme {
		FilterScreen()
	}
}
