package com.example.sudokuslayer.data.statistics

import com.example.sudokuslayer.domain.statistics.StatisticsRepository
import org.koin.dsl.module

val dataStatisticsModule = module {
	single<StatisticsRepository> {
		AndroidStatisticsRepository(
			database = get(),
		)
	}
}
