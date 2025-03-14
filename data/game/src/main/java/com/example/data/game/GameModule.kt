package com.example.data.game

import com.example.domain.game.repositories.GameRepository
import com.example.domain.game.repositories.OperationRepository
import org.koin.dsl.module

val dataGameModule =
	module {
		factory { ProtoGameSerializer() }
		factory { ProtoOperationHistorySerializer() }

		single<GameRepository> {
			AndroidProtoGameRepository(
				protoStorageFactory = get(),
				serializer = get(),
			)
		}
		single<OperationRepository> {
			AndroidProtoOperationRepository(
				protoStorageFactory = get(),
				serializer = get(),
			)
		}
	}
