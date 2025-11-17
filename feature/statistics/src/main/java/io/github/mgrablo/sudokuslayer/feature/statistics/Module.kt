package io.github.mgrablo.sudokuslayer.feature.statistics

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val statisticsModule = module {
	viewModel {
		StatisticsViewModel(
			statisticsRepository = get(),
			settingsRepository = get(),
			hasActiveGameUseCase = get(),
			createNewGameUseCase = get(),
			saveGameUseCase = get(),
		)
	}
}
