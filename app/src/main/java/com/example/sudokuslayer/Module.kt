package com.example.sudokuslayer

import com.example.sudokuslayer.data.core.dataCoreModule
import com.example.sudokuslayer.data.game.dataGameModule
import com.example.sudokuslayer.data.settings.dataSettingsModule
import com.example.sudokuslayer.data.statistics.dataStatisticsModule
import com.example.sudokuslayer.feature.creator.sudokuCreatorModule
import com.example.sudokuslayer.feature.game.gameModule
import com.example.sudokuslayer.feature.settings.settingsModule
import com.example.sudokuslayer.feature.statistics.statisticsModule
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
