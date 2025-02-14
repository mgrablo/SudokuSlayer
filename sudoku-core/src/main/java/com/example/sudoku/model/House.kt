package com.example.sudoku.model

sealed interface House {
	val cells: List<SudokuCellData>
	val id: Int

	class Row(override val cells: List<SudokuCellData>, override val id: Int) : House

	class Column(override val cells: List<SudokuCellData>, override val id: Int) : House

	class Block(override val cells: List<SudokuCellData>, override val id: Int) : House
}
