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

internal fun getScopePartStringLocalized(
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
