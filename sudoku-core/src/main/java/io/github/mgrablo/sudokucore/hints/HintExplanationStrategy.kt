package io.github.mgrablo.sudokucore.hints

import androidx.compose.runtime.Stable
import io.github.mgrablo.sudokucore.model.SudokuCellData
import io.github.mgrablo.sudokucore.model.SudokuGrid

@Stable
interface HintExplanationStrategy {
	fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider = HintStringProvider.DEFAULT,
	): List<HintExplanationStep>
}

class PointingCandidateExplanation : HintExplanationStrategy {
	override fun generateStructuredHintExplanation(
		grid: SudokuGrid,
		hint: Hint,
		stringProvider: HintStringProvider,
	): List<HintExplanationStep> {
		val hintType = hint.type as HintType.PointingCandidate

		// Get block ID and scope type
		val affectedBlockId =
			(hint.row / grid.subgridSize) * grid.subgridSize + (hint.col / grid.subgridSize) + 1
		val scopeType = if (hintType.groupType is GroupType.Row) ScopeType.ROW else ScopeType.COLUMN

		// Get localized scope name
		val scopeTypeString = getScopeTypeString(scopeType, stringProvider)

		// Get enforcing cells
		val enforcingCells = hint.enforcingCells

		// Group cells by row/column and get their block IDs
		val scopeAndBlockInfo = enforcingCells
			.groupBy { if (hintType.groupType is GroupType.Row) it.row else it.col }
			.map { (index, cells) ->
				val blockId = cells.firstOrNull()?.let {
					(it.row / grid.subgridSize) * grid.subgridSize + (it.col / grid.subgridSize) + 1
				} ?: 0
				Triple(index, blockId, cells.map { Pair(it.row + 1, it.col + 1) })
			}

		// Get the enforcing block ID
		val enforcingBlockId = enforcingCells.firstOrNull()?.let {
			(it.row / grid.subgridSize) * grid.subgridSize + (it.col / grid.subgridSize) + 1
		} ?: 0

		// Get the scope part description (top/middle/bottom row or left/center/right column)
		val enforcingScopePart =
			getScopePartStringLocalized(
				enforcingCells.firstOrNull(),
				hintType.groupType,
				stringProvider,
			)

		// Get the affected cells
		val affectedCells = hint.affectedCells.map { Pair(it.row + 1, it.col + 1) }

		// Build the explanation steps
		val steps = mutableListOf<HintExplanationStep>()

		// Step 1: Focus on the affected block
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.FOCUS_ON_BLOCK)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, affectedBlockId),
					HintExplanationPart.Text("!"),
				),
			),
		)

		// Step 2: Explain for each block where the value is confined to a single row/column
		scopeAndBlockInfo.forEach { (scopeIndex, blockId, cells) ->
			val positionString =
				getScopePartStringLocalized(scopeIndex, hintType.groupType, stringProvider)
			steps.add(
				HintExplanationStep(
					listOf(
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.IN)),
						HintExplanationPart.Text(" "),
						HintExplanationPart.ScopeReference(ScopeType.BLOCK, blockId),
						HintExplanationPart.Text(", "),
						HintExplanationPart.Value(hint.value),
						HintExplanationPart.Text(" "),
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.CAN_ONLY_APPEAR_IN_THE)),
						HintExplanationPart.Text(" "),
						HintExplanationPart.Text(positionString),
						HintExplanationPart.Text(" "),
						HintExplanationPart.Text(scopeTypeString),
						HintExplanationPart.Text("."),
					),
				),
			)
		}

		// Step 3: Explain the pointing pattern and its implication
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.IN)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, affectedBlockId),
					HintExplanationPart.Text(", "),
					HintExplanationPart.Value(hint.value),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.CANNOT_APPEAR_IN_THE)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(enforcingScopePart),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(scopeTypeString),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.SINCE_CONFINED_TO)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(enforcingScopePart),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(scopeTypeString),
					HintExplanationPart.Text(" "),
					HintExplanationPart.Text(stringProvider.getString(HintStringKey.IN)),
					HintExplanationPart.Text(" "),
					HintExplanationPart.ScopeReference(ScopeType.BLOCK, enforcingBlockId),
					HintExplanationPart.Text("."),
				),
			),
		)

		// Step 4: Show affected cells if any
		if (affectedCells.isNotEmpty()) {
			steps.add(
				HintExplanationStep(
					listOf(
						HintExplanationPart.Text(stringProvider.getString(HintStringKey.AFFECTED_CELLS_ARE)),
						HintExplanationPart.Text(" "),
						HintExplanationPart.CellCoordinatesGroup(affectedCells),
						HintExplanationPart.Text("."),
					),
				),
			)
		}

		// Step 5: Technique name
		steps.add(
			HintExplanationStep(
				listOf(
					HintExplanationPart.TechniqueName(
						stringProvider.getString(HintStringKey.TECHNIQUE_POINTING_CANDIDATE),
					),
				),
			),
		)

		return steps
	}
}

