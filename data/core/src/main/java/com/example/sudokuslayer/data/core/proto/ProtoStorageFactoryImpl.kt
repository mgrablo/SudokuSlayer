package com.example.sudokuslayer.data.core.proto

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap
import androidx.datastore.core.Serializer as DataStoreSerializer

class ProtoStorageFactoryImpl(private val context: Context) : ProtoStorageFactory {
	private val instances = ConcurrentHashMap<String, ProtoStorage<*>>()
	private val datastoreLock = Any()

	@Suppress("UNCHECKED_CAST")
	override fun <T> createProtoStorage(filename: String, serializer: Serializer<T>): ProtoStorage<T> =
		instances.computeIfAbsent(filename) {
			val dataStoreSerializer =
				object : DataStoreSerializer<T> {
					override val defaultValue = serializer.defaultValue

					override suspend fun readFrom(input: InputStream): T =
						serializer.deserialize(input.readBytes())

					override suspend fun writeTo(t: T, output: OutputStream) {
						val bytes = serializer.serialize(t)
						output.write(bytes)
					}
				}

			val dataStore = synchronized(datastoreLock) {
				DataStoreFactory.create(
					serializer = dataStoreSerializer,
					produceFile = { context.dataStoreFile(filename) },
				)
			}
			ProtoStorageImpl(dataStore, serializer.defaultValue)
		} as ProtoStorage<T>
}
