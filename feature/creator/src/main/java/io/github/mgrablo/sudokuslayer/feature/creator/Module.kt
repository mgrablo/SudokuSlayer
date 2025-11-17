package io.github.mgrablo.sudokuslayer.feature.creator

import io.github.mgrablo.sudokuslayer.domain.creator.domainCreatorModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sudokuCreatorModule =
	module {
		includes(domainCreatorModule)
		viewModel { parameters ->
			SudokuCreatorViewModel(
				createNewGameUseCase = get(),
				getSavedGameUseCase = get(),
				saveGameUseCase = get(),
				hasActiveGameUseCase = get(),
				validateSeedInputUseCase = get(),
				args = parameters.getOrNull(),
			)
		}
	}