private fun getScopePartString(cell: SudokuCellData?, groupType: GroupType): String {
	if (cell == null) return "null"
	return when (groupType) {
		is GroupType.Row ->
			when (cell.row % 3) {
				0 -> "top"
				1 -> "middle"
				2 -> "bottom"
				else -> "null"
			}

		is GroupType.Column ->
			when (cell.col % 3) {
				0 -> "left"
				1 -> "center"
				2 -> "right"
				else -> "null"
			}

		else -> "null"
	}
}

private fun getScopePartString(number: Int, groupType: GroupType): String = when (groupType) {
	is GroupType.Row ->
		when (number % 3) {
			0 -> "top"
			1 -> "middle"
			2 -> "bottom"
			else -> "null"
		}

	is GroupType.Column ->
		when (number % 3) {
			0 -> "left"
			1 -> "center"
			2 -> "right"
			else -> "null"
		}

	else -> "null"
}

private fun getScopePartStringLocalized(
	cell: SudokuCellData?,
	groupType: GroupType,
	stringProvider: HintStringProvider,
): String {
	if (cell == null) return "null"
	return when (groupType) {
		is GroupType.Row ->
			when (cell.row % 3) {
				0 -> stringProvider.getString(HintStringKey.TOP)
				1 -> stringProvider.getString(HintStringKey.MIDDLE)
				2 -> stringProvider.getString(HintStringKey.BOTTOM)
				else -> "null"
			}

		is GroupType.Column ->
			when (cell.col % 3) {
				0 -> stringProvider.getString(HintStringKey.LEFT)
				1 -> stringProvider.getString(HintStringKey.CENTER)
				2 -> stringProvider.getString(HintStringKey.RIGHT)
				else -> "null"
			}

		else -> "null"
	}
}

internal fun getScopePartStringLocalized(
	number: Int,
	groupType: GroupType,
	stringProvider: HintStringProvider,
): String = when (groupType) {
	is GroupType.Row ->
		when (number % 3) {
			0 -> stringProvider.getString(HintStringKey.TOP)
			1 -> stringProvider.getString(HintStringKey.MIDDLE)
			2 -> stringProvider.getString(HintStringKey.BOTTOM)
			else -> "null"
		}

	is GroupType.Column ->
		when (number % 3) {
			0 -> stringProvider.getString(HintStringKey.LEFT)
			1 -> stringProvider.getString(HintStringKey.CENTER)
			2 -> stringProvider.getString(HintStringKey.RIGHT)
			else -> "null"
		}

	else -> "null"
}

internal fun getScopeTypeString(scopeType: ScopeType, stringProvider: HintStringProvider): String =
	when (scopeType) {
		ScopeType.ROW -> stringProvider.getString(HintStringKey.ROW)
		ScopeType.COLUMN -> stringProvider.getString(HintStringKey.COLUMN)
		ScopeType.BLOCK -> stringProvider.getString(HintStringKey.BLOCK)
		ScopeType.BLOCK_PART -> stringProvider.getString(HintStringKey.BLOCK_PART)
	}
