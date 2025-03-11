package com.example.data.core_android

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.data.core.proto.ProtoStorage
import com.example.data.core.proto.ProtoStorageFactory
import com.example.data.core.proto.Serializer
import java.io.InputStream
import java.io.OutputStream
import androidx.datastore.core.Serializer as DataStoreSerializer

class ProtoStorageFactoryImpl(
	private val context: Context,
) : ProtoStorageFactory {
	private val instances = mutableMapOf<String, ProtoStorage<*>>()

	@Suppress("UNCHECKED_CAST")
	override fun <T> createProtoStorage(
		filename: String,
		serializer: Serializer<T>,
	): ProtoStorage<T> =
		instances.getOrPut(filename) {
			val dataStoreSerializer =
				object : DataStoreSerializer<T> {
					override val defaultValue = serializer.defaultValue

					override suspend fun readFrom(input: InputStream): T = serializer.deserialize(input.readBytes())

					override suspend fun writeTo(
						t: T,
						output: OutputStream,
					) {
						val bytes = serializer.serialize(t)
						output.write(bytes)
					}
				}

			val dataStore =
				DataStoreFactory.create(
					serializer = dataStoreSerializer,
					produceFile = { context.dataStoreFile(filename) },
				)
			ProtoStorageImpl(dataStore, serializer.defaultValue)
		} as ProtoStorage<T>
}
