package com.example.data.core.proto

interface ProtoStorageFactory {
	fun <T> createProtoStorage(
		filename: String,
		serializer: Serializer<T>,
	): ProtoStorage<T>
}
