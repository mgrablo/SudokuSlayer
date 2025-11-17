package io.github.mgrablo.sudokuslayer.data.game

import io.github.mgrablo.sudokuslayer.data.core.proto.ProtoStorage
import io.github.mgrablo.sudokuslayer.data.core.proto.ProtoStorageFactory
import io.github.mgrablo.sudokuslayer.domain.core.GameRepository
import io.github.mgrablo.sudokuslayer.domain.core.OperationRepository
import io.github.mgrablo.sudokuslayer.domain.game.repositories.GameResultWriter
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
