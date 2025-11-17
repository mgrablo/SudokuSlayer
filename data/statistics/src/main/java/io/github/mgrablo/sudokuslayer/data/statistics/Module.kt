package io.github.mgrablo.sudokuslayer.data.statistics

import io.github.mgrablo.sudokuslayer.domain.statistics.StatisticsRepository
import org.koin.dsl.module

val dataStatisticsModule = module {
	single<StatisticsRepository> {
		AndroidStatisticsRepository(
			database = get(),
		)
	}
}
