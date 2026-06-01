package io.github.mgrablo.sudokuslayer.feature.game.components

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
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokuslayer.domain.core.HintLog
import io.github.mgrablo.sudokuslayer.feature.game.R
import io.github.mgrablo.sudokuslayer.feature.game.components.snackbar.GameSnackbar
import io.github.mgrablo.sudokuslayer.feature.game.components.snackbar.GameSnackbarHost
import io.github.mgrablo.sudokuslayer.feature.game.hints.toExplanation
import io.github.mgrablo.sudokuslayer.feature.game.model.SnackbarState
import io.github.mgrablo.sudokuslayer.feature.game.theme.SudokuGameTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
private fun HintLog.toHintLogCardState(isExpanded: Boolean) = HintLogCardState(
	hintExplanation = hint.toExplanation(LocalContext.current),
	isRevealed = isRevealed,
	isUserGuessed = isUserGuessed,
	isExpanded = isExpanded,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HintBottomSheetScaffold(
	sheetScaffoldState: BottomSheetScaffoldState,
	snackbarState: SnackbarState?,
	explainHintClick: () -> Unit,
	nextHintClick: () -> Unit,
	onHighlightCellClick: (Hint) -> Unit,
	onShowMistakes: () -> Unit,
	onDismissSnackbar: () -> Unit,
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
		snackbarHost = { hostState ->
			GameSnackbarHost(
				snackbarHostState = hostState,
				snackbarState = snackbarState,
				dismissBoxState = rememberSwipeToDismissBoxState(
					initialValue = SwipeToDismissBoxValue.Settled,
				),
				onDismissSnackbar = onDismissSnackbar,
				onShowMistakes = onShowMistakes,
				snackbar = { data ->
					GameSnackbar(data)
				},
			)
		},
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
	var expandedLogId by remember { mutableStateOf<Int?>(null) }
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
			itemsIndexed(items = logs, key = { i, v -> v.id }) { index, hintLog ->
				val interactionSource = remember { MutableInteractionSource() }
				if (interactionSources.size <= index) {
					interactionSources.add(interactionSource)
				} else {
					interactionSources[index] = interactionSource
				}

				val cardState = hintLog.toHintLogCardState(isExpanded = expandedLogId == hintLog.id)
				HintLogCard(
					state = cardState,
					interactionSource = interactionSource,
					onExplainClick = {
						explainHintClick()
						expandedLogId = hintLog.id
					},
					onHighlightCellClick = { onHighlightCellClick(hintLog.hint) },
					onExpandToggle = {
						expandedLogId = if (expandedLogId == hintLog.id) null else hintLog.id
						coroutineScope.launch {
							listState.animateScrollToItem(index)
						}
					},
					modifier = Modifier.animateItem(),
				)

				Spacer(Modifier.height(8.dp))
			}
		}

		BottomSheetElevatedButton(
			text = stringResource(R.string.next_hint),
			icon = { Icon(painterResource(R.drawable.lightbulb), null) },
			onClick = {
				if (showNextHint) {
					nextHintClick()
				} else {
					if (interactionSources.isNotEmpty()) {
						coroutineScope.launch {
							val lastInteractionSource = interactionSources.last()
							triggerRippleAnimation(lastInteractionSource)
						}
					}
				}
			},
			contentColor = MaterialTheme.colorScheme.secondary,
		)
	}
}

private suspend fun triggerRippleAnimation(interactionSource: MutableInteractionSource) {
	interactionSource.tryEmit(PressInteraction.Press(Offset.Zero))
	delay(200.milliseconds)
	interactionSource.tryEmit(
		PressInteraction.Release(
			PressInteraction.Press(
				Offset.Zero,
			),
		),
	)
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
	SudokuGameTheme {
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
							Hint.NakedSingle(
								row = 1,
								col = 1,
								number = 4,
							),
						isUserGuessed = false,
						isRevealed = false,
					),
				),
			explainHintClick = { },
			nextHintClick = { },
			onHighlightCellClick = { },
			topBar = null,
			onShowMistakes = { },
			onDismissSnackbar = { },
			snackbarState = null,
			content = { },
		)
	}
}
