package com.example.data.game

import com.example.domain.game.repositories.GameRepository
import org.koin.dsl.module

val dataGameModule =
	module {
		factory { ProtoGameSerializer() }

		single<GameRepository> {
			AndroidProtoGameRepository(
				protoStorageFactory = get(),
				serializer = get(),
			)
		}
	}
