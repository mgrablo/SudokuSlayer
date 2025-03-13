package com.example.sudokuslayer

import com.example.data.core_android.coreAndroidModule
import com.example.data.game.dataGameModule
import com.example.data.settings.dataSettingsModule
import com.example.domain.game.domainGameModule
import com.example.sudokuslayer.presentation.screen.game.gameModule
import com.example.sudokuslayer.presentation.screen.sudokucreator.sudokuCreatorModule
import org.koin.dsl.module

val appModule =
	module {
		includes(
			coreAndroidModule,
			gameModule,
			dataSettingsModule,
			dataGameModule,
			sudokuCreatorModule,
			domainGameModule,
		)
	}
