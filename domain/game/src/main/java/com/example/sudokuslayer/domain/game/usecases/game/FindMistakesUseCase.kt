package com.example.sudokuslayer.domain.game.usecases.game

import com.example.sudoku.model.CellAttributes
import com.example.sudokuslayer.domain.core.Game

class FindMistakesUseCase {
	operator fun invoke(game: Game): List<Pair<Int, Int>> {
		val mistakes = mutableListOf<Pair<Int, Int>>()
		val gameGrid = game.grid
		val solutionGrid = game.solution

		for (row in 0 until gameGrid.gridSize) {
			for (col in 0 until gameGrid.gridSize) {
				val cell = gameGrid.getCellAt(row, col)
				// A mistake is a non-generated cell with a number that doesn't match the solution
				if (cell.number != 0 && !cell.attributes.contains(CellAttributes.GENERATED)) {
					if (cell.number != solutionGrid.getValue(row, col)) {
						mistakes.add(row to col)
					}
				}
			}
		}
		return mistakes
	}
}
