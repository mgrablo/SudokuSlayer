package com.example.data.game

import com.example.data.core.proto.ProtoStorage
import com.example.data.core.proto.ProtoStorageFactory
import com.example.domain.core.GameRepository
import com.example.domain.core.OperationRepository
import com.example.domain.game.repositories.GameResultWriter
import data.game.ProtoGame
import org.koin.dsl.module

val dataGameModule =
	module {
		single { ProtoGameSerializer() }
		single { ProtoOperationHistorySerializer() }

		single<ProtoStorage<ProtoGame>> {
			val protoStorageFactory: ProtoStorageFactory = get()
			val serializer: ProtoGameSerializer = get()
			protoStorageFactory.createProtoStorage(
				filename = "game.pb",
				serializer = serializer,
			)
		}

		single<GameRepository> {
			AndroidProtoGameRepository(
				protoStorage = get(),
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
