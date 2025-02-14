package com.example.sudokuslayer.presentation.screen.game.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.model.SudokuCellData
import com.example.sudokuslayer.presentation.ui.theme.extendedColorScheme
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay

@Composable
private fun rememberCellColors(
	attributeStates: CellAttributeStates,
	defaultBackgroundColor: Color = MaterialTheme.colorScheme.background,
	selectedBackgroundColor: Color = MaterialTheme.colorScheme.surfaceTint,
	highlightedBackgroundColor: Color = MaterialTheme.colorScheme.surface,
	numberSelectedBackgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
	breakingRulesBackgroundColor: Color = MaterialTheme.colorScheme.error,
	hintRevealedTextColor: Color = MaterialTheme.extendedColorScheme.yellow.onColorContainer,
	hintFocusTextColor: Color = MaterialTheme.extendedColorScheme.yellow.colorContainer,
	numberSelectedTextColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
	breakingRulesTextColor: Color = MaterialTheme.colorScheme.onError,
	generatedTextColor: Color = MaterialTheme.colorScheme.onSurface,
	userInputTextColor: Color = MaterialTheme.colorScheme.secondary,
): @Composable CellColors =
	remember(attributeStates) {
		val isNumberHighlightApplicable =
			attributeStates.run {
				(isBreakingRules || isHintRevealed || isNumberHighlighted)
			}

		val circleColor =
			when {
				attributeStates.isBreakingRules -> breakingRulesBackgroundColor
				attributeStates.isHintRevealed -> hintFocusTextColor
				isNumberHighlightApplicable -> numberSelectedBackgroundColor
				else -> Color.Transparent
			}

		CellColors(
			background =
				when {
					attributeStates.isSelected -> selectedBackgroundColor
					attributeStates.isRowColumnHighlighted -> highlightedBackgroundColor
					else -> defaultBackgroundColor
				},
			text =
				when {
					attributeStates.isHintRevealed -> hintRevealedTextColor
					attributeStates.isHintFocus -> hintFocusTextColor
					attributeStates.isBreakingRules -> breakingRulesTextColor
					attributeStates.isNumberHighlighted -> numberSelectedTextColor
					attributeStates.isGenerated -> generatedTextColor
					else -> userInputTextColor
				},
			circle = circleColor,
		)
	}

private data class CellColors(
	val background: Color,
	val text: Color,
	val circle: Color,
)

data class CellAttributeStates(
	val isSelected: Boolean = false,
	val isRowColumnHighlighted: Boolean = false,
	val isBreakingRules: Boolean = false,
	val isHintRevealed: Boolean = false,
	val isNumberHighlighted: Boolean = false,
	val isGenerated: Boolean = false,
	val isHintFocus: Boolean = false,
)

@Composable
fun SudokuCell(
	cellData: SudokuCellData,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	attributeStates: CellAttributeStates = CellAttributeStates(),
) {
	val colors = rememberCellColors(attributeStates)
	val fontWeight = if (attributeStates.isGenerated) FontWeight.Bold else FontWeight.Normal

	Surface(
		color = colors.background,
		modifier =
			Modifier
				.fillMaxSize()
				.aspectRatio(1f)
				.clickable(onClick = onClick),
	) {
		if (attributeStates.isHintFocus) {
			val animationDuration = 5000
			val animationFrequency = 10
			val infiniteTransition = rememberInfiniteTransition()
			val animationColor by infiniteTransition.animateColor(
				initialValue = MaterialTheme.extendedColorScheme.yellow.colorContainer,
				targetValue = MaterialTheme.colorScheme.background,
				animationSpec =
					infiniteRepeatable(
						animation = tween(animationDuration / animationFrequency, easing = LinearEasing),
						repeatMode = RepeatMode.Reverse,
					),
			)

			var showAnimation by remember { mutableStateOf(true) }

			LaunchedEffect(Unit) {
				delay(animationDuration.toLong())
				showAnimation = false
			}

			if (showAnimation) {
				Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
					val strokeWidth = 4.dp.toPx()
					drawRect(
						color = animationColor,
						size = size,
						style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
					)
				}
			}
		}

		// Always show ElevatedSurface with circle color (will be transparent when not needed)
		ElevatedSurface(
			color = colors.circle,
			shadowElevation = 0.dp,
			modifier =
				Modifier
					.fillMaxSize()
					.padding(2.dp),
		) {
			CellContent(
				cellData = cellData,
				textColor = colors.text,
				fontWeight = fontWeight,
			)
		}
	}
}

@Composable
fun ElevatedSurface(
	color: Color,
	modifier: Modifier = Modifier,
	shadowElevation: Dp = 6.dp,
	tonalElevation: Dp = 6.dp,
	content: @Composable () -> Unit,
) {
	Surface(
		color = color,
		shape = CircleShape,
		shadowElevation = shadowElevation,
		tonalElevation = tonalElevation,
		modifier = modifier,
	) {
		content()
	}
}

@Composable
fun CellContent(
	cellData: SudokuCellData,
	textColor: Color,
	fontWeight: FontWeight,
	modifier: Modifier = Modifier,
) {
	if (cellData.number != 0) {
		Text(
			text = cellData.number.toString(),
			modifier =
				Modifier
					.fillMaxSize()
					.wrapContentHeight(align = Alignment.CenterVertically),
			style = MaterialTheme.typography.bodyLarge,
			textAlign = TextAlign.Center,
			fontWeight = fontWeight,
			color = textColor,
		)
	} else {
		// Create a fixed 3x3 grid for notes
		Box(modifier = Modifier.fillMaxSize()) {
			LazyVerticalGrid(
				columns = GridCells.Fixed(3),
				modifier =
					Modifier
						.fillMaxSize()
						.padding(2.dp),
				// Add padding to prevent clipping
				horizontalArrangement = Arrangement.SpaceEvenly,
				verticalArrangement = Arrangement.SpaceEvenly,
			) {
				// Create all 9 positions, show number if it exists in notes
				items(9) { position ->
					val number = position + 1
					if (number in cellData.cornerNotes) {
						Text(
							text = number.toString(),
							modifier = Modifier.wrapContentHeight(),
							fontSize = 8.sp,
							lineHeight = 8.sp,
							textAlign = TextAlign.Center,
							fontWeight = fontWeight,
							color = textColor,
							maxLines = 1,
						)
					} else {
						// Empty space holder to maintain grid structure
						Text(
							text = "",
							modifier = Modifier.wrapContentHeight(),
							fontSize = 8.sp,
							lineHeight = 8.sp,
						)
					}
				}
			}
		}
	}
}

@Preview(name = "Empty cell", group = "SudokuCellPreview")
@Composable
private fun SudokuEmptyCellPreview() {
	SudokuCell(
		cellData = SudokuCellData(0, 0),
		onClick = { },
	)
}

@Preview(name = "Cell with generated number", group = "SudokuCellPreview")
@Composable
private fun SudokuGeneratedCellPreview() {
	SudokuCell(
		cellData = SudokuCellData(0, 0, 4),
		onClick = { },
	)
}

@Preview(name = "Cell with users number", group = "SudokuCellPreview")
@Composable
private fun SudokuFilledCellPreview() {
	SudokuCell(
		cellData = SudokuCellData(0, 0, 6),
		onClick = { },
	)
}

@Preview(name = "Cell with notes", group = "SudokuCellPreview")
@Composable
private fun SudokuNotesCellPreview() {
	SudokuCell(
		cellData =
			SudokuCellData(
				0,
				0,
				0,
				cornerNotes = persistentSetOf(1, 2, 5, 7, 9),
			),
		onClick = { },
	)
}
