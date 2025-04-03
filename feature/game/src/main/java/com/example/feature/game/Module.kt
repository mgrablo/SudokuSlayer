package com.example.feature.game

import com.example.domain.game.ElapsedTimerManager
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gameModule =
	module {
		viewModel {
			SudokuGameViewModel(
				settingsRepository = get(),
				operationRepository = get(),
				getGameUseCase = get(),
				saveGameUseCase = get(),
				selectCellUseCase = get(),
				inputNumberUseCase = get(),
				provideHintUseCase = get(),
				focusOnHintCellsUseCase = get(),
				generateHintLogUseCase = get(),
				revealHintOnGridUseCase = get(),
				revealLastHintLogUseCase = get(),
				undoOperationUseCase = get(),
				redoOperationUseCase = get(),
				resetGameUseCase = get(),
				elapsedTimerManager = get(),
			)
		}
		factory { ElapsedTimerManager(get(), get()) }
	}
