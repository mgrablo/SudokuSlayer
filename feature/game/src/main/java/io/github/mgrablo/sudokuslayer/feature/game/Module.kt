package io.github.mgrablo.sudokuslayer.feature.game

import io.github.mgrablo.sudokuslayer.domain.game.domainGameModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gameModule =
	module {
		includes(domainGameModule)
		viewModel { parameters ->
			SudokuGameViewModel(
				sudokuGridSize = parameters.get(),
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
