package com.example.domain.game.usecases

import org.koin.dsl.module

val domainGameModule =
	module {
		factory { GetGameUseCase(get()) }
		factory { GetElapsedTimeUseCase(get()) }
		factory { SaveGameUseCase(get()) }

		factory { SelectCellUseCase(get(), get(), get(), get()) }
		factory { HighlightMatchingNumbersUseCase() }
		factory { ClearHighlightedNumbersUseCase(get()) }
		factory { HighlightRowAndColumnUseCase() }
		factory { ClearHighlightedRowAndColumnUseCase() }

		factory { InputNumberUseCase() }
	}
