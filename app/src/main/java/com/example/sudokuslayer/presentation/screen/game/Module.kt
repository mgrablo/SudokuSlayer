package com.example.sudokuslayer.presentation.screen.game

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gameModule =
	module {
		viewModel {
			SudokuGameViewModel(
				dataStoreRepository = get(),
				settingsRepository = get(),
				getGameUseCase = get(),
				saveGameUseCase = get(),
				selectCellUseCase = get(),
				inputNumberUseCase = get(),
			)
		}
		viewModel {
			TimerViewModel(
				dataStoreRepository = get(),
			)
		}
	}
