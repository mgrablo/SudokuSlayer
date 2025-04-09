package com.example.sudokuslayer

import com.example.data.core.dataCoreModule
import com.example.data.game.dataGameModule
import com.example.data.settings.dataSettingsModule
import com.example.feature.creator.sudokuCreatorModule
import com.example.feature.game.gameModule
import com.example.feature.settings.settingsModule
import com.example.feature.statistics.statisticsModule
import org.koin.dsl.module

val appModule =
	module {
		includes(
			dataCoreModule,
			dataSettingsModule,
			dataGameModule,
			sudokuCreatorModule,
			gameModule,
			settingsModule,
			statisticsModule,
		)
	}
