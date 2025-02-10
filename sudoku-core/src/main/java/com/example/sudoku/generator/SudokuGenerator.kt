package com.example.sudoku.generator

import com.example.sudoku.model.SudokuGrid
import kotlin.random.Random

interface SudokuGenerator {
    suspend fun createSudoku(cellsToRemove: Int, seed: Long = Random.nextLong()): SudokuGrid
    suspend fun generateFullSudokuGrid(seed: Long = Random.nextLong()): SudokuGrid
    suspend fun removeNumbers(grid: SudokuGrid, cellsToRemove: Int): SudokuGrid
}