package com.example.feature.game

import com.example.domain.game.domainGameModule
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gameModule =
	module {
		includes(domainGameModule)
		viewModel {
			SudokuGameViewModel(
				application = androidApplication(),
				settingsRepository = get(),
				gameManagementUseCases = get(),
				hintUseCases = get(),
				operationRepository = get(),
				undoOperationUseCase = get(),
				redoOperationUseCase = get(),
				recordUndoOperation = get(),
				getBestTimeUseCase = get(),
				getElapsedTimeUseCase = get(),
				saveElapsedTimeUseCase = get(),
				gameResultWriter = get(),
			)
		}
	}
