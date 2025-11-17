package com.example.sudokuslayer.data.core.proto

interface Serializer<T> {
	val defaultValue: T

	suspend fun serialize(value: T): ByteArray

	suspend fun deserialize(data: ByteArray): T
}
