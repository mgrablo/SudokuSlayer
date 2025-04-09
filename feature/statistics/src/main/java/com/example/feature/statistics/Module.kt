package com.example.feature.statistics

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val statisticsModule = module {
	viewModel { StatisticsViewModel() }
}
