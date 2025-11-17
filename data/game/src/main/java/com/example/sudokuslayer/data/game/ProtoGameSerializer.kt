package com.example.sudokuslayer.data.game

import com.example.sudokuslayer.data.core.proto.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.game.ProtoGame

class ProtoGameSerializer : Serializer<ProtoGame> {
	override val defaultValue: ProtoGame
		get() =
			ProtoGame.getDefaultInstance()

	override suspend fun serialize(value: ProtoGame): ByteArray = value.toByteArray()

	override suspend fun deserialize(data: ByteArray): ProtoGame = try {
		ProtoGame.parseFrom(data)
	} catch (e: InvalidProtocolBufferException) {
		throw IllegalArgumentException("Failed to parse ProtoGame", e)
	}
}
