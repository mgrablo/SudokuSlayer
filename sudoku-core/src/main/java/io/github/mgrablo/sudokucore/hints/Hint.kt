package io.github.mgrablo.sudokucore.hints

import io.github.mgrablo.sudokucore.model.SudokuCellData
import kotlinx.collections.immutable.PersistentSet

sealed interface Hint {
	val number: Int

	sealed interface HouseType {
		val id: Int

		data class Row(override val id: Int) : HouseType

		data class Column(override val id: Int) : HouseType

		data class Block(override val id: Int) : HouseType
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
		val houseType: HouseType
	}

	/**
	 * Naked Single.
	 *
	 * Occurs when a cell has only one possible candidate remaining.
	 * Since all other digits are already present in the same row, column, or block,
	 * this remaining digit must be the value for the cell.
	 *
	 * @param row Zero-based row index of the resolved cell.
	 * @param col Zero-based column index of the resolved cell.
	 * @param number The digit that must be placed in the cell.
	 */
	data class NakedSingle(override val row: Int, override val col: Int, override val number: Int) :
		ResolutionHint

	/**
	 * Hidden Single.
	 *
	 * Occurs when a candidate digit appears only once in a specific house (row, column, or block).
	 * Even if the cell has other candidates, this digit must be the value for that cell.
	 *
	 * @param houseType The house (row/column/block) where the digit appears only once.
	 * @param row Zero-based row index of the resolved cell.
	 * @param col Zero-based column index of the resolved cell.
	 * @param number The digit that must be placed in the cell.
	 */
	data class HiddenSingle(
		val houseType: HouseType,
		override val row: Int,
		override val col: Int,
		override val number: Int,
	) : ResolutionHint

	/**
	 * Pointing Candidate.
	 *
	 * Occurs when all occurrences of a candidate digit within a block are aligned in a single row or column.
	 * This allows us to eliminate that candidate from all other cells in that same row or column outside the block.
	 *
	 * @param lineHouseType The row or column outside the block where candidates can be eliminated.
	 * @param blockHouseType The block in which the candidates are aligned.
	 * @param number The candidate digit being eliminated.
	 * @param affectedCells Cells outside the block from which the candidate can be removed.
	 * @param enforcingCells Cells inside the block that enforce the alignment.
	 *
	 * @throws IllegalArgumentException if lineHouseType is not of type HouseType.Row or HouseType.Column
	 */
	data class PointingCandidate(
		val lineHouseType: HouseType,
		val blockHouseType: HouseType.Block,
		override val number: Int,
		override val affectedCells: PersistentSet<SudokuCellData>,
		override val enforcingCells: PersistentSet<SudokuCellData>,
	) : EliminationHint {
		override val houseType: HouseType get() = lineHouseType

		init {
			require(lineHouseType is HouseType.Row || lineHouseType is HouseType.Column) {
				"lineHouseType must be either Row or Column"
			}
		}
	}

	/**
	 * Claiming Candidate.
	 *
	 * Occurs when all occurrences of a candidate digit within a row or column are confined to a single block.
	 * This allows us to eliminate that candidate from all other cells in that same block.
	 *
	 * @param lineHouseType The row or column where the candidate is confined.
	 * @param blockHouseType The block in which the candidate is confined.
	 * @param number The candidate digit being eliminated.
	 * @param affectedCells Cells in the block (outside the line) from which the candidate can be removed.
	 * @param enforcingCells Cells in the line that enforce the confinement.
	 *
	 * @throws IllegalArgumentException if lineHouseType is not of type HouseType.Row or HouseType.Column
	 */
	data class ClaimingCandidate(
		val lineHouseType: HouseType,
		val blockHouseType: HouseType.Block,
		override val number: Int,
		override val affectedCells: PersistentSet<SudokuCellData>,
		override val enforcingCells: PersistentSet<SudokuCellData>,
	) : EliminationHint {
		override val houseType: HouseType get() = lineHouseType

		init {
			require(lineHouseType is HouseType.Row || lineHouseType is HouseType.Column) {
				"lineHouseType must be either Row or Column"
			}
		}
	}
}
