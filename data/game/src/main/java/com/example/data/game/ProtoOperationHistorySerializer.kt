package com.example.data.game

import com.example.data.core.proto.Serializer
import data.game.ProtoOperationHistory

class ProtoOperationHistorySerializer : Serializer<ProtoOperationHistory> {
	override val defaultValue: ProtoOperationHistory
		get() = ProtoOperationHistory.getDefaultInstance()

	override suspend fun serialize(value: ProtoOperationHistory): ByteArray = value.toByteArray()

	override suspend fun deserialize(data: ByteArray): ProtoOperationHistory = try {
		ProtoOperationHistory.parseFrom(data)
	} catch (e: Exception) {
		throw IllegalArgumentException("Failed to parse ProtoOperationHistory", e)
	}
}
