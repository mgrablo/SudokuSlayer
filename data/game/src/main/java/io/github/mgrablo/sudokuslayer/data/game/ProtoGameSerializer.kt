package io.github.mgrablo.sudokuslayer.data.game

import com.google.protobuf.InvalidProtocolBufferException
import data.game.ProtoGame
import io.github.mgrablo.sudokuslayer.data.core.proto.Serializer

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
