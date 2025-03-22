package com.example.data.settings

import com.example.domain.settings.SettingsRepository
import org.koin.dsl.module

val dataSettingsModule =
	module {
		single<SettingsRepository> { AndroidSettingsRepository() }
	}
