package com.example.feature.game.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.core.HintLog
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintType
import com.example.sudoku.solver.NakedSingleExplanation
import com.example.sudokuslayer.feature.game.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HintBottomSheetScaffold(
	sheetScaffoldState: BottomSheetScaffoldState,
	explainHintClick: () -> Unit,
	nextHintClick: () -> Unit,
	onHighlightCellClick: (Hint) -> Unit,
	modifier: Modifier = Modifier,
	hintLogs: PersistentList<HintLog> = persistentListOf(),
	showNextHint: Boolean = false,
	topBar: @Composable (() -> Unit)? = null,
	content: @Composable (PaddingValues) -> Unit,
) {
	BottomSheetScaffold(
		modifier = modifier,
		scaffoldState = sheetScaffoldState,
		sheetPeekHeight = 128.dp,
		sheetContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
		sheetContentColor = MaterialTheme.colorScheme.onSurface,
		containerColor = MaterialTheme.colorScheme.background,
		contentColor = MaterialTheme.colorScheme.onBackground,
		topBar = topBar,
		sheetContent = {
			SheetContent(
				logs = hintLogs,
				showNextHint = showNextHint,
				explainHintClick = explainHintClick,
				nextHintClick = nextHintClick,
				onHighlightCellClick = onHighlightCellClick,
				modifier = Modifier.heightIn(min = 128.dp, max = 320.dp),
			)
		},
		content = content,
	)
}

@Composable
private fun SheetContent(
	nextHintClick: () -> Unit,
	explainHintClick: () -> Unit,
	onHighlightCellClick: (Hint) -> Unit,
	modifier: Modifier = Modifier,
	showNextHint: Boolean = false,
	logs: PersistentList<HintLog> = persistentListOf(),
) {
	val listState = rememberLazyListState()
	val expandedItems = remember { mutableStateListOf<HintLog>() }
	val interactionSources = remember { mutableStateListOf<MutableInteractionSource>() }
	val coroutineScope = rememberCoroutineScope()

	LaunchedEffect(logs.size) {
		if (logs.isNotEmpty()) {
			listState.animateScrollToItem(logs.size - 1)
		} else {
			interactionSources.clear()
		}
	}

	Column(
		modifier =
		modifier
			.fillMaxSize()
			.padding(horizontal = 16.dp)
			.padding(
				bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
			),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		LazyColumn(
			state = listState,
			modifier =
			Modifier
				.fillMaxWidth()
				.weight(1f),
		) {
			itemsIndexed(items = logs, key = { id, v -> id }) { index, hintLog ->
				val interactionSource = remember { MutableInteractionSource() }
				if (interactionSources.size <= index) {
					interactionSources.add(interactionSource)
				} else {
					interactionSources[index] = interactionSource
				}

				HintStepCard(
					title = "${index + 1}. ${hintLog.explanation.first()}",
					cardContent = hintLog.explanation.drop(1).toPersistentList(),
					onExplainClick = {
						explainHintClick()
						if (!expandedItems.contains(hintLog)) {
							expandedItems.add(hintLog)
						}
					},
					isExpanded = expandedItems.contains(hintLog),
					onExpandToggle = {
						if (expandedItems.contains(hintLog)) {
							expandedItems.remove(hintLog)
						} else {
							expandedItems.add(hintLog)
						}
					},
					onHighlightCellClick = { onHighlightCellClick(hintLog.hint) },
					isRevealed = hintLog.isRevealed,
					isUserGuessed = hintLog.isUserGuessed,
					interactionSource = interactionSource,
					modifier = Modifier.animateItem(),
				)
				Spacer(Modifier.height(8.dp))
			}
		}

		BottomSheetElevatedButton(
			text = "Next hint",
			icon = { Icon(painterResource(R.drawable.lightbulb), null) },
			onClick = {
				if (showNextHint) {
					nextHintClick()
				} else {
					if (interactionSources.isNotEmpty()) {
						coroutineScope.launch {
							val lastInteractionSource = interactionSources.last()
							lastInteractionSource.tryEmit(PressInteraction.Press(Offset.Zero))
							delay(100)
							lastInteractionSource.tryEmit(PressInteraction.Release(PressInteraction.Press(Offset.Zero)))
						}
					}
				}
			},
			contentColor = MaterialTheme.colorScheme.secondary,
		)
	}
}

@Composable
internal fun BottomSheetElevatedButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	icon: @Composable (() -> Unit)? = null,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = MaterialTheme.colorScheme.primary,
) {
	ElevatedButton(
		modifier = modifier,
		onClick = onClick,
		colors =
		ButtonDefaults.elevatedButtonColors(
			containerColor = containerColor,
			contentColor = contentColor,
		),
	) {
		Text(
			text = text,
			color = contentColor,
		)

		if (icon != null) {
			Spacer(Modifier.width(4.dp))
			icon()
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HintBottomSheetScaffoldPreview() {
	SudokuSlayerTheme {
		val scaffoldState =
			rememberBottomSheetScaffoldState(
				bottomSheetState =
				rememberStandardBottomSheetState(
					initialValue = SheetValue.Expanded,
				),
			)
		HintBottomSheetScaffold(
			sheetScaffoldState = scaffoldState,
			hintLogs =
			persistentListOf(
				HintLog(
					id = 1,
					hint =
					Hint(
						row = 1,
						col = 1,
						value = 4,
						type = HintType.NakedSingle,
						explanationStrategy = NakedSingleExplanation(),
						additionalInfo = "",
					),
					isUserGuessed = false,
					isRevealed = false,
					explanation = persistentListOf(),
				),
			),
			explainHintClick = { },
			nextHintClick = { },
			onHighlightCellClick = { },
			topBar = null,
			content = { },
		)
	}
}
