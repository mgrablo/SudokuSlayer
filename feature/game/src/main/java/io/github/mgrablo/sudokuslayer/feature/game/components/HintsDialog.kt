package io.github.mgrablo.sudokuslayer.feature.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.mgrablo.sudokuslayer.feature.game.R
import io.github.mgrablo.sudokuslayer.feature.game.theme.SudokuGameTheme

@Composable
internal fun HintsDialog(
	isVisible: Boolean,
	onDismissRequest: () -> Unit,
	onFillNotesClick: () -> Unit,
	onHintClick: () -> Unit,
	onShowLogsClick: () -> Unit,
	onFindMistakesClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val buttons =
		listOf(
			HintDialogButton(stringResource(R.string.hint_cell), onHintClick, {
				Icon(Icons.Default.Face, null)
			}),
			HintDialogButton(
				stringResource(R.string.show_hint_logs),
				onShowLogsClick,
				{
					Icon(Icons.AutoMirrored.Default.List, null)
				},
			),
			HintDialogButton(
				stringResource(R.string.fill_notes),
				onFillNotesClick,
				{
					Icon(painterResource(R.drawable.stylus_note), null)
				},
			),
			HintDialogButton(
				stringResource(R.string.find_mistakes),
				onFindMistakesClick,
				{
					Icon(painterResource(R.drawable.question_mark), null)
				},
			),
		)

	if (isVisible) {
		Dialog(
			onDismissRequest = onDismissRequest,
		) {
			Box(
				modifier =
				modifier
					.widthIn(min = 280.dp, max = 560.dp)
					.height(400.dp)
					.padding(20.dp)
					.clip(RoundedCornerShape(12.dp)),
			) {
				Card(
					modifier = Modifier.fillMaxSize(),
					colors =
					CardDefaults.cardColors(
						containerColor = MaterialTheme.colorScheme.surface,
					),
					elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
				) {
					Column(
						modifier = Modifier.padding(20.dp),
						horizontalAlignment = Alignment.CenterHorizontally,
					) {
						Icon(painterResource(R.drawable.lightbulb), "")
						Spacer(Modifier.height(20.dp))
						LazyColumn {
							items(buttons) { button ->
								Button(
									onClick = button.onClick,
									colors =
									ButtonDefaults.buttonColors(
										containerColor = MaterialTheme.colorScheme.primaryContainer,
										contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
									),
								) {
									Row(
										Modifier.fillMaxWidth().padding(4.dp),
										horizontalArrangement = Arrangement.Center,
										verticalAlignment = Alignment.CenterVertically,
									) {
										button.icon()
										Spacer(Modifier.width(10.dp))
										Text(
											text = button.text,
											color = MaterialTheme.colorScheme.onPrimaryContainer,
										)
									}
								}
								Spacer(Modifier.height(10.dp))
							}
						}
					}
				}
			}
		}
	}
}

private data class HintDialogButton(
	val text: String,
	val onClick: () -> Unit,
	val icon: @Composable () -> Unit = { },
)

@PreviewLightDark
@Composable
private fun HintsDialogPreview() {
	SudokuGameTheme {
		HintsDialog(
			isVisible = true,
			onDismissRequest = { },
			onHintClick = { },
			onFillNotesClick = { },
			onShowLogsClick = { },
			onFindMistakesClick = { },
		)
	}
}
