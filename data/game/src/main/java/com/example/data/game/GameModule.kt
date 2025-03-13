package com.example.data.game

import org.koin.dsl.module

val dataGameModule =
	module {
		factory { ProtoGameSerializer() }

		single {
			ProtoGameRepository(
				protoStorageFactory = get(),
				serializer = get(),
			)
		}
	}
