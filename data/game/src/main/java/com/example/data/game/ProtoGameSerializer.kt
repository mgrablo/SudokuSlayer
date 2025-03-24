package com.example.data.game

import com.example.data.core.proto.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.game.ProtoGame

class ProtoGameSerializer : Serializer<ProtoGame> {
	override val defaultValue: ProtoGame
		get() =
			ProtoGame.getDefaultInstance()

	override suspend fun serialize(data: ProtoGame): ByteArray = data.toByteArray()

	override suspend fun deserialize(data: ByteArray): ProtoGame =
		try {
			ProtoGame.parseFrom(data)
		} catch (e: InvalidProtocolBufferException) {
			throw IllegalArgumentException("Failed to parse ProtoGame", e)
		}
}
