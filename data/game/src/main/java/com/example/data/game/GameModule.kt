package com.example.data.game

import com.example.data.game.mappers.ProtoCellMapper
import com.example.data.game.mappers.ProtoGameMapper
import com.example.data.game.mappers.ProtoGridMapper
import org.koin.dsl.module

val dataGameModule =
	module {
		factory { ProtoGameMapper() }
		factory { ProtoCellMapper() }
		factory { ProtoGridMapper() }

		factory { ProtoGameSerializer() }

		single {
			ProtoGameRepository(
				protoStorageFactory = get(),
				serializer = get(),
				gameMapper = get(),
				cellMapper = get(),
			)
		}
	}
