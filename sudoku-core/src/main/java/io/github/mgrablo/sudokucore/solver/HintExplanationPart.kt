package io.github.mgrablo.sudokucore.solver

/**
 * Represents different types of parts that can appear in a hint explanation.
 */
sealed class HintExplanationPart {
	/**
	 * Regular text in the explanation
	 */
	data class Text(val content: String) : HintExplanationPart()

	/**
	 * Highlighted cell coordinates like [1, 2]
	 */
	data class CellCoordinate(val row: Int, val col: Int) : HintExplanationPart()

	/**
	 * A group of cell coordinates
	 */
	data class CellCoordinatesGroup(val cells: List<Pair<Int, Int>>) : HintExplanationPart()

	/**
	 * A sudoku value that should be emphasized
	 */
	data class Value(val value: Int) : HintExplanationPart()

	/**
	 * A hint technique name like "Naked Single"
	 */
	data class TechniqueName(val name: String) : HintExplanationPart()

	/**
	 * A reference to a row, column or block
	 */
	data class ScopeReference(val type: ScopeType, val index: Int?) : HintExplanationPart()

	/**
	 * A range of values like {1, 2, 3}
	 */
	data class ValueGroup(val values: List<Int>) : HintExplanationPart()
}

/**
 * Type of scope being referenced in the explanation
 */
enum class ScopeType {
	ROW,
	COLUMN,
	BLOCK,
	BLOCK_PART,
}

/**
 * Represents a complete hint explanation step that consists of multiple parts
 */
data class HintExplanationStep(val parts: List<HintExplanationPart>)
