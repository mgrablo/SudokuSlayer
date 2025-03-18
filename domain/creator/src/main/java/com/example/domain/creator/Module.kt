package com.example.domain.creator

import org.koin.dsl.module

val domainCreatorModule =
	module {
		factory { CreateNewGameUseCase() }
	}
