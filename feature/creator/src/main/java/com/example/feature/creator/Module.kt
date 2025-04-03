package com.example.feature.creator

import com.example.domain.creator.domainCreatorModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sudokuCreatorModule =
	module {
		includes(domainCreatorModule)
		viewModel { SudokuCreatorViewModel(get(), get(), get()) }
	}
