package com.example.sudoku

import com.example.sudoku.solver.HintProvider
import org.koin.dsl.module

val sudokuModule =
	module {
		single { HintProvider() }
	}
