package com.example.domain.creator

import org.koin.dsl.module

val domainCreatorModule =
	module {
		factory { CreateNewGameUseCase(get()) }
		factory { GetSavedGameUseCase(get()) }
		factory { SaveGameUseCase(get()) }
		factory { HasActiveGameUseCase(get()) }
	}
