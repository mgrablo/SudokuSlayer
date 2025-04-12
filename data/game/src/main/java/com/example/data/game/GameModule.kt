package com.example.data.game

import com.example.domain.core.GameRepository
import com.example.domain.game.repositories.GameResultWriter
import com.example.domain.game.repositories.OperationRepository
import org.koin.dsl.module

val dataGameModule =
	module {
		single { ProtoGameSerializer() }
		single { ProtoOperationHistorySerializer() }

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
		single<GameResultWriter> {
			AndroidGameResultWriter(
				database = get(),
			)
		}
	}
