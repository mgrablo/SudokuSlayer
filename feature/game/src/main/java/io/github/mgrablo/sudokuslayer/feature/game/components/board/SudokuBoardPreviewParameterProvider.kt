package io.github.mgrablo.sudokuslayer.feature.game.components.board

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.mgrablo.sudokucore.model.CellAttributes
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid
import kotlinx.collections.immutable.persistentSetOf

internal class SudokuBoardPreviewParameterProvider :
	PreviewParameterProvider<Pair<SudokuGrid, String>> {
	override val values: Sequence<Pair<SudokuGrid, String>> = sequenceOf(
		// 9x9 Grid Examples
		Pair(
			SudokuGrid(9).withReplacedCell(
				0,
				0,
				SudokuCellData(0, 0, 5, attributes = persistentSetOf(CellAttributes.GENERATED)),
			),
			"9x9 Generated Cell",
		),
		Pair(SudokuGrid(9).withReplacedCell(0, 0, SudokuCellData(0, 0, 3)), "9x9 User Input Cell"),
		Pair(SudokuGrid(9).withReplacedCell(0, 0, SudokuCellData(0, 0, 0)), "9x9 Empty Cell"),
		Pair(
			SudokuGrid(9).withReplacedCell(
				0,
				0,
				SudokuCellData(0, 0, cornerNotes = persistentSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)),
			),
			"9x9 Corner Notes Cell",
		),
		Pair(
			SudokuGrid(9).withReplacedCell(
				0,
				0,
				SudokuCellData(0, 0, candidates = persistentSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)),
			),
			"9x9 Center Notes Cell",
		),
		Pair(
			SudokuGrid(9).withReplacedCell(
				0,
				0,
				SudokuCellData(0, 0, 7, attributes = persistentSetOf(CellAttributes.HINT_REVEALED)),
			),
			"9x9 Hint Revealed Cell",
		),
		Pair(
			SudokuGrid(9).withReplacedCell(
				0,
				0,
				SudokuCellData(0, 0, 1, attributes = persistentSetOf(CellAttributes.RULE_BREAKING)),
			),
			"9x9 Rule Breaking Cell",
		),
		Pair(
			SudokuGrid(9).withReplacedCell(
				0,
				0,
				SudokuCellData(
					0,
					0,
					2,
					attributes = persistentSetOf(CellAttributes.SOLUTION_CONFLICT),
				),
			),
			"9x9 Solution Conflict Cell",
		),
		Pair(
			SudokuGrid(9).withReplacedCell(
				0,
				0,
				SudokuCellData(0, 0, 4, attributes = persistentSetOf(CellAttributes.SELECTED)),
			),
			"9x9 Selected Cell",
		),
		Pair(
			SudokuGrid(9).withReplacedCell(
				0,
				0,
				SudokuCellData(
					0,
					0,
					6,
					attributes = persistentSetOf(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
				),
			),
			"9x9 Number Match Highlighted Cell",
		),
		Pair(
			SudokuGrid(9).withReplacedCell(
				0,
				0,
				SudokuCellData(
					0,
					0,
					8,
					attributes = persistentSetOf(CellAttributes.ROW_COLUMN_HIGHLIGHTED),
				),
			),
			"9x9 Row/Column Highlighted Cell",
		),
		Pair(
			SudokuGrid(9)
				.withReplacedCell(
					0,
					0,
					SudokuCellData(0, 0, 1, attributes = persistentSetOf(CellAttributes.GENERATED)),
				)
				.withReplacedCell(
					0,
					1,
					SudokuCellData(0, 1, 2, attributes = persistentSetOf(CellAttributes.SELECTED)),
				)
				.withReplacedCell(
					0,
					2,
					SudokuCellData(0, 2, 3, cornerNotes = persistentSetOf(4, 5, 6)),
				)
				.withReplacedCell(
					1,
					0,
					SudokuCellData(
						1,
						0,
						4,
						attributes = persistentSetOf(CellAttributes.HINT_REVEALED),
					),
				)
				.withReplacedCell(
					1,
					1,
					SudokuCellData(
						1,
						1,
						5,
						attributes = persistentSetOf(CellAttributes.RULE_BREAKING),
					),
				)
				.withReplacedCell(
					1,
					2,
					SudokuCellData(1, 2, 6, candidates = persistentSetOf(7, 8, 9)),
				)
				.withReplacedCell(
					2,
					0,
					SudokuCellData(
						2,
						0,
						7,
						attributes = persistentSetOf(CellAttributes.SOLUTION_CONFLICT),
					),
				)
				.withReplacedCell(
					2,
					1,
					SudokuCellData(
						2,
						1,
						8,
						attributes = persistentSetOf(CellAttributes.NUMBER_MATCH_HIGHLIGHTED),
					),
				)
				.withReplacedCell(
					2,
					2,
					SudokuCellData(
						2,
						2,
						9,
						attributes = persistentSetOf(CellAttributes.ROW_COLUMN_HIGHLIGHTED),
					),
				),
			"9x9 Mixed Grid",
		),

		// 4x4 Grid Example
		Pair(
			SudokuGrid(4)
				.withReplacedCell(
					0,
					0,
					SudokuCellData(0, 0, 1, attributes = persistentSetOf(CellAttributes.GENERATED)),
				)
				.withReplacedCell(
					0,
					1,
					SudokuCellData(0, 1, 2, attributes = persistentSetOf(CellAttributes.SELECTED)),
				)
				.withReplacedCell(
					1,
					0,
					SudokuCellData(1, 0, 3, cornerNotes = persistentSetOf(1, 4)),
				)
				.withReplacedCell(
					1,
					1,
					SudokuCellData(
						1,
						1,
						4,
						attributes = persistentSetOf(CellAttributes.RULE_BREAKING),
					),
				)
				.withReplacedCell(
					1,
					2,
					SudokuCellData(1, 1, 0, cornerNotes = persistentSetOf(1, 2, 3, 4)),
				),
			"4x4 Mixed Grid",
		),

		// 16x16 Grid Example (sparse)
		Pair(
			SudokuGrid(16)
				.withReplacedCell(
					0,
					0,
					SudokuCellData(0, 0, 1, attributes = persistentSetOf(CellAttributes.GENERATED)),
				)
				.withReplacedCell(
					0,
					1,
					SudokuCellData(0, 1, 2, attributes = persistentSetOf(CellAttributes.SELECTED)),
				)
				.withReplacedCell(
					1,
					0,
					SudokuCellData(1, 0, 3, cornerNotes = persistentSetOf(10, 11, 12)),
				)
				.withReplacedCell(
					1,
					1,
					SudokuCellData(
						1,
						1,
						4,
						attributes = persistentSetOf(CellAttributes.RULE_BREAKING),
					),
				)
				.withReplacedCell(
					15,
					15,
					SudokuCellData(
						15,
						15,
						0,
						cornerNotes = persistentSetOf(
							1,
							2,
							3,
							4,
							5,
							6,
							7,
							8,
							9,
							10,
							11,
							12,
							13,
							14,
							15,
						),
					),
				),
			"16x16 Mixed Grid (Sparse)",
		),
	)
}
