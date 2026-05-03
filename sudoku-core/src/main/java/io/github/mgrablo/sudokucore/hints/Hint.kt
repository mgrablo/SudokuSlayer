package io.github.mgrablo.sudokucore.hints

import androidx.compose.runtime.Stable
import io.github.mgrablo.sudokucore.model.SudokuCellData
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

@Stable
sealed interface HintType {
	object NakedSingle : HintType

	data class HiddenSingle(val groupType: GroupType) : HintType

	data class PointingCandidate(val groupType: GroupType) : HintType

	data class ClaimingCandidate(val groupType: GroupType) : HintType
}

@Stable
sealed interface GroupType {
	val id: Int

	data class Row(override val id: Int) : GroupType

	data class Column(override val id: Int) : GroupType

	data class Block(override val id: Int) : GroupType
}

@Stable
data class Hint(
	val row: Int,
	val col: Int,
	val value: Int,
	val type: HintType,
	val explanationStrategy: HintExplanationStrategy? = null,
	val additionalInfo: String = "",
	val affectedCells: PersistentSet<SudokuCellData> = persistentSetOf(),
	val enforcingCells: PersistentSet<SudokuCellData> = persistentSetOf(),
)
