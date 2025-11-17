package com.example.sudokuslayer.data.settings

import com.example.sudokuslayer.domain.settings.SettingsRepository
import org.koin.dsl.module

val dataSettingsModule =
	module {
		single<SettingsRepository> {
			AndroidSettingsRepository(
				preferenceStorage = get(),
			)
		}
	}
