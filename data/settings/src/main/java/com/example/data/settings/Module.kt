package com.example.data.settings

import org.koin.dsl.module

val dataSettingsModule =
	module {
		single { SettingsRepository() }
	}
