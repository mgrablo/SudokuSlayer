package io.github.mgrablo.sudokuslayer.data.settings

import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
import org.koin.dsl.module

val dataSettingsModule =
	module {
		single<SettingsRepository> {
			AndroidSettingsRepository(
				preferenceStorage = get(),
			)
		}
	}
