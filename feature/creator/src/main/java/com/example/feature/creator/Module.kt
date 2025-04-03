package com.example.feature.creator

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sudokuCreatorModule =
	module {
		viewModel { SudokuCreatorViewModel(get(), get(), get()) }
	}
