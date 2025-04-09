package com.example.feature.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.theme.SudokuSlayerTheme

@Composable
internal fun TableHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderCell(text = "Date", weight = 1f)
        HeaderCell(text = "Difficulty", weight = 1f)
        HeaderCell(text = "Size", weight = 0.8f)
        HeaderCell(text = "Time", weight = 0.8f)
        HeaderCell(text = "Hints", weight = 0.6f)
    }
}

@Composable
private fun RowScope.HeaderCell(text: String, weight: Float, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .weight(weight)
            .padding(vertical = 12.dp, horizontal = 4.dp),
    )
}

@PreviewLightDark
@Composable
private fun TableHeaderPreview() {
    SudokuSlayerTheme {
        Surface {
            TableHeader(modifier = Modifier.fillMaxWidth())
        }
    }
}
