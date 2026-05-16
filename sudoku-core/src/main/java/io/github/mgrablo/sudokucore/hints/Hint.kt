package io.github.mgrablo.sudokucore.hints

import io.github.mgrablo.sudokucore.hints.strategies.ClaimingCandidateExplanation
import io.github.mgrablo.sudokucore.hints.strategies.HiddenSingleExplanation
import io.github.mgrablo.sudokucore.hints.strategies.NakedSingleExplanation
import io.github.mgrablo.sudokucore.hints.strategies.PointingCandidateExplanation
import io.github.mgrablo.sudokucore.model.SudokuCellData
import kotlinx.collections.immutable.PersistentSet

sealed interface Hint {
	val number: Int

	sealed interface GroupType {
		val id: Int

		data class Row(override val id: Int) : GroupType

		data class Column(override val id: Int) : GroupType

		data class Block(override val id: Int) : GroupType
	}

	// Hints that determine the final value for a cell
	sealed interface ResolutionHint : Hint {
		val row: Int
		val col: Int
	}

	// Hints that eliminate candidates from other cells
	sealed interface EliminationHint : Hint {
		val affectedCells: PersistentSet<SudokuCellData>
		val enforcingCells: PersistentSet<SudokuCellData>
		val groupType: GroupType
	}

	data class NakedSingle(override val row: Int, override val col: Int, override val number: Int) :
		ResolutionHint

	data class HiddenSingle(
		val groupType: GroupType,
		override val row: Int,
		override val col: Int,
		override val number: Int,
	) : ResolutionHint

	data class PointingCandidate(
		override val number: Int,
		override val groupType: GroupType,
		override val affectedCells: PersistentSet<SudokuCellData>,
		override val enforcingCells: PersistentSet<SudokuCellData>,
	) : EliminationHint

	data class ClaimingCandidate(
		override val number: Int,
		override val groupType: GroupType,
		override val affectedCells: PersistentSet<SudokuCellData>,
		override val enforcingCells: PersistentSet<SudokuCellData>,
	) : EliminationHint

	val explanationStrategy get() = when (this) {
		is NakedSingle -> NakedSingleExplanation()
		is HiddenSingle -> HiddenSingleExplanation()
		is PointingCandidate -> PointingCandidateExplanation()
		is ClaimingCandidate -> ClaimingCandidateExplanation()
	}
}
