package io.github.mgrablo.sudokuslayer

import io.github.mgrablo.sudokuslayer.data.core.dataCoreModule
import io.github.mgrablo.sudokuslayer.data.game.dataGameModule
import io.github.mgrablo.sudokuslayer.data.settings.dataSettingsModule
import io.github.mgrablo.sudokuslayer.data.statistics.dataStatisticsModule
import io.github.mgrablo.sudokuslayer.feature.creator.sudokuCreatorModule
import io.github.mgrablo.sudokuslayer.feature.game.gameModule
import io.github.mgrablo.sudokuslayer.feature.settings.settingsModule
import io.github.mgrablo.sudokuslayer.feature.statistics.statisticsModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
	module {
		includes(
			dataCoreModule,
			dataSettingsModule,
			dataGameModule,
			dataStatisticsModule,
			sudokuCreatorModule,
			gameModule,
			settingsModule,
			statisticsModule,
		)
		viewModel {
			AppViewModel(
				gameRepository = get(),
				settingsRepository = get(),
			)
		}
	}
