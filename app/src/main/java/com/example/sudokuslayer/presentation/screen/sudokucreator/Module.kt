package com.example.sudokuslayer.presentation.screen.sudokucreator

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sudokuCreatorModule =
	module {
		viewModel { SudokuCreatorViewModel(get(), get()) }
	}
