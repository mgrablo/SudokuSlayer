package com.example.data.statistics

import com.example.domain.statistics.StatisticsRepository
import org.koin.dsl.module

val dataStatisticsModule = module {
	single<StatisticsRepository> {
		AndroidStatisticsRepository(
			database = get(),
		)
	}
}
