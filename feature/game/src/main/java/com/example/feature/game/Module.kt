package com.example.feature.game

import com.example.domain.game.ElapsedTimerManager
import com.example.domain.game.domainGameModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gameModule =
	module {
		includes(domainGameModule)
		viewModel {
			SudokuGameViewModel(
				settingsRepository = get(),
				gameManagementUseCases = get(),
				hintUseCases = get(),
				operationRepository = get(),
				undoOperationUseCase = get(),
				redoOperationUseCase = get(),
				getBestTimeUseCase = get(),
				elapsedTimerManager = get(),
				gameResultWriter = get(),
			)
		}
		factory { ElapsedTimerManager(get(), get()) }
	}
